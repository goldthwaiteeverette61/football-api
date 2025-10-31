package org.dromara.biz.app;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.bo.BizWithdrawalsBo;
import org.dromara.biz.domain.bo.WithdrawalApplyBo;
import org.dromara.biz.domain.vo.BizWithdrawalsVo;
import org.dromara.biz.service.IBizWithdrawalsService;
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
 * 用戶提現申請
 *
 * @author Lion Li
 * @date 2025-08-11
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/app/withdrawals")
@Tag(name = "withdrawal", description = "取款")
public class AppWithdrawalsController extends BaseController {

    private final IBizWithdrawalsService bizWithdrawalsService;

    @Log(title = "用戶提現申請", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping("/apply")
    public R<Void> applyWithdrawal(@Validated @RequestBody WithdrawalApplyBo bo) {
        bizWithdrawalsService.applyForWithdrawal(bo);
        return R.ok("您的提現申請已提交，正在等待審覈");
    }
}
