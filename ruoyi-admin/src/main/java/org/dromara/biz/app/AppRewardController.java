package org.dromara.biz.app;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.bo.BizRewardClaimBo;
import org.dromara.biz.domain.bo.BizUserProgressBo;
import org.dromara.biz.domain.vo.BizRewardClaimVo;
import org.dromara.biz.domain.vo.BizUserProgressVo;
import org.dromara.biz.service.IBizRewardClaimService;
import org.dromara.biz.service.IBizRewardService;
import org.dromara.biz.service.IBizUserProgressService;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.web.core.BaseController;
import org.dromara.system.service.ISysConfigService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 *
 * 理賠金
 *
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/app/reward")
@Tag(name = "reward", description = "理賠金")
public class AppRewardController extends BaseController {

    private final IBizRewardService rewardService;
    private final IBizRewardClaimService bizRewardClaimService;
    private final IBizUserProgressService iBizUserProgressService;
    private final ISysConfigService iSysConfigService;

    @PostMapping("/claim")
    public R<Void> claim(@RequestParam @NotBlank(message = "支付密碼不能爲空") String payPassword) {
        rewardService.claimRewardForCurrentUser(payPassword);
        return R.ok("領取成功");
    }

    /**
     * 重置連輸記錄
     * @return
     */
    @PostMapping("/reset-losses")
    public R<Void> resetLosses(@RequestParam @NotBlank(message = "支付密碼不能爲空") String payPassword) {
        rewardService.resetConsecutiveLossesForCurrentUser(payPassword);
        return R.ok("重置成功");
    }

    /**
     * 查詢理賠申請列表
     */
    @GetMapping("/rewardClaimList")
    public TableDataInfo<BizRewardClaimVo> rewardClaimList(BizRewardClaimBo bo, PageQuery pageQuery) {
        bo.setUserId(LoginHelper.getUserId());
        return bizRewardClaimService.queryPageList(bo, pageQuery);
    }

    /**
     * 查詢理賠狀態
     */
    @GetMapping("/status")
    public R<Boolean> status() {
        BizUserProgressBo bizUserProgressBo = new BizUserProgressBo();
        bizUserProgressBo.setUserId(LoginHelper.getUserId());
        BizUserProgressVo bizUserProgressVo = iBizUserProgressService.findByUserId(LoginHelper.getUserId());

        return R.ok(bizUserProgressVo.getConsecutiveLosses() >= iSysConfigService.lossesThresholdForReward());
    }

}
