package org.dromara.biz.app;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.bo.BizSystemReserveSummaryBo;
import org.dromara.biz.domain.vo.BizSystemReserveSummaryVo;
import org.dromara.biz.service.IBizSystemReserveSummaryService;
import org.dromara.common.core.domain.R;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 理賠金
 *
 * @author Lion Li
 * @date 2025-08-09
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/app/Bets")
@Tag(name = "bet", description = "倍投")
public class AppBetsController extends BaseController {

    private final IBizSystemReserveSummaryService bizSystemReserveSummaryService;

    /**
     * 理賠金
     *
     */
    @GetMapping("/reserve")
    public R<BizSystemReserveSummaryVo> getInfo() {
        List<BizSystemReserveSummaryVo> bizSystemReserveSummaryVos = bizSystemReserveSummaryService.queryList(new BizSystemReserveSummaryBo());
        if(bizSystemReserveSummaryVos.size() > 0){
            return R.ok(bizSystemReserveSummaryVos.get(0));
        }
        return R.ok(new BizSystemReserveSummaryVo());
    }

}
