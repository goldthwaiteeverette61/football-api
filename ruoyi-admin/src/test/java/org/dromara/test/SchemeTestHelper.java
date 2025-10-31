package org.dromara.test.helper;

import org.dromara.biz.domain.bo.UserFollowDetailsSaveBo;
import org.dromara.biz.service.IBizUserFollowsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * 方案與投注相關的測試輔助類
 *
 * @description: 該類別封裝了所有用於準備和驗證方案投注測試場景的公共方法，
 * 以保持測試類別本身的簡潔和專注。
 */
@Profile("test") // 確保該組件只在測試環境下被載入
@Component
public class SchemeTestHelper {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private IBizUserFollowsService bizUserFollowsService;

    /**
     * 準備一個處於「待開獎」狀態的方案及其詳情
     */
    public void setupPendingPeriod(long periodId, long matchId, String selection, String odds) {
        jdbcTemplate.update("INSERT INTO `biz_scheme_periods` (`period_id`, `name`, `status`, `deadline_time`) VALUES (?, '測試方案', 'pending', DATEADD('DAY', 1, NOW()))", periodId);
        jdbcTemplate.update("INSERT INTO `biz_scheme_period_details` (`detail_id`, `period_id`, `match_id`, `pool_code`, `selection`, `odds`) VALUES (?, ?, ?, 'hhad', ?, ?)",
            periodId, periodId, matchId, selection, new BigDecimal(odds)
        );
    }

    /**
     * 根據用戶ID和期數ID獲取跟投記錄ID
     */
    public Long getFollowId(long userId, long periodId) {
        return jdbcTemplate.queryForObject("SELECT follow_id FROM biz_user_follows WHERE user_id = ? AND period_id = ?", Long.class, userId, periodId);
    }

    /**
     * 為一筆跟投記錄儲存其投注詳情快照
     */
    public void saveFollowDetails(Long followId, Long periodId, Long matchId, String selection, String odds) {
        UserFollowDetailsSaveBo saveDetailsBo = new UserFollowDetailsSaveBo();
        saveDetailsBo.setFollowIds(List.of(followId));
        UserFollowDetailsSaveBo.DetailItem detailItem = new UserFollowDetailsSaveBo.DetailItem();
        detailItem.setPeriodId(periodId);
        detailItem.setPeriodDetailsId(periodId);
        detailItem.setMatchId(matchId);
        detailItem.setPoolCode("hhad");
        detailItem.setSelection(selection);
        detailItem.setOdds(new BigDecimal(odds));
        saveDetailsBo.setCombinations(List.of(detailItem));
        bizUserFollowsService.saveFollowDetails(saveDetailsBo);
    }

    /**
     * 將一筆跟投記錄的狀態從 'in_cart' 更新為 'bought'
     */
    public void confirmFollow(long userId, long periodId) {
        jdbcTemplate.update("UPDATE biz_user_follows SET status = 'bought' WHERE user_id = ? AND period_id = ?", userId, periodId);
    }

    /**
     * 將一場比賽的結果結算為「輸」
     */
    public void finalizeMatchAsLoss(long matchId) {
        jdbcTemplate.update("INSERT INTO `biz_match_results` (`match_id`, `pool_code`, `combination`) VALUES (?, 'hhad', 'L')", matchId);
        jdbcTemplate.update("UPDATE biz_matches SET status = 'Payout' WHERE match_id = ?", matchId);
    }

    /**
     * 將一場比賽的結果結算為「贏」
     */
    public void finalizeMatchAsWin(long matchId) {
        jdbcTemplate.update("INSERT INTO `biz_match_results` (`match_id`, `pool_code`, `combination`) VALUES (?, 'hhad', 'W')", matchId);
        jdbcTemplate.update("UPDATE biz_matches SET status = 'Payout' WHERE match_id = ?", matchId);
    }
}

