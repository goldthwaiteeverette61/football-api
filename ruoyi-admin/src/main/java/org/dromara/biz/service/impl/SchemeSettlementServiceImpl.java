package org.dromara.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.biz.domain.BizSchemePeriods;
import org.dromara.biz.domain.bo.BizMatchResultsBo;
import org.dromara.biz.domain.bo.BizSchemePeriodsBo;
import org.dromara.biz.domain.bo.BizTransactionsBo;
import org.dromara.biz.domain.bo.BizUserFollowsBo;
import org.dromara.biz.domain.vo.*;
import org.dromara.biz.service.*;
import org.dromara.system.service.ISysUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class SchemeSettlementServiceImpl implements ISchemeSettlementService {

    private final IBizSchemePeriodsService bizSchemePeriodsService;
    private final IBizSchemePeriodDetailsService bizSchemePeriodDetailsService;
    private final IBizUserFollowsService bizUserFollowsService;
    private final IBizUserFollowDetailsService bizUserFollowDetailsService;
    private final IBizMatchesService bizMatchesService;
    private final IBizMatchResultsService bizMatchResultsService;
    private final ISysUserService sysUserService;
    private final IBizSystemReserveSummaryService bizSystemReserveSummaryService;
    private final IBizUserProgressService bizUserProgressService;
    private final IBizTransactionsService bizTransactionsService;

    // 辅助内部类，用于传递中奖信息
    @Data
    @AllArgsConstructor
    private static class WinningInfo {
        private List<List<BizSchemePeriodDetailsVo>> winningCombinations;
        private int totalCombinationCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void settlePeriod(BizSchemePeriodsVo period) {
        List<BizSchemePeriodDetailsVo> originalDetails = bizSchemePeriodDetailsService.queryByPeriodId(period.getPeriodId());
        if (CollUtil.isEmpty(originalDetails)) {
            log.warn("期数ID: {} 缺少原始方案详情，无法进行结算。", period.getPeriodId());
            updatePeriodStatus(period.getPeriodId(), false);
            return;
        }

        if (!areAllMatchesInSchemeSettled(originalDetails)) {
            log.info("期数ID: {} 的原始方案中尚有比赛未结算，本次跳过。", period.getPeriodId());
            return;
        }

        WinningInfo winningInfo = determineWinningCombinations(originalDetails);
        boolean isPeriodWon = !winningInfo.getWinningCombinations().isEmpty();

        BizUserFollowsBo followQuery = new BizUserFollowsBo();
        followQuery.setPeriodId(period.getPeriodId());
        followQuery.setStatus("bought");
        List<BizUserFollowsVo> followers = bizUserFollowsService.queryList(followQuery);

        for (BizUserFollowsVo follower : followers) {
            settleFollowerBetOptimized(follower, isPeriodWon, winningInfo);
        }

        updatePeriodStatus(period.getPeriodId(), isPeriodWon);
    }

    private WinningInfo determineWinningCombinations(List<BizSchemePeriodDetailsVo> originalDetails) {
        Map<Long, List<BizSchemePeriodDetailsVo>> selectionsByMatch = originalDetails.stream()
            .collect(Collectors.groupingBy(BizSchemePeriodDetailsVo::getMatchId));

        List<List<BizSchemePeriodDetailsVo>> allCombinations = generateCombinations(new ArrayList<>(selectionsByMatch.values()));

        List<List<BizSchemePeriodDetailsVo>> winning = allCombinations.stream()
            .filter(combo -> combo.stream().allMatch(this::isOriginalSelectionCorrect))
            .collect(Collectors.toList());

        return new WinningInfo(winning, allCombinations.size());
    }

    private void settleFollowerBetOptimized(BizUserFollowsVo follower, boolean isPeriodWon, WinningInfo winningInfo) {
        if (isPeriodWon) {
            List<BizUserFollowDetailsVo> followerDetails = bizUserFollowDetailsService.queryByFollowId(follower.getFollowId());
            if (CollUtil.isEmpty(followerDetails)) {
                log.warn("跟投ID: {} 缺少投注详情快照，无法计算派彩。", follower.getFollowId());
                updateFollowerStatus(follower.getFollowId(), "lost");
                return;
            }
            payoutForFollowerOptimized(follower, followerDetails, winningInfo.getWinningCombinations(), winningInfo.getTotalCombinationCount());
            bizUserProgressService.resetUserLosses(follower.getUserId());
        } else {
            bizUserProgressService.incrementUserLosses(follower.getUserId(), follower);
        }
        updateFollowerStatus(follower.getFollowId(), isPeriodWon ? "won" : "lost");
    }

    private void payoutForFollowerOptimized(BizUserFollowsVo follower, List<BizUserFollowDetailsVo> followerDetails, List<List<BizSchemePeriodDetailsVo>> winningMasterCombinations, int totalCombinationCount) {
        // 【核心修改】使用 periodDetailsId 作为 Key
        Map<Long, BigDecimal> followerOddsMap = followerDetails.stream()
            .collect(Collectors.toMap(
                BizUserFollowDetailsVo::getPeriodDetailsId,
                BizUserFollowDetailsVo::getOdds,
                (o1, o2) -> o1
            ));

        if (totalCombinationCount == 0) totalCombinationCount = 1;

        BigDecimal stakePerCombination = follower.getBetAmount().divide(new BigDecimal(totalCombinationCount), 2, RoundingMode.DOWN);
        BigDecimal maxPayout = BigDecimal.ZERO;

        for (List<BizSchemePeriodDetailsVo> winningCombo : winningMasterCombinations) {
            BigDecimal comboOddsForFollower = BigDecimal.ONE;
            boolean allOddsFound = true;
            for (BizSchemePeriodDetailsVo masterDetail : winningCombo) {
                // 【核心修改】通过原始方案的 detailId (即 periodDetailsId) 来查找用户自己的赔率
                BigDecimal followerOdd = followerOddsMap.get(masterDetail.getDetailId());
                if (followerOdd == null) {
                    log.error("严重错误：在跟投ID {} 的快照中找不到中奖项详情ID {} 的赔率！", follower.getFollowId(), masterDetail.getDetailId());
                    allOddsFound = false;
                    break;
                }
                comboOddsForFollower = comboOddsForFollower.multiply(followerOdd);
            }

            if (allOddsFound) {
                BigDecimal currentPayout = stakePerCombination.multiply(comboOddsForFollower);
                maxPayout = maxPayout.max(currentPayout);
            }
        }
        maxPayout = maxPayout.setScale(2, RoundingMode.DOWN);

        // --- 【核心修改】根据投注类型计算佣金 ---
        BigDecimal commission = BigDecimal.ZERO;
        BizUserProgressVo progress = bizUserProgressService.findByUserId(follower.getUserId());
        BigDecimal consecutiveLossesAmount = (progress.getConsecutiveLossesAmount() != null)
            ? progress.getConsecutiveLossesAmount()
            : BigDecimal.ZERO;
        consecutiveLossesAmount = consecutiveLossesAmount.add(progress.getBetAmount());

        BigDecimal commissionBase = maxPayout.subtract(consecutiveLossesAmount);
        if (commissionBase.compareTo(BigDecimal.ZERO) > 0) {
            // 佣金 = (奖金 - 连输金额) * 0.5
            commission = commissionBase.multiply(progress.getCommissionRate()).setScale(2, RoundingMode.DOWN);
        }

        BigDecimal finalPayout = maxPayout.subtract(commission);
        BizUserFollowsBo followUpdateBo = new BizUserFollowsBo();
        followUpdateBo.setFollowId(follower.getFollowId());
        followUpdateBo.setPayoutAmount(finalPayout);
        bizUserFollowsService.updateByBo(followUpdateBo);

        if (finalPayout.compareTo(BigDecimal.ZERO) > 0) {
            sysUserService.addBalance(follower.getUserId(), finalPayout);
            BizTransactionsBo transactionBo = new BizTransactionsBo();
            transactionBo.setUserId(follower.getUserId());
            transactionBo.setAmount(finalPayout);
            transactionBo.setTransactionType("BONUS");
            transactionBo.setStatus("CONFIRMED");
            transactionBo.setSourceId(follower.getFollowId().toString());
            transactionBo.setRemarks("方案跟投中奖派彩");
            bizTransactionsService.insertByBo(transactionBo);
        }
        if (commission.compareTo(BigDecimal.ZERO) > 0) {
            bizSystemReserveSummaryService.addReserveAmount(commission);
        }
    }

    private boolean areAllMatchesInSchemeSettled(List<BizSchemePeriodDetailsVo> originalDetails) {
        List<Long> allMatchIds = originalDetails.stream()
            .map(BizSchemePeriodDetailsVo::getMatchId)
            .distinct()
            .collect(Collectors.toList());
        if (CollUtil.isEmpty(allMatchIds)) return false;
        return allMatchIds.stream()
            .map(bizMatchesService::queryById)
            .allMatch(m -> m != null && "Payout".equalsIgnoreCase(m.getStatus()));
    }

    private boolean isOriginalSelectionCorrect(BizSchemePeriodDetailsVo detail) {
        BizMatchResultsBo resultsQuery = new BizMatchResultsBo();
        resultsQuery.setMatchId(detail.getMatchId());
        resultsQuery.setPoolCode(detail.getPoolCode());
        List<BizMatchResultsVo> results = bizMatchResultsService.queryList(resultsQuery);
        if (CollUtil.isEmpty(results)) {
            log.warn("无法找到比赛ID: {} 的玩法 {} 的官方赛果。", detail.getMatchId(), detail.getPoolCode());
            return false;
        }
        return detail.getSelection().equalsIgnoreCase(results.get(0).getCombination());
    }

    private List<List<BizSchemePeriodDetailsVo>> generateCombinations(List<List<BizSchemePeriodDetailsVo>> selectionsByMatch) {
        List<List<BizSchemePeriodDetailsVo>> result = new ArrayList<>();
        backtrack(0, new ArrayList<>(), selectionsByMatch, result);
        return result;
    }

    private void backtrack(int index, List<BizSchemePeriodDetailsVo> currentCombo, List<List<BizSchemePeriodDetailsVo>> selectionsByMatch, List<List<BizSchemePeriodDetailsVo>> result) {
        if (index == selectionsByMatch.size()) {
            result.add(new ArrayList<>(currentCombo));
            return;
        }
        for (BizSchemePeriodDetailsVo selection : selectionsByMatch.get(index)) {
            currentCombo.add(selection);
            backtrack(index + 1, currentCombo, selectionsByMatch, result);
            currentCombo.remove(currentCombo.size() - 1);
        }
    }

    private void updatePeriodStatus(Long periodId, boolean isWon) {
        String resultStatus = isWon ? "won" : "lost";

        // 1. 【重构】全局查找上一期已结算的记录
        LambdaQueryWrapper<BizSchemePeriods> lqw = Wrappers.lambdaQuery(BizSchemePeriods.class)
            .lt(BizSchemePeriods::getPeriodId, periodId)
            .in(BizSchemePeriods::getStatus, "won", "lost")
            .orderByDesc(BizSchemePeriods::getPeriodId)
            .last("LIMIT 1");
        List<BizSchemePeriodsVo> lastPeriods = bizSchemePeriodsService.queryList(lqw);
        BizSchemePeriodsVo lastPeriod = CollUtil.isNotEmpty(lastPeriods) ? lastPeriods.get(0) : null;

        // 2. 计算本期的“连黑次数”
        int currentLostStreak = 0;
        if ("lost".equals(resultStatus)) {
            // 如果本期黑了，就在上一期的基础上+1
            currentLostStreak = (lastPeriod != null && lastPeriod.getLostStreakSinceLastWin() != null ? lastPeriod.getLostStreakSinceLastWin() : 0) + 1;
        }
        // 如果本期红了，连黑次数自动归零

        // 3. 更新本期记录，包含状态和统计信息
        BizSchemePeriodsBo periodUpdateBo = new BizSchemePeriodsBo();
        periodUpdateBo.setPeriodId(periodId);
        periodUpdateBo.setStatus(resultStatus);
        periodUpdateBo.setResultTime(new Date());
        periodUpdateBo.setLostStreakSinceLastWin(currentLostStreak); // 设置新计算的连黑次数
        bizSchemePeriodsService.updateByBo(periodUpdateBo);

        log.info("periodId: {} 结算完成，状态: {}, 连黑次数更新为: {}", periodId, resultStatus, currentLostStreak);
    }

    private void updateFollowerStatus(Long followId, String resultStatus) {
        BizUserFollowsBo followUpdateBo = new BizUserFollowsBo();
        followUpdateBo.setFollowId(followId);
        followUpdateBo.setStatus("settled");
        followUpdateBo.setResultStatus(resultStatus);
        bizUserFollowsService.updateByBo(followUpdateBo);
    }




}
