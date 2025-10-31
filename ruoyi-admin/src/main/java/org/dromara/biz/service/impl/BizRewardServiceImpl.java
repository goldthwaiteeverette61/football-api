package org.dromara.biz.service.impl;

import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.bo.BizRewardClaimBo;
import org.dromara.biz.domain.vo.BizSchemePeriodsVo;
import org.dromara.biz.domain.vo.BizUserProgressVo;
import org.dromara.biz.service.IBizRewardClaimService;
import org.dromara.biz.service.IBizRewardService;
import org.dromara.biz.service.IBizSchemePeriodsService;
import org.dromara.biz.service.IBizUserProgressService;
import org.dromara.common.core.domain.model.LoginUser;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.system.service.ISysUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BizRewardServiceImpl implements IBizRewardService {

    private final IBizUserProgressService bizUserProgressService;
    private final IBizSchemePeriodsService bizSchemePeriodsService;
    private final IBizRewardClaimService iBizRewardClaimService;
    private final ISysUserService iSysUserService;
    private static final String COMPENSATION_CODE_PREFIX = "CP";
    private static final String REDIS_KEY_PREFIX = "biz:code:compensation:";

    @Override
    public String generateCompensationCode() {
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String redisKey = REDIS_KEY_PREFIX + currentDate;

        // 2. 使用 Redis 的 INCR 命令獲取當天的自增序列號，確保原子性和唯一性
        Long sequence = RedisUtils.incrAtomicValue(redisKey);

        // 3. 首次生成時，為該key設定24小時的過期時間，自動清理
        if (sequence != null && sequence == 1L) {
            RedisUtils.expire(redisKey, 60*60*24);
        }

        // 4. 將序列號格式化為4位，不足則在前面補0
        String formattedSequence = String.format("%04d", sequence);

        // 5. 拼接成最終的業務編碼
        return COMPENSATION_CODE_PREFIX + currentDate + formattedSequence;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void claimRewardForCurrentUser(String payPassword) {
        LoginUser loginUser = LoginHelper.getLoginUser();
        Long userId = loginUser.getUserId();
        String username = loginUser.getUsername();

        // 【核心新增】在執行操作前，先進行支付密碼校驗
        iSysUserService.verifyPayPassword(userId, payPassword);

        // 1. 檢查用戶是否有資格領取，並直接獲取連輸數據
        BizUserProgressVo progress = bizUserProgressService.findByUserId(userId);

        if (progress.getBetAmount() != null && progress.getBetAmount().compareTo(BigDecimal.ZERO) == 1) {
            throw new ServiceException("当前正在投注中，不允许此项操作");
        }

        if (progress.getCanClaimReward() == null || progress.getCanClaimReward() != 1) {
            throw new ServiceException("您当前不满足领取条件");
        }

        // 【核心新增】校驗投注模式 (bet_type 必須為 double)
        if (!"double".equalsIgnoreCase(progress.getBetType())) {
            throw new ServiceException("普通模式无法申请理赔金");
        }

        // 2. 核心優化：直接從進度表獲取理賠金額
        BigDecimal rewardAmount = progress.getConsecutiveLossesAmount();

        if (rewardAmount == null || rewardAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("理赔金额不足，无法申请");
        }

        // 3. 獲取理賠相關的方案期數詳情，用於生成詳細備註
        BizSchemePeriodsVo lastWonPeriod = bizSchemePeriodsService.findLastWonPeriodByUserId(userId);
        Long lastWonPeriodId = (lastWonPeriod != null) ? lastWonPeriod.getPeriodId() : 0L;
        List<BizSchemePeriodsVo> losingPeriods = bizSchemePeriodsService.findLostPeriodsAfter(userId, lastWonPeriodId);
        String periodDetails = "无";
        if (losingPeriods != null && !losingPeriods.isEmpty()) {
            periodDetails = losingPeriods.stream()
                .map(p -> p.getName())
                .collect(Collectors.joining(", "));
        }

        // 4. 建立新的理賠申請記錄
        BizRewardClaimBo claimBo = new BizRewardClaimBo();

        // 【核心新增】生成並設定業務編碼
        String bizCode = generateCompensationCode();
        claimBo.setBizCode(bizCode);
        claimBo.setUserId(userId);
        claimBo.setAmount(rewardAmount);
        claimBo.setUserName(username);
        claimBo.setLostCount(progress.getConsecutiveLosses());
        claimBo.setCurrency("USDT");
        claimBo.setStatus("PENDING"); // 狀態設定為 "待審核"

        // 核心修改：優化備註資訊
        String lastWinText = (lastWonPeriodId > 0L) ? String.format("上次盈利期數ID: %d。 ", lastWonPeriodId) : "";
        String remarks = String.format("申请领取连输%d场理赔金。%s涉及方案: [%s]",
            progress.getConsecutiveLosses(), lastWinText, periodDetails);
        claimBo.setRemarks(remarks);
        iBizRewardClaimService.insertByBo(claimBo);
        bizUserProgressService.resetUserLosses(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetConsecutiveLossesForCurrentUser(String payPassword) {
        LoginUser loginUser = LoginHelper.getLoginUser();
        Long userId = loginUser.getUserId();
        String username = loginUser.getUsername();

        // 【核心新增】在執行操作前，先進行支付密碼校驗
        iSysUserService.verifyPayPassword(userId, payPassword);
        BizUserProgressVo progress = bizUserProgressService.findByUserId(userId);

        if (progress.getBetAmount() != null && progress.getBetAmount().compareTo(BigDecimal.ZERO) == 1) {
            throw new ServiceException("当前正在投注中，不允许此项操作");
        }

        if (progress.getConsecutiveLosses() == 0) {
            throw new ServiceException("当前没有连输记录，无需重置");
        }

        // 【核心修改】獲取相關的方案期數詳情，用於生成詳細備註
        BizSchemePeriodsVo lastWonPeriod = bizSchemePeriodsService.findLastWonPeriodByUserId(userId);
        Long lastWonPeriodId = (lastWonPeriod != null) ? lastWonPeriod.getPeriodId() : 0L;
        List<BizSchemePeriodsVo> losingPeriods = bizSchemePeriodsService.findLostPeriodsAfter(userId, lastWonPeriodId);
        String periodDetails = "无";
        if (losingPeriods != null && !losingPeriods.isEmpty()) {
            periodDetails = losingPeriods.stream()
                .map(BizSchemePeriodsVo::getName)
                .collect(Collectors.joining(", "));
        }

        // 為用戶手動重置操作建立一條留底記錄
        BizRewardClaimBo claimBo = new BizRewardClaimBo();
        claimBo.setUserId(userId);
        claimBo.setBizCode(generateCompensationCode());
        claimBo.setAmount(progress.getConsecutiveLossesAmount());
        claimBo.setCurrency("USDT");
        claimBo.setUserName(username);
        claimBo.setLostCount(progress.getConsecutiveLosses());
        claimBo.setStatus("RESET"); // 自動批准，因為它只是一個日誌記錄

        // 【核心修改】更新備註資訊，加入方案詳情
        String remarks = String.format("用户手动重置连输记录。重置前连输 %d 场，累计金额 %s。涉及方案: [%s]",
            progress.getConsecutiveLosses(), progress.getConsecutiveLossesAmount().toPlainString(), periodDetails);
        claimBo.setRemarks(remarks);
        iBizRewardClaimService.insertByBo(claimBo);

        bizUserProgressService.resetUserLosses(userId);
    }

}
