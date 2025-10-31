package org.dromara.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.biz.domain.bo.BizTeamsBo;
import org.dromara.biz.domain.vo.BizTeamsVo;
import org.dromara.biz.service.IBizTeamsService;
import org.dromara.biz.service.ITeamLogoUpdateService;
import org.dromara.common.core.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 【核心新增】球队Logo更新服务
 *
 * @author Lion Li
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class TeamLogoUpdateServiceImpl implements ITeamLogoUpdateService {

    private final IBizTeamsService bizTeamsService;

    // --- 【核心修改】更新为新的API配置 ---
    // 前置条件:
    // 1. `biz_teams` 表需要增加一个 `code` 字段 (VARCHAR)，用于存储球队的英文代码 (例如 "JAC")。
    //    SQL示例: ALTER TABLE biz_teams ADD COLUMN code VARCHAR(20) NULL COMMENT '球队英文代码 (用于API查询)';
    // 2. `BizTeamsVo` 和 `BizTeamsBo` 类需要同步增加 `code` 属性。
    // 3. 在采集比赛数据时，需要将 `homeTeamAbbEnName`/`awayTeamAbbEnName` 存入 `biz_teams.code` 字段。
    private static final String API_URL = "https://v3.football.api-sports.io/teams";
    private static final String API_KEY = "dbbbacd27e50a5fa80772978a7ceea03"; // 建议将Key配置化
    private static final String API_HOST = "v3.football.api-sports.io";


    /**
     * 【核心重构】更新球队Logo，采用新的API并逐个请求
     */
    @Override
    public void updateTeamLogos() {
        log.info("===== 开始执行球队Logo更新任务 =====");
        // 1. 只获取数据库中 logo 字段为空的球队
        List<BizTeamsVo> teamsToUpdate = bizTeamsService.queryTeamsWithoutLogo();
        if (CollUtil.isEmpty(teamsToUpdate)) {
            log.info("没有需要更新Logo的球队，任务结束。");
            return;
        }

        log.info("共找到 {} 支需要更新Logo的球队，将开始逐个处理。", teamsToUpdate.size());
        int updatedCount = 0;

        // 2. 遍历每一支球队，单独发起请求
        for (BizTeamsVo team : teamsToUpdate) {
            // 【核心修改】确保球队有code才能查询
            if (StringUtils.isBlank(team.getCode())) {
                log.warn("球队ID {} ({}) 的英文代码为空，跳过处理。", team.getTeamId(), team.getAbbrName());
                continue;
            }

            String fullUrl = API_URL + "?code=" + team.getCode();

            try {
                // 3. 发起HTTP请求，并带上请求头
                String body = HttpRequest.get(fullUrl)
                    .header("x-rapidapi-key", API_KEY)
                    .header("x-rapidapi-host", API_HOST)
                    .execute().body();

                JSONObject root = JSONUtil.parseObj(body);

                // 4. 解析返回的JSON对象
                JSONArray responseArray = root.getJSONArray("response");
                if (responseArray == null || responseArray.isEmpty()) {
                    log.warn("Logo API未返回球队 {} 的数据。", team.getCode());
                    continue;
                }

                // 5. 获取Logo并更新数据库
                JSONObject teamInfo = responseArray.getJSONObject(0).getJSONObject("team");
                if (teamInfo != null) {
                    Long teamId = team.getTeamId(); // 使用我们自己库里的ID来更新
                    String logoUrl = teamInfo.getStr("logo");

                    if (StringUtils.isNotBlank(logoUrl)) {
                        BizTeamsBo updateBo = new BizTeamsBo();
                        updateBo.setTeamId(teamId);
                        updateBo.setLogo(logoUrl);
                        bizTeamsService.updateByBo(updateBo);
                        updatedCount++;
                        log.info(" -> 成功更新球队 {} 的Logo。", team.getCode());
                    }
                }

                // 6. 礼貌性地暂停一下，避免请求过于频繁
                Thread.sleep(500); // 避免API调用频率过高

            } catch (Exception e) {
                log.error("处理球队 {} 时发生异常", team.getCode(), e);
            }
        }
        log.info("===== 球队Logo更新任务执行完毕，共更新了 {} 条记录。 =====", updatedCount);
    }
}
