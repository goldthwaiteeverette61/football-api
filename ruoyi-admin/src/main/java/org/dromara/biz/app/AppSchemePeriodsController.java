package org.dromara.biz.app;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.BizSchemePeriods;
import org.dromara.biz.domain.bo.BizSchemePeriodsBo;
import org.dromara.biz.domain.vo.BizSchemePeriodsVo;
import org.dromara.biz.service.IBizSchemePeriodsService;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 方案期數
 *
 * @author Lion Li
 * @date 2025-07-28
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/app/schemePeriods")
@Tag(name = "scheme", description = "投注方案")
public class AppSchemePeriodsController extends BaseController {

    private final IBizSchemePeriodsService bizSchemePeriodsService;

    /**
     * 查詢方案期數列表 (包含詳情)
     */
    @GetMapping("/list")
    public TableDataInfo<BizSchemePeriodsVo> getSchemePeriodsList(BizSchemePeriodsBo bo, PageQuery pageQuery) {
        // 確保狀態爲空，查詢所有
        bo.setExcludeDraft(true);
        bo.setExcludePending(true);
        return bizSchemePeriodsService.queryPageListFull(bo, pageQuery);
    }

    /**
     * 查詢當前進行中的方案，或24小時內最近結束的方案
     */
    @GetMapping("/findActiveOrRecentPeriod")
    public R<BizSchemePeriodsVo> findActiveOrRecentPeriod() {
        return R.ok(bizSchemePeriodsService.findActiveOrRecentPeriod());
    }

    @GetMapping("/pendingList")
    public TableDataInfo<BizSchemePeriodsVo> getSchemePeriodsPendingList(BizSchemePeriodsBo bo, PageQuery pageQuery) {
        // 核心修復：設置查詢條件爲“待開獎”
        bo.setStatus(BizSchemePeriods.STATUS_PENDING);
//        bo.setExcludeDraft(true);
        return bizSchemePeriodsService.queryPageListFull(bo, pageQuery);
    }

    /**
     * 獲取方案看板統計數據
     */
    @GetMapping("/dashboard")
    public R<Map<String, Object>> getDashboardStats() {
        return R.ok(bizSchemePeriodsService.getSchemeDashboardStats());
    }


}
