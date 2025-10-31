package org.dromara.biz.mapper;

import org.apache.ibatis.annotations.Param;
import org.dromara.biz.domain.BizSchemePeriods;
import org.dromara.biz.domain.vo.BizSchemePeriodsVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;

import java.util.List;

/**
 * 方案期数Mapper接口
 *
 * @author Lion Li
 * @date 2025-07-28
 */
public interface BizSchemePeriodsMapper extends BaseMapperPlus<BizSchemePeriods, BizSchemePeriodsVo> {
    BizSchemePeriodsVo selectLastWonPeriodByUserId(@Param("userId") Long userId);

    /**
     * 查找在指定期数ID之后，用户跟投且结果为'lost'的所有期数
     *
     * @param userId   用户ID
     * @param periodId 起始的期数ID (不包含)
     * @return 输掉的期数列表
     */
    List<BizSchemePeriodsVo> findLostPeriodsAfter(@Param("userId") Long userId, @Param("periodId") Long periodId);
}
