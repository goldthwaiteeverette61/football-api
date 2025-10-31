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
import org.dromara.biz.domain.vo.BizReferralsVo;
import org.dromara.biz.domain.bo.BizReferralsBo;
import org.dromara.biz.service.IBizReferralsService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 好友推荐关系
 *
 * @author Lion Li
 * @date 2025-07-24
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/biz/referrals")
public class BizReferralsController extends BaseController {

    private final IBizReferralsService bizReferralsService;

    /**
     * 查询好友推荐关系列表
     */
    @SaCheckPermission("biz:referrals:list")
    @GetMapping("/list")
    public TableDataInfo<BizReferralsVo> list(BizReferralsBo bo, PageQuery pageQuery) {
        return bizReferralsService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出好友推荐关系列表
     */
    @SaCheckPermission("biz:referrals:export")
    @Log(title = "好友推荐关系", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BizReferralsBo bo, HttpServletResponse response) {
        List<BizReferralsVo> list = bizReferralsService.queryList(bo);
        ExcelUtil.exportExcel(list, "好友推荐关系", BizReferralsVo.class, response);
    }

    /**
     * 获取好友推荐关系详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("biz:referrals:query")
    @GetMapping("/{id}")
    public R<BizReferralsVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(bizReferralsService.queryById(id));
    }

    /**
     * 新增好友推荐关系
     */
    @SaCheckPermission("biz:referrals:add")
    @Log(title = "好友推荐关系", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizReferralsBo bo) {
        return toAjax(bizReferralsService.insertByBo(bo));
    }

    /**
     * 修改好友推荐关系
     */
    @SaCheckPermission("biz:referrals:edit")
    @Log(title = "好友推荐关系", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizReferralsBo bo) {
        return toAjax(bizReferralsService.updateByBo(bo));
    }

    /**
     * 删除好友推荐关系
     *
     * @param ids 主键串
     */
    @SaCheckPermission("biz:referrals:remove")
    @Log(title = "好友推荐关系", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(bizReferralsService.deleteWithValidByIds(List.of(ids), true));
    }
}
