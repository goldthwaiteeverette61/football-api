package org.dromara.biz.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.dromara.biz.domain.BizUserFollows;
import org.dromara.biz.domain.bo.BizUserFollowsBo;
import org.dromara.biz.domain.vo.BizUserFollowsVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 用户跟投记录Mapper接口
 *
 * @author Lion Li
 * @date 2025-07-28
 */
public interface BizUserFollowsMapper extends BaseMapperPlus<BizUserFollows, BizUserFollowsVo> {

    /**
     * 【核心新增】根据查询条件统计总跟投金额
     */
    Map<String, BigDecimal> selectSumBetAmount(@Param(Constants.WRAPPER) Wrapper<BizUserFollows> queryWrapper);

    BigDecimal sumUserAmountForLostPeriodsAfter(@Param("userId") Long userId, @Param("periodId") Long periodId);
    Long countUserAmountForLostPeriodsAfter(@Param("userId") Long userId, @Param("periodId") Long periodId);
    BigDecimal sumAmountByPeriodIdAndUserId(@Param("periodId") Long periodId, @Param("userId") Long userId);

    Long countForLostPeriodsAfter(@Param("periodId") Long periodId);
//    BigDecimal sumAmountForLostPeriodsAfter(@Param("userId") Long userId, @Param("periodId") Long periodId);
//    BigDecimal selectSumAmountByPeriodId(@Param("periodId") Long periodId);

    Page<BizUserFollowsVo> selectMyFollowsPage(@Param("page") Page<BizUserFollowsVo> page, @Param("bo") BizUserFollowsBo bo);
    /**
     * 查询用户最后一次已结算的跟投记录
     * @param userId 用户ID
     * @return 包含期数状态的跟投记录
     */
    BizUserFollowsVo selectUserLastSettledFollow(@Param("userId") Long userId);


}
