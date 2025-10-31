
// ===================================================================================
// 文件路径: org/dromara/biz/mapper/BizMatchesMapper.java
// 描述: 在 Mapper 接口中增加新的查询方法。
// ===================================================================================
package org.dromara.biz.mapper;

import org.apache.ibatis.annotations.Param;
import org.dromara.biz.domain.BizMatches;
import org.dromara.biz.domain.bo.BizMatchesBo;
import org.dromara.biz.domain.vo.BizMatchesVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;

import java.util.Date;
import java.util.List;

public interface BizMatchesMapper extends BaseMapperPlus<BizMatches, BizMatchesVo> {

    /**
     * 查询用于足球计算器页面的比赛列表
     * @param bo 查询条件
     * @return 比赛列表
     */
    List<BizMatchesVo> selectCalculatorMatchList(@Param("bo") BizMatchesBo bo);

    List<BizMatchesVo> selectMatchesForLiveScoreUpdate(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

}
