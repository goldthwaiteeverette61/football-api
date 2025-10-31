package org.dromara.biz.app;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.vo.SchemeDashboardVo;
import org.dromara.biz.service.IBizDashboardService;
import org.dromara.common.core.domain.R;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 理賠金+跟投金額等
 *
 * @author Lion Li
 * @date 2025-08-09
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/app/dashboard")
@Tag(name = "dashboard", description = "dashboard")
public class AppDashboardController extends BaseController {

    private final IBizDashboardService dashboardService;

    /**
     * 數據摘要
     */
    @GetMapping("/scheme-summary")
    public R<SchemeDashboardVo> getSchemeSummary() {
        return R.ok(dashboardService.getSchemeDashboardData());
    }
}
