package org.dromara.biz.app;

import lombok.RequiredArgsConstructor;
import org.dromara.biz.service.IBizTransactionsService;
import org.dromara.biz.service.IBizUserInvitationsService;
import org.dromara.common.core.domain.R;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 用戶邀請記錄
 *
 * @author Lion Li
 * @date 2025-08-28
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/app/userInvitations")
public class AppUserInvitationsController extends BaseController {

    private final IBizTransactionsService transactionsService;
    private final IBizUserInvitationsService iBizUserInvitationsService;

    /**
     * 獲取當前用戶的邀請收益統計
     */
    @GetMapping("/invitationStats")
    public R<Map<String, Object>> getInvitationStats() {
        Long userId = LoginHelper.getUserId();
        Map<String, Object> map = transactionsService.getInvitationCommissionStats(userId);
        map.putAll(iBizUserInvitationsService.getInvitationSummary(userId));
        return R.ok(map);
    }


}
