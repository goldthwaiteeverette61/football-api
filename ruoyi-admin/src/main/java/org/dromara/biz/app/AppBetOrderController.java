package org.dromara.biz.app;

import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.bo.BizBetOrderDetailsBo;
import org.dromara.biz.domain.bo.BizBetOrdersBo;
import org.dromara.biz.domain.dto.BetOrderDto;
import org.dromara.biz.domain.vo.BizBetOrdersVo;
import org.dromara.biz.service.IBetPlacementService;
import org.dromara.biz.service.IBizBetOrderDetailsService;
import org.dromara.biz.service.IBizBetOrdersService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 投注
 *
 * @author Lion Li
 * @date 2025-10-08
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/app/betOrder")
public class AppBetOrderController extends BaseController {

    // 【新增】注入投注流程服务
    private final IBetPlacementService betPlacementService;
    private final IBizBetOrdersService bizBetOrdersService;
    private final IBizBetOrderDetailsService iBizBetOrderDetailsService;

    /**
     * 【新增】提交投注订单
     */
    @Log(title = "提交投注订单", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping("/placeOrder")
    public R<Void> placeOrder(@Validated @RequestBody BetOrderDto betOrderDto) {
        betPlacementService.placeBetOrder(betOrderDto);
        return R.ok("投注成功");
    }

    /**
     * 【新增】查询当前用户的投注订单列表
     * 訂單狀態: pending-待開獎, won-已中獎, lost-未中獎, void-作廢
     */
    @GetMapping("/list")
    public TableDataInfo<BizBetOrdersVo> list(BizBetOrdersBo bo, PageQuery pageQuery) {
        // 关键：自动将查询范围限定为当前登录用户
        bo.setUserId(LoginHelper.getUserId());
        TableDataInfo<BizBetOrdersVo> bizBetOrdersVoTableDataInfo = bizBetOrdersService.queryPageList(bo, pageQuery);
        bizBetOrdersVoTableDataInfo.getRows().forEach(t -> {
            t.setBizBetOrderDetailsVos(iBizBetOrderDetailsService.queryListF1(new BizBetOrderDetailsBo().setOrderId(t.getOrderId())));
        });
        return bizBetOrdersVoTableDataInfo;
    }
}
