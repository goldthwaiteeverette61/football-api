package org.dromara.biz.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.dromara.biz.domain.BizSystemReserveSummary;
import org.dromara.biz.domain.vo.BizSystemReserveSummaryVo;
import org.dromara.biz.domain.bo.BizSystemReserveSummaryBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * 系统储备金汇总Service接口
 *
 * @author Lion Li
 * @date 2025-08-09
 */
public interface IBizSystemReserveSummaryService {

    void deductReserveAmount(BigDecimal amount);

    /**
     * 累加储备金总额 (原子操作)
     * @param amount 要增加的金额
     */
    void addReserveAmount(BigDecimal amount);

    /**
     * 查询系统储备金汇总
     *
     * @param summaryId 主键
     * @return 系统储备金汇总
     */
    BizSystemReserveSummaryVo queryById(Integer summaryId);

    /**
     * 分页查询系统储备金汇总列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 系统储备金汇总分页列表
     */
    TableDataInfo<BizSystemReserveSummaryVo> queryPageList(BizSystemReserveSummaryBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的系统储备金汇总列表
     *
     * @param bo 查询条件
     * @return 系统储备金汇总列表
     */
    List<BizSystemReserveSummaryVo> queryList(BizSystemReserveSummaryBo bo);

    /**
     * 新增系统储备金汇总
     *
     * @param bo 系统储备金汇总
     * @return 是否新增成功
     */
    Boolean insertByBo(BizSystemReserveSummaryBo bo);

    /**
     * 修改系统储备金汇总
     *
     * @param bo 系统储备金汇总
     * @return 是否修改成功
     */
    Boolean updateByBo(BizSystemReserveSummaryBo bo);

    /**
     * 校验并批量删除系统储备金汇总信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid);


    /**
     * 新增或更新
     *
     * @param bo 对象
     * @return 是否成功
     */
    Boolean insertOrUpdate(BizSystemReserveSummaryBo bo);

    List<BizSystemReserveSummaryVo> queryList(LambdaQueryWrapper<BizSystemReserveSummary> lqw);

    BizSystemReserveSummaryVo queryOne(LambdaQueryWrapper<BizSystemReserveSummary> lqw);

    BizSystemReserveSummaryVo getDefault();

    /**
     * 核心修改：新增原子扣减方法
     * 扣减储备金总额 (原子操作)
     * @param amount 要扣减的金额
     */
    void subtractReserveAmount(BigDecimal amount);
}
