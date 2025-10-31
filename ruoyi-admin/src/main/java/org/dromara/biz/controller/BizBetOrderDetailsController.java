package org.dromara.biz.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.bo.BizBetOrderDetailsBo;
import org.dromara.biz.domain.vo.BizBetOrderDetailsVo;
import org.dromara.biz.service.IBizBetOrderDetailsService;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 投注訂單詳情
 *
 * @author Lion Li
 * @date 2025-10-08
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/biz/betOrderDetails")
public class BizBetOrderDetailsController extends BaseController {

    private final IBizBetOrderDetailsService bizBetOrderDetailsService;

    /**
     * 查询投注訂單詳情列表
     */
    @SaCheckPermission("biz:betOrderDetails:list")
    @GetMapping("/list-s")
    public List<BizBetOrderDetailsVo> listS(BizBetOrderDetailsBo bo) {
        return bizBetOrderDetailsService.queryListF1(bo);
    }

    /**
     * 查询投注訂單詳情列表
     */
    @SaCheckPermission("biz:betOrderDetails:list")
    @GetMapping("/list")
    public TableDataInfo<BizBetOrderDetailsVo> list(BizBetOrderDetailsBo bo, PageQuery pageQuery) {
        return bizBetOrderDetailsService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出投注訂單詳情列表
     */
    @SaCheckPermission("biz:betOrderDetails:export")
    @Log(title = "投注訂單詳情", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BizBetOrderDetailsBo bo, HttpServletResponse response) {
        List<BizBetOrderDetailsVo> list = bizBetOrderDetailsService.queryList(bo);
        ExcelUtil.exportExcel(list, "投注訂單詳情", BizBetOrderDetailsVo.class, response);
    }

    /**
     * 获取投注訂單詳情详细信息
     *
     * @param detailId 主键
     */
    @SaCheckPermission("biz:betOrderDetails:query")
    @GetMapping("/{detailId}")
    public R<BizBetOrderDetailsVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long detailId) {
        return R.ok(bizBetOrderDetailsService.queryById(detailId));
    }

    /**
     * 新增投注訂單詳情
     */
    @SaCheckPermission("biz:betOrderDetails:add")
    @Log(title = "投注訂單詳情", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizBetOrderDetailsBo bo) {
        return toAjax(bizBetOrderDetailsService.insertByBo(bo));
    }

    /**
     * 修改投注訂單詳情
     */
    @SaCheckPermission("biz:betOrderDetails:edit")
    @Log(title = "投注訂單詳情", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizBetOrderDetailsBo bo) {
        return toAjax(bizBetOrderDetailsService.updateByBo(bo));
    }

    /**
     * 删除投注訂單詳情
     *
     * @param detailIds 主键串
     */
    @SaCheckPermission("biz:betOrderDetails:remove")
    @Log(title = "投注訂單詳情", businessType = BusinessType.DELETE)
    @DeleteMapping("/{detailIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] detailIds) {
        return toAjax(bizBetOrderDetailsService.deleteWithValidByIds(List.of(detailIds), true));
    }
}
