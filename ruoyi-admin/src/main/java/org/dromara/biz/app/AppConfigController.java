package org.dromara.biz.app;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.vo.ConfigVo;
import org.dromara.common.core.domain.R;
import org.dromara.common.web.core.BaseController;
import org.dromara.system.service.ISysConfigService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * 配置
 *
 * @author Lion Li
 * @date 2025-08-09
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/app/config")
@Tag(name = "config", description = "config")
public class AppConfigController extends BaseController {

    private final ISysConfigService iSysConfigService;

    /**
     * 配置
     */
    @GetMapping("/configs")
    public R<ConfigVo> getConfigs() {
        ConfigVo configVo = new ConfigVo();
        configVo.setBaseBetAmount(iSysConfigService.baseBetAmount());
        configVo.setLossesThresholdForReward(iSysConfigService.lossesThresholdForReward());
        configVo.setWithdrawalFee(new BigDecimal(iSysConfigService.selectConfigByKey("withdrawalFee")));
        configVo.setWithdrawalMin(iSysConfigService.selectConfigByKey("sys.biz.withdrawalMin"));
        return R.ok(configVo);
    }
}
