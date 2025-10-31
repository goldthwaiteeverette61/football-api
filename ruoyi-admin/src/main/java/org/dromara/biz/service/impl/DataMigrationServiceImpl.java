package org.dromara.biz.service.impl;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.biz.domain.BizLeagues;
import org.dromara.biz.domain.BizMatches;
import org.dromara.biz.domain.BizSchemePeriodDetails;
import org.dromara.biz.domain.BizTeams;
import org.dromara.biz.domain.BizUserFollowDetails;
import org.dromara.biz.domain.BizUserFollows;
import org.dromara.biz.domain.bo.BizLeaguesBo;
import org.dromara.biz.domain.bo.BizMatchesBo;
import org.dromara.biz.domain.bo.BizSchemePeriodDetailsBo;
import org.dromara.biz.domain.bo.BizTeamsBo;
import org.dromara.biz.domain.bo.BizUserFollowDetailsBo;
import org.dromara.biz.domain.bo.BizUserFollowsBo;
import org.dromara.biz.domain.vo.BizLeaguesVo;
import org.dromara.biz.domain.vo.BizMatchesVo;
import org.dromara.biz.domain.vo.BizSchemePeriodDetailsVo;
import org.dromara.biz.domain.vo.BizTeamsVo;
import org.dromara.biz.domain.vo.BizUserFollowDetailsVo;
import org.dromara.biz.domain.vo.BizUserFollowsVo;
import org.dromara.biz.service.IBizLeaguesService;
import org.dromara.biz.service.IBizMatchesService;
import org.dromara.biz.service.IBizSchemePeriodDetailsService;
import org.dromara.biz.service.IBizTeamsService;
import org.dromara.biz.service.IBizUserFollowDetailsService;
import org.dromara.biz.service.IBizUserFollowsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据迁移服务实现类
 *
 * @author Lion Li
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class DataMigrationServiceImpl {

    private final IBizLeaguesService bizLeaguesService;
    private final IBizTeamsService bizTeamsService;
    private final IBizMatchesService bizMatchesService;
    private final IBizSchemePeriodDetailsService bizSchemePeriodDetailsService;
    private final IBizUserFollowsService bizUserFollowsService;
    private final IBizUserFollowDetailsService bizUserFollowDetailsService;

    @Transactional(rollbackFor = Exception.class)
    public void convertExistingDataToTraditional() {
        log.info("===== 开始执行历史数据简转繁迁移任务 =====");
        convertLeagues();
        convertTeams();
        convertMatches();
        convertSchemeDetails();
        convertFollows();
        convertFollowDetails();
        log.info("===== 历史数据简转繁迁移任务执行完毕 =====");
    }

    private void convertLeagues() {
        log.info(" -> 正在处理联赛表...");
        List<BizLeaguesVo> allLeagues = bizLeaguesService.queryList(new BizLeaguesBo());
        if (allLeagues.isEmpty()) {
            log.info(" -> 联赛表为空，无需处理。");
            return;
        }

        List<BizLeagues> leaguesToUpdate = new ArrayList<>();
        for (BizLeaguesVo league : allLeagues) {
            String originalName = league.getName();
            String originalAbbrName = league.getAbbrName();
            String traditionalName = ZhConverterUtil.toTraditional(originalName);
            String traditionalAbbrName = ZhConverterUtil.toTraditional(originalAbbrName);

            if (!originalName.equals(traditionalName) || !originalAbbrName.equals(traditionalAbbrName)) {
                BizLeagues updateEntity = new BizLeagues();
                updateEntity.setLeagueId(league.getLeagueId());
                updateEntity.setName(traditionalName);
                updateEntity.setAbbrName(traditionalAbbrName);
                leaguesToUpdate.add(updateEntity);
            }
        }

        if (!leaguesToUpdate.isEmpty()) {
            Db.updateBatchById(leaguesToUpdate);
            log.info(" -> 成功转换并更新了 {} 条联赛记录。", leaguesToUpdate.size());
        } else {
            log.info(" -> 联赛表数据已是繁体，无需更新。");
        }
    }

    private void convertTeams() {
        log.info(" -> 正在处理球队表...");
        List<BizTeamsVo> allTeams = bizTeamsService.queryList(new BizTeamsBo());
        if (allTeams.isEmpty()) {
            log.info(" -> 球队表为空，无需处理。");
            return;
        }

        List<BizTeams> teamsToUpdate = new ArrayList<>();
        for (BizTeamsVo team : allTeams) {
            String originalFullName = team.getFullName();
            String originalAbbrName = team.getAbbrName();
            String traditionalFullName = ZhConverterUtil.toTraditional(originalFullName);
            String traditionalAbbrName = ZhConverterUtil.toTraditional(originalAbbrName);

            if (!originalFullName.equals(traditionalFullName) || !originalAbbrName.equals(traditionalAbbrName)) {
                BizTeams updateEntity = new BizTeams();
                updateEntity.setTeamId(team.getTeamId());
                updateEntity.setFullName(traditionalFullName);
                updateEntity.setAbbrName(traditionalAbbrName);
                teamsToUpdate.add(updateEntity);
            }
        }

        if (!teamsToUpdate.isEmpty()) {
            Db.updateBatchById(teamsToUpdate);
            log.info(" -> 成功转换并更新了 {} 条球队记录。", teamsToUpdate.size());
        } else {
            log.info(" -> 球队表数据已是繁体，无需更新。");
        }
    }

    private void convertMatches() {
        log.info(" -> 正在处理比赛表...");
        List<BizMatchesVo> allMatches = bizMatchesService.queryList(new BizMatchesBo());
        if (allMatches.isEmpty()) {
            log.info(" -> 比赛表为空，无需处理。");
            return;
        }

        List<BizMatches> matchesToUpdate = new ArrayList<>();
        for (BizMatchesVo match : allMatches) {
            String originalMatchName = match.getMatchName();
            String originalHomeName = match.getHomeTeamName();
            String originalAwayName = match.getAwayTeamName();
            String mns = match.getMatchNumStr();

            String traditionalMatchName = ZhConverterUtil.toTraditional(originalMatchName);
            String traditionalHomeName = ZhConverterUtil.toTraditional(originalHomeName);
            String traditionalAwayName = ZhConverterUtil.toTraditional(originalAwayName);
            String tmns = ZhConverterUtil.toTraditional(mns);

            if ((mns != null && !mns.equals(tmns)) ||
                (originalMatchName != null && !originalMatchName.equals(traditionalMatchName)) ||
                (originalHomeName != null && !originalHomeName.equals(traditionalHomeName)) ||
                (originalAwayName != null && !originalAwayName.equals(traditionalAwayName))) {
                BizMatches updateEntity = new BizMatches();
                updateEntity.setMatchId(match.getMatchId());
                updateEntity.setMatchName(traditionalMatchName);
                updateEntity.setHomeTeamName(traditionalHomeName);
                updateEntity.setAwayTeamName(traditionalAwayName);
                updateEntity.setMatchNumStr(tmns);
                matchesToUpdate.add(updateEntity);
            }
        }

        if (!matchesToUpdate.isEmpty()) {
            Db.updateBatchById(matchesToUpdate);
            log.info(" -> 成功转换并更新了 {} 条比赛记录。", matchesToUpdate.size());
        } else {
            log.info(" -> 比赛表数据已是繁体，无需更新。");
        }
    }

    private void convertSchemeDetails() {
        log.info(" -> 正在处理方案详情表...");
        List<BizSchemePeriodDetailsVo> allDetails = bizSchemePeriodDetailsService.queryList(new BizSchemePeriodDetailsBo());
        if (allDetails.isEmpty()) {
            log.info(" -> 方案详情表为空，无需处理。");
            return;
        }

        List<BizSchemePeriodDetails> detailsToUpdate = new ArrayList<>();
        for (BizSchemePeriodDetailsVo detail : allDetails) {
            String originalMatchName = detail.getMatchName();
            if (originalMatchName != null) {
                String traditionalMatchName = ZhConverterUtil.toTraditional(originalMatchName);
                if (!originalMatchName.equals(traditionalMatchName)) {
                    BizSchemePeriodDetails updateEntity = new BizSchemePeriodDetails();
                    updateEntity.setDetailId(detail.getDetailId());
                    updateEntity.setMatchName(traditionalMatchName);
                    detailsToUpdate.add(updateEntity);
                }
            }
        }

        if (!detailsToUpdate.isEmpty()) {
            Db.updateBatchById(detailsToUpdate);
            log.info(" -> 成功转换并更新了 {} 条方案详情记录。", detailsToUpdate.size());
        } else {
            log.info(" -> 方案详情表数据已是繁体，无需更新。");
        }
    }

    private void convertFollows() {
        log.info(" -> 正在处理用户跟投记录表...");
        List<BizUserFollowsVo> allFollows = bizUserFollowsService.queryList(new BizUserFollowsBo());
        if (allFollows.isEmpty()) {
            log.info(" -> 用户跟投记录表为空，无需处理。");
            return;
        }

        List<BizUserFollows> followsToUpdate = new ArrayList<>();
        for (BizUserFollowsVo follow : allFollows) {
            String originalDesc = follow.getBetOddsDesc();
            if (originalDesc != null) {
                String traditionalDesc = ZhConverterUtil.toTraditional(originalDesc);
                if (!originalDesc.equals(traditionalDesc)) {
                    BizUserFollows updateEntity = new BizUserFollows();
                    updateEntity.setFollowId(follow.getFollowId());
                    updateEntity.setBetOddsDesc(traditionalDesc);
                    followsToUpdate.add(updateEntity);
                }
            }
        }

        if (!followsToUpdate.isEmpty()) {
            Db.updateBatchById(followsToUpdate);
            log.info(" -> 成功转换并更新了 {} 条用户跟投记录。", followsToUpdate.size());
        } else {
            log.info(" -> 用户跟投记录表数据已是繁体，无需更新。");
        }
    }

    private void convertFollowDetails() {
        log.info(" -> 正在处理用户跟投详情表...");
        List<BizUserFollowDetailsVo> allFollowDetails = bizUserFollowDetailsService.queryList(new BizUserFollowDetailsBo());
        if (allFollowDetails.isEmpty()) {
            log.info(" -> 用户跟投详情表为空，无需处理。");
            return;
        }

        List<BizUserFollowDetails> detailsToUpdate = new ArrayList<>();
        for (BizUserFollowDetailsVo detail : allFollowDetails) {
            String originalMatchName = detail.getMatchName();
            if (originalMatchName != null) {
                String traditionalMatchName = ZhConverterUtil.toTraditional(originalMatchName);
                if (!originalMatchName.equals(traditionalMatchName)) {
                    BizUserFollowDetails updateEntity = new BizUserFollowDetails();
                    updateEntity.setFollowDetailId(detail.getFollowDetailId());
                    updateEntity.setMatchName(traditionalMatchName);
                    detailsToUpdate.add(updateEntity);
                }
            }
        }

        if (!detailsToUpdate.isEmpty()) {
            Db.updateBatchById(detailsToUpdate);
            log.info(" -> 成功转换并更新了 {} 条用户跟投详情记录。", detailsToUpdate.size());
        } else {
            log.info(" -> 用户跟投详情表数据已是繁体，无需更新。");
        }
    }
}

