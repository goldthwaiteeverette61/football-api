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
import org.dromara.biz.domain.vo.BizChainSyncStateVo;
import org.dromara.biz.domain.bo.BizChainSyncStateBo;
import org.dromara.biz.service.IBizChainSyncStateService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 区块链同步状态
 *
 * @author Lion Li
 * @date 2025-09-29
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/biz/chainSyncState")
public class BizChainSyncStateController extends BaseController {

    private final IBizChainSyncStateService bizChainSyncStateService;

    /**
     * 查询区块链同步状态列表
     */
    @SaCheckPermission("biz:chainSyncState:list")
    @GetMapping("/list")
    public TableDataInfo<BizChainSyncStateVo> list(BizChainSyncStateBo bo, PageQuery pageQuery) {
        return bizChainSyncStateService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出区块链同步状态列表
     */
    @SaCheckPermission("biz:chainSyncState:export")
    @Log(title = "区块链同步状态", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BizChainSyncStateBo bo, HttpServletResponse response) {
        List<BizChainSyncStateVo> list = bizChainSyncStateService.queryList(bo);
        ExcelUtil.exportExcel(list, "区块链同步状态", BizChainSyncStateVo.class, response);
    }

    /**
     * 获取区块链同步状态详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("biz:chainSyncState:query")
    @GetMapping("/{id}")
    public R<BizChainSyncStateVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(bizChainSyncStateService.queryById(id));
    }

    /**
     * 新增区块链同步状态
     */
    @SaCheckPermission("biz:chainSyncState:add")
    @Log(title = "区块链同步状态", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizChainSyncStateBo bo) {
        return toAjax(bizChainSyncStateService.insertByBo(bo));
    }

    /**
     * 修改区块链同步状态
     */
    @SaCheckPermission("biz:chainSyncState:edit")
    @Log(title = "区块链同步状态", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizChainSyncStateBo bo) {
        return toAjax(bizChainSyncStateService.updateByBo(bo));
    }

    /**
     * 删除区块链同步状态
     *
     * @param ids 主键串
     */
    @SaCheckPermission("biz:chainSyncState:remove")
    @Log(title = "区块链同步状态", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(bizChainSyncStateService.deleteWithValidByIds(List.of(ids), true));
    }
}
