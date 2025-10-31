package org.dromara.biz.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.dromara.biz.domain.BizMatchResults;
import org.dromara.biz.domain.bo.BizMatchResultsBo;
import org.dromara.biz.domain.vo.BizMatchResultsVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 比赛赛果Service接口
 *
 * @author Lion Li
 * @date 2025-08-08
 */
public interface IBizMatchResultsService {

    /**
     * 【新增】根据比赛ID列表查询赛果
     * @param matchIds 比赛ID列表
     * @return 赛果列表
     */
    List<BizMatchResultsVo> queryListByMatchIds(List<Long> matchIds);

    LambdaQueryWrapper<BizMatchResults> getLqw();

    /**
     * 新增或修改
     *
     * @param bo 比赛赛果
     * @return 是否修改成功
     */
    Boolean saveOrUpdate(BizMatchResultsBo bo);

    /**
     * 【核心新增】根据比赛ID删除赛果
     * <p>
     * 这是为了保证赛果生成时的幂等性，在插入新赛果前先清除旧数据。
     *
     * @param matchId 比赛ID
     */
    public void deleteByMatchId(Long matchId);

    /**
     * 【核心新增】批量插入赛果
     *
     * @param boList 赛果业务对象列表
     */
    void insertBatchByBo(List<BizMatchResultsBo> boList);

    /**
     * 查询比赛赛果
     *
     * @param id 主键
     * @return 比赛赛果
     */
    BizMatchResultsVo queryById(Long id);

    /**
     * 分页查询比赛赛果列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 比赛赛果分页列表
     */
    TableDataInfo<BizMatchResultsVo> queryPageList(BizMatchResultsBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的比赛赛果列表
     *
     * @param bo 查询条件
     * @return 比赛赛果列表
     */
    List<BizMatchResultsVo> queryList(BizMatchResultsBo bo);

    /**
     * 新增比赛赛果
     *
     * @param bo 比赛赛果
     * @return 是否新增成功
     */
    Boolean insertByBo(BizMatchResultsBo bo);

    /**
     * 修改比赛赛果
     *
     * @param bo 比赛赛果
     * @return 是否修改成功
     */
    Boolean updateByBo(BizMatchResultsBo bo);

    /**
     * 校验并批量删除比赛赛果信息
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
    Boolean insertOrUpdate(BizMatchResultsBo bo);

    List<BizMatchResultsVo> queryList(LambdaQueryWrapper<BizMatchResults> lqw);

    BizMatchResultsVo queryOne(LambdaQueryWrapper<BizMatchResults> lqw);

    /**
     * 分页查询比赛赛果列表 (包含详情)
     */
    TableDataInfo<BizMatchResultsVo> queryPageListWithDetails(BizMatchResultsBo bo, PageQuery pageQuery);
}
