package org.dromara.biz.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.dromara.biz.domain.dto.BatchUpdateOddsDto;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.web.core.BaseController;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.biz.domain.vo.BizBetOrdersVo;
import org.dromara.biz.domain.bo.BizBetOrdersBo;
import org.dromara.biz.service.IBizBetOrdersService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 使用者投注訂單
 *
 * @author Lion Li
 * @date 2025-10-08
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/biz/betOrders")
public class BizBetOrdersController extends BaseController {

    private final IBizBetOrdersService bizBetOrdersService;

    /**
     * 查询使用者投注訂單列表
     */
    @SaCheckPermission("biz:betOrders:list")
    @GetMapping("/list")
    public TableDataInfo<BizBetOrdersVo> list(BizBetOrdersBo bo, PageQuery pageQuery) {
        return bizBetOrdersService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出使用者投注訂單列表
     */
    @SaCheckPermission("biz:betOrders:export")
    @Log(title = "使用者投注訂單", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BizBetOrdersBo bo, HttpServletResponse response) {
        List<BizBetOrdersVo> list = bizBetOrdersService.queryList(bo);
        ExcelUtil.exportExcel(list, "使用者投注訂單", BizBetOrdersVo.class, response);
    }

    /**
     * 获取使用者投注訂單详细信息
     *
     * @param orderId 主键
     */
    @SaCheckPermission("biz:betOrders:query")
    @GetMapping("/{orderId}")
    public R<BizBetOrdersVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long orderId) {
        return R.ok(bizBetOrdersService.queryById(orderId));
    }

    /**
     * 新增使用者投注訂單
     */
    @SaCheckPermission("biz:betOrders:add")
    @Log(title = "使用者投注訂單", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizBetOrdersBo bo) {
        return toAjax(bizBetOrdersService.insertByBo(bo));
    }

    /**
     * 修改使用者投注訂單
     */
    @SaCheckPermission("biz:betOrders:edit")
    @Log(title = "使用者投注訂單", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizBetOrdersBo bo) {
        return toAjax(bizBetOrdersService.updateByBo(bo));
    }

    /**
     * 删除使用者投注訂單
     *
     * @param orderIds 主键串
     */
    @SaCheckPermission("biz:betOrders:remove")
    @Log(title = "使用者投注訂單", businessType = BusinessType.DELETE)
    @DeleteMapping("/{orderIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] orderIds) {
        return toAjax(bizBetOrdersService.deleteWithValidByIds(List.of(orderIds), true));
    }

    /**
     * 【新增】批量修改订单详情赔率
     */
    @SaCheckPermission("biz:betOrders:edit") // 复用编辑权限
    @Log(title = "批量修改赔率", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/batchUpdateOdds")
    public R<Void> batchUpdateOdds(@Validated @RequestBody BatchUpdateOddsDto dto) {
        return toAjax(bizBetOrdersService.batchUpdateOdds(dto));
    }
}
