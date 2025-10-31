package org.dromara.biz.service.impl;

import com.github.houbb.opencc4j.util.ZhConverterUtil;
import org.dromara.biz.domain.bo.BizLeaguesBo;
import org.dromara.biz.domain.bo.BizMatchesBo;
import org.dromara.biz.domain.bo.BizTeamsBo;
import org.dromara.biz.domain.vo.BizLeaguesVo;
import org.dromara.biz.domain.vo.BizMatchesVo;
import org.dromara.biz.domain.vo.BizTeamsVo;
import org.dromara.biz.service.IBizLeaguesService;
import org.dromara.biz.service.IBizMatchesService;
import org.dromara.biz.service.IBizTeamsService;
import org.dromara.test.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 数据迁移服务集成测试
 *
 * @description: 该测试使用H2内存数据库来验证简繁转换的数据库操作是否正确。
 * @author Lion Li
 */
@Transactional
@DisplayName("数据迁移服务集成测试")
public class DataMigrationServiceImplIT extends BaseIntegrationTest {

    @Autowired
    private DataMigrationServiceImpl dataMigrationService;

    @Autowired
    private IBizLeaguesService bizLeaguesService;

    @Autowired
    private IBizTeamsService bizTeamsService;

    @Autowired
    private IBizMatchesService bizMatchesService;

    // --- 测试常量定义 ---
    private static final String leagueNameSimp = "欧洲冠军联赛";
    private static final String teamNameSimp1 = "皇家马德里";
    private static final String teamNameSimp2 = "拜仁慕尼黑";
    private static final String matchNameSimp = "皇家马德里 vs 拜仁慕尼黑";

    private static final String leagueNameTrad = ZhConverterUtil.toTraditional(leagueNameSimp);
    private static final String teamNameTrad1 = ZhConverterUtil.toTraditional(teamNameSimp1);
    private static final String teamNameTrad2 = ZhConverterUtil.toTraditional(teamNameSimp2);
    private static final String teamNameTrad = ZhConverterUtil.toTraditional(matchNameSimp);

    private static final String leagueId = "L001";
    private static final Long teamId1 = 1001L;
    private static final Long teamId2 = 1002L;
    private static final Long matchId = 2001L;

    /**
     * 在每个测试方法执行前，准备好测试数据
     */
    @BeforeEach
    void setUp() {
        // 插入一条简体联赛数据
        BizLeaguesBo leagueBo = new BizLeaguesBo();
        leagueBo.setLeagueId(leagueId);
        leagueBo.setName(leagueNameSimp);
        leagueBo.setAbbrName(leagueNameSimp);
        bizLeaguesService.insertByBo(leagueBo);

        // 插入两条简体球队数据
        BizTeamsBo teamBo1 = new BizTeamsBo();
        teamBo1.setTeamId(teamId1);
        teamBo1.setFullName(teamNameSimp1);
        teamBo1.setAbbrName(teamNameSimp1);
        bizTeamsService.insertByBo(teamBo1);

        BizTeamsBo teamBo2 = new BizTeamsBo();
        teamBo2.setTeamId(teamId2);
        teamBo2.setFullName(teamNameSimp2);
        teamBo2.setAbbrName(teamNameSimp2);
        bizTeamsService.insertByBo(teamBo2);

        // 插入一条简体比赛数据
        BizMatchesBo matchBo = new BizMatchesBo();
        matchBo.setMatchId(matchId);
        matchBo.setLeagueId(leagueId);
        matchBo.setHomeTeamId(teamId1);
        matchBo.setAwayTeamId(teamId2);
        matchBo.setMatchName(matchNameSimp);
        matchBo.setHomeTeamName(teamNameSimp1);
        matchBo.setAwayTeamName(teamNameSimp2);
        bizMatchesService.insertByBo(matchBo);
    }

    @Test
    @DisplayName("【核心场景】将数据库中简体数据成功转换为繁体")
    void testConvertExistingDataToTraditional() {
        // --- 1. Arrange (安排) ---
        // 数据已在 @BeforeEach 的 setUp 方法中准备就绪。

        // --- 2. Act (执行) ---
        dataMigrationService.convertExistingDataToTraditional();

        // --- 3. Assert (断言) ---
        // 验证联赛
        BizLeaguesVo updatedLeague = bizLeaguesService.queryById(leagueId);
        assertNotNull(updatedLeague, "联赛记录不应为空");
        assertEquals(leagueNameTrad, updatedLeague.getName(), "联赛名称应被转换为繁体");
        assertEquals(leagueNameTrad, updatedLeague.getAbbrName(), "联赛简称应被转换为繁体");

        // 验证球队
        BizTeamsVo updatedTeam1 = bizTeamsService.queryById(teamId1);
        assertNotNull(updatedTeam1, "球队1记录不应为空");
        assertEquals(teamNameTrad1, updatedTeam1.getFullName(), "球队1全称应被转换为繁体");
        assertEquals(teamNameTrad1, updatedTeam1.getAbbrName(), "球队1简称应被转换为繁体");

        BizTeamsVo updatedTeam2 = bizTeamsService.queryById(teamId2);
        assertNotNull(updatedTeam2, "球队2记录不应为空");
        assertEquals(teamNameTrad2, updatedTeam2.getFullName(), "球队2全称应被转换为繁体");
        assertEquals(teamNameTrad2, updatedTeam2.getAbbrName(), "球队2简称应被转换为繁体");

        // 验证比赛
        BizMatchesVo updatedMatch = bizMatchesService.queryById(matchId);
        assertNotNull(updatedMatch, "比赛记录不应为空");
        assertEquals(teamNameTrad, updatedMatch.getMatchName(), "比赛名称应被转换为繁体");
        assertEquals(teamNameTrad1, updatedMatch.getHomeTeamName(), "比赛主队名称应被转换为繁体");
        assertEquals(teamNameTrad2, updatedMatch.getAwayTeamName(), "比赛客队名称应被转换为繁体");
    }
}

