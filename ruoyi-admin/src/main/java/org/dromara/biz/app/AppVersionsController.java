package org.dromara.biz.app;

import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.dto.VersionCheckRequestDto;
import org.dromara.biz.domain.dto.VersionCheckResponseDto;
import org.dromara.biz.service.IBizAppVersionsService;
import org.dromara.common.core.domain.R;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 應用版本信息
 *
 * @author Lion Li
 * @date 2025-09-13
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/app/appVersions")
public class AppVersionsController extends BaseController {

    private final IBizAppVersionsService appVersionsService;

    /**
     * 檢查應用版本更新
     * 這是一個公開接口，不需要登錄權限
     */
    @GetMapping("/version/check")
    public R<VersionCheckResponseDto> checkVersion(@Validated VersionCheckRequestDto request) {
        VersionCheckResponseDto responseData = appVersionsService.checkVersion(request);
        return R.ok(responseData);
    }
}
