package org.dromara.biz.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
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
import org.dromara.biz.domain.vo.BizTransactionsVo;
import org.dromara.biz.domain.bo.BizTransactionsBo;
import org.dromara.biz.service.IBizTransactionsService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 用户资金流水
 *
 * @author Lion Li
 * @date 2025-08-06
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/biz/transactions")
public class BizTransactionsController extends BaseController {

    private final IBizTransactionsService bizTransactionsService;

    /**
     * 查询用户资金流水列表
     */
    @SaCheckPermission("biz:transactions:list")
    @GetMapping("/list")
    public TableDataInfo<BizTransactionsVo> list(BizTransactionsBo bo, PageQuery pageQuery) {
        return bizTransactionsService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出用户资金流水列表
     */
    @SaCheckPermission("biz:transactions:export")
    @Log(title = "用户资金流水", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BizTransactionsBo bo, HttpServletResponse response) {
        List<BizTransactionsVo> list = bizTransactionsService.queryList(bo);
        ExcelUtil.exportExcel(list, "用户资金流水", BizTransactionsVo.class, response);
    }

    /**
     * 获取用户资金流水详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("biz:transactions:query")
    @GetMapping("/{id}")
    public R<BizTransactionsVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(bizTransactionsService.queryById(id));
    }

    /**
     * 新增用户资金流水
     */
    @SaCheckPermission("biz:transactions:add")
    @Log(title = "用户资金流水", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizTransactionsBo bo) {
        return toAjax(bizTransactionsService.insertByBo(bo));
    }

    /**
     * 修改用户资金流水
     */
    @SaCheckPermission("biz:transactions:edit")
    @Log(title = "用户资金流水", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizTransactionsBo bo) {
        return toAjax(bizTransactionsService.updateByBo(bo));
    }

    /**
     * 删除用户资金流水
     *
     * @param ids 主键串
     */
    @SaCheckPermission("biz:transactions:remove")
    @Log(title = "用户资金流水", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(bizTransactionsService.deleteWithValidByIds(List.of(ids), true));
    }
}
