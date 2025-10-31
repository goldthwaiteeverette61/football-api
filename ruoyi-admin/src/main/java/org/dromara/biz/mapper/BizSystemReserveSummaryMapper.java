package org.dromara.biz.mapper;

import org.apache.ibatis.annotations.Param;
import org.dromara.biz.domain.BizSystemReserveSummary;
import org.dromara.biz.domain.vo.BizSystemReserveSummaryVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;

import java.math.BigDecimal;

/**
 * 系统储备金汇总Mapper接口
 *
 * @author Lion Li
 * @date 2025-08-09
 */
public interface BizSystemReserveSummaryMapper extends BaseMapperPlus<BizSystemReserveSummary, BizSystemReserveSummaryVo> {
    /**
     * 以原子方式累加储备金总额
     * @param amount 要增加的金额
     * @param id     主键ID
     */
    void addTotalReserveAmount(@Param("amount") BigDecimal amount, @Param("id") Long id);

    /**
     * 核心修改：新增原子扣减的Mapper方法
     */
    void subtractTotalReserveAmount(@Param("amount") BigDecimal amount, @Param("id") Long id);

    /**
     * 原子性扣除储备金 (余额充足时才会成功)
     * @param amount 扣除金额
     * @return 受影响行数
     */
    int deductReserveAmount(@Param("amount") BigDecimal amount);
}
