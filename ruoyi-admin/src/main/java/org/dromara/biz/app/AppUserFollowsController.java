// ===================================================================================
// 模塊: Controller (API 接口)
// 描述: 方案主表 (biz_schemes) 的所有API接口。
// ===================================================================================

// 文件路徑: org/dromara/biz/controller/BizSchemesController.java
package org.dromara.biz.app;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.bo.BizUserFollowsBo;
import org.dromara.biz.domain.dto.FollowSchemeDto;
import org.dromara.biz.domain.vo.BizUserFollowsVo;
import org.dromara.biz.domain.vo.MinimumAmountVo;
import org.dromara.biz.service.IBizSchemeWorkflowService;
import org.dromara.biz.service.IBizUserFollowsService;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用戶跟投
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/app/userFollows")
@Tag(name = "follow", description = "跟投")
public class AppUserFollowsController extends BaseController {

    private final IBizUserFollowsService bizUserFollowsService;
    private final IBizSchemeWorkflowService schemeWorkflowService;

    /**
     * 查詢我的跟投記錄列表
     */
    @GetMapping("/myList")
    public TableDataInfo<BizUserFollowsVo> myList(BizUserFollowsBo bo, PageQuery pageQuery) {
        return bizUserFollowsService.queryMyPageList(bo, pageQuery);
    }

    /**
     * 跟投方案
     */
    @PostMapping("/follow")
    public R<Void> follow(@Validated @RequestBody FollowSchemeDto dto) {
        schemeWorkflowService.followScheme(dto);
        return R.ok();
    }

    /**
     * 獲取當前用戶最小下注金額
     */
    @GetMapping("/min-bet-amount")
    public R<MinimumAmountVo> getMinBetAmount() {
        MinimumAmountVo minimumAmountVo = new MinimumAmountVo();
        minimumAmountVo.setMinimumBetAmount(bizUserFollowsService.getMinimumBetAmount());
        return R.ok(minimumAmountVo);
    }

}
