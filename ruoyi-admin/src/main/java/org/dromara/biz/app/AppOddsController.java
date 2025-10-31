package org.dromara.biz.app;

import cn.hutool.json.JSONObject;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.bo.BizOddsBo;
import org.dromara.biz.domain.vo.BizOddsVo;
import org.dromara.biz.service.IBizOddsService;
import org.dromara.biz.service.IJcDataService;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 比賽賠率
 *
 * @author Lion Li
 * @date 2025-07-24
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/app/odds")
@Tag(name = "odds", description = "賠率")
public class AppOddsController extends BaseController {

//    private final IBizOddsService bizOddsService;
    private final IJcDataService iJcDataService;

//    /**
//     * 查詢比賽賠率列表
//     */
//    @GetMapping("/list")
//    public TableDataInfo<BizOddsVo> oddsList(BizOddsBo bo, PageQuery pageQuery) {
//        return bizOddsService.queryPageList(bo, pageQuery);
//    }

    /**
     * 根據玩法代碼動態查詢比賽賠率列表
     * @return R<JSONObject> 格式化的比賽數據
     */
    @GetMapping("/oddsList")
    public R<JSONObject> oddsList() {
        JSONObject oddsData = iJcDataService.getFormattedOddsByPools();
        return R.ok(oddsData);
    }

    /**
     * 根據玩法代碼動態查詢比賽賠率列表
     * @param poolCodes 玩法代碼列表, 例如: spf,crs,ttg,hafu
     * @return R<JSONObject> 格式化的比賽數據
     */
    @GetMapping("/oddsList2")
    public R<JSONObject> oddsList2(@RequestParam(value = "poolCodes") List<String> poolCodes) {
        JSONObject oddsData = iJcDataService.getFormattedOddsByPools();
        return R.ok(oddsData);
    }

}
