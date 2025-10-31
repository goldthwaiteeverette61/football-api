package org.dromara.biz.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.dromara.biz.domain.BizTransactions;
import org.dromara.biz.domain.bo.TransactionHistoryBo;
import org.dromara.biz.domain.vo.BizTransactionsVo;
import org.dromara.biz.domain.vo.TransactionHistoryVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;

import java.math.BigDecimal;

/**
 * 用户资金流水Mapper接口
 *
 * @author Lion Li
 * @date 2025-08-06
 */
public interface BizTransactionsMapper extends BaseMapperPlus<BizTransactions, BizTransactionsVo> {

    /**
     * 根据时间范围统计指定用户的佣金总额
     *
     * @param userId    用户ID
     * @param timeRange 时间范围 ("today", "this_month", "total")
     * @return 佣金总额
     */
    BigDecimal sumCommissionByTimeRange(@Param("userId") Long userId, @Param("timeRange") String timeRange);

    /**
     * 查询我的交易历史分页记录
     * @param page 分页对象
     * @param userId 当前用户ID
     * @param bo 筛选条件
     * @return 分页列表
     */
    Page<TransactionHistoryVo> selectMyHistoryPage(@Param("page") Page<TransactionHistoryVo> page,
                                                   @Param("userId") Long userId,
                                                   @Param("bo") TransactionHistoryBo bo);
}
