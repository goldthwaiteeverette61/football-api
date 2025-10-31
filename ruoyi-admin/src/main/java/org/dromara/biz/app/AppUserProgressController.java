package org.dromara.biz.app;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.dromara.biz.service.IBizUserProgressService;
import org.dromara.common.core.domain.R;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用戶跟投進度
 *
 * @author Lion Li
 * @date 2025-08-12
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/app/userProgress")
public class AppUserProgressController extends BaseController {

    private final IBizUserProgressService bizUserProgressService;

    /**
     * 切換用戶投注模式
     *
     * @param betType 新的投注模式 (normal 或 double)
     */
    @PutMapping("/betType")
    public R<Void> updateBetType(
        @NotBlank(message = "投注模式不能爲空")
        @Pattern(regexp = "^(normal|double)$", message = "無效的投注模式，只能是 'normal' 或 'double'")
        @RequestParam String betType) {
        Long userId = LoginHelper.getUserId();
        bizUserProgressService.updateBetType(userId, betType);
        return R.ok("模式切換成功");
    }

}
