package org.dromara.biz.service;

import org.dromara.biz.domain.vo.BizSchemePeriodDetailsVo;
import org.dromara.biz.domain.bo.BizSchemePeriodDetailsBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 方案期数详情Service接口
 *
 * @author Lion Li
 * @date 2025-07-28
 */
public interface IBizSchemePeriodDetailsService {

    List<BizSchemePeriodDetailsVo> queryByPeriodId(Long periodId);

    void updateBatch(List<BizSchemePeriodDetailsBo> boList);

    List<BizSchemePeriodDetailsVo> queryListByPeriodIds(List<Long> periodIds);

    /**
     * 根据期数ID统计详情数量
     * @param periodId 期数ID
     * @return 详情数量
     */
    long countByPeriodId(Long periodId);

    /**
     * 查询方案期数详情
     *
     * @param detailId 主键
     * @return 方案期数详情
     */
    BizSchemePeriodDetailsVo queryById(Long detailId);

    /**
     * 分页查询方案期数详情列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 方案期数详情分页列表
     */
    TableDataInfo<BizSchemePeriodDetailsVo> queryPageList(BizSchemePeriodDetailsBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的方案期数详情列表
     *
     * @param bo 查询条件
     * @return 方案期数详情列表
     */
    List<BizSchemePeriodDetailsVo> queryList(BizSchemePeriodDetailsBo bo);

    /**
     * 新增方案期数详情
     *
     * @param bo 方案期数详情
     * @return 是否新增成功
     */
    Boolean insertByBo(BizSchemePeriodDetailsBo bo);

    /**
     * 修改方案期数详情
     *
     * @param bo 方案期数详情
     * @return 是否修改成功
     */
    Boolean updateByBo(BizSchemePeriodDetailsBo bo);

    /**
     * 校验并批量删除方案期数详情信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);


    /**
     * 新增或更新
     *
     * @param bo 对象
     * @return 是否成功
     */
    Boolean insertOrUpdate(BizSchemePeriodDetailsBo bo);

    Boolean deleteByPeriodId(Long periodId);

    Boolean insertBatchByBo(List<BizSchemePeriodDetailsBo> list);

    /**
     * 根据多个期数ID查询详情列表
     * @param periodIds 期数ID列表
     * @return 详情列表
     */
    List<BizSchemePeriodDetailsVo> queryByPeriodIds(List<Long> periodIds);
}
