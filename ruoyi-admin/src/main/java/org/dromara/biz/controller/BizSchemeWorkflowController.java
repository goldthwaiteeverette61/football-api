package org.dromara.biz.controller;

import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.dto.UpdatePeriodDetailsDto;
import org.dromara.biz.domain.dto.UpdatePeriodDto;
import org.dromara.biz.service.IBizSchemeWorkflowService;
import org.dromara.biz.service.ICalculatorService;
import org.dromara.common.core.domain.R;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/biz/scheme")
public class BizSchemeWorkflowController extends BaseController {

    private final ICalculatorService iCalculatorService;

    /**
     * 更新方案某一期的投注内容
     */
    @PutMapping("/period/updateDetails")
    public R<Void> updatePeriodDetails(@Validated @RequestBody UpdatePeriodDto dto) {
        iCalculatorService.updatePeriodDetails(dto);
        return R.ok();
    }
}
