package org.dromara.biz.app;

import cn.hutool.core.date.DateUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.bo.BizMatchesBo;
import org.dromara.biz.domain.vo.BizMatchesGroupVo;
import org.dromara.biz.service.IBizMatchesService;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 比賽信息
 *
 * @author Lion Li
 * @date 2025-07-24
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/app/matches")
@Tag(name = "matches", description = "賽事")
public class AppMatchesController extends BaseController {

    private final IBizMatchesService bizMatchesService;

    /**
     * 獲取足球計算器比賽列表 (支持按狀態和日期篩選)
     */
    @GetMapping("/list")
    public TableDataInfo<BizMatchesGroupVo> getCalculatorList(BizMatchesBo bo) {

        if (bo.getBusinessDate() == null) {
            bo.setBusinessDate(DateUtil.beginOfDay(DateUtil.yesterday()));
            bo.setGeBusinessDate(true); // 激活 "大於等於" 查詢模式
        }

        List<BizMatchesGroupVo> list = bizMatchesService.queryCalculatorListWithoutOdds(bo);
        return TableDataInfo.build(list);
    }


}
