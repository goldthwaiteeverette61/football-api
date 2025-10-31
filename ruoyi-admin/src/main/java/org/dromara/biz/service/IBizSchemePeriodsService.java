package org.dromara.biz.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.dromara.biz.domain.BizSchemePeriods;
import org.dromara.biz.domain.bo.BizSchemePeriodsBo;
import org.dromara.biz.domain.vo.BizSchemePeriodsVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 方案期数Service接口
 *
 * @author Lion Li
 * @date 2025-07-28
 */
public interface IBizSchemePeriodsService {

    /**
     * 【核心新增】查询当前进行中的方案，或24小时内最近结束的方案
     * @return 方案期数视图对象，可能为null
     */
    BizSchemePeriodsVo findActiveOrRecentPeriod();

    /**
     * 【核心新增】获取方案看板统计数据
     * @return 包含总赢、总输和最近50期结果的Map
     */
    Map<String, Object> getSchemeDashboardStats();

    /**
     * 发布方案
     * @param periodId 要发布的方案ID
     * @return 是否发布成功
     */
    Boolean publishPeriod(Long periodId);


    /**
     * 查询方案期数
     *
     * @param periodId 主键
     * @return 方案期数
     */
    BizSchemePeriodsVo queryById(Long periodId);

    /**
     * 分页查询方案期数列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 方案期数分页列表
     */
    TableDataInfo<BizSchemePeriodsVo> queryPageList(BizSchemePeriodsBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的方案期数列表
     *
     * @param bo 查询条件
     * @return 方案期数列表
     */
    List<BizSchemePeriodsVo> queryList(BizSchemePeriodsBo bo);

    /**
     * 查询符合条件的方案期数列表
     */
    List<BizSchemePeriodsVo> queryList(LambdaQueryWrapper<BizSchemePeriods> lqw);

    /**
     * 新增方案期数
     *
     * @param bo 方案期数
     * @return 是否新增成功
     */
    Boolean insertByBo(BizSchemePeriodsBo bo);

    /**
     * 修改方案期数
     *
     * @param bo 方案期数
     * @return 是否修改成功
     */
    Boolean updateByBo(BizSchemePeriodsBo bo);

    /**
     * 校验并批量删除方案期数信息
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
    Boolean insertOrUpdate(BizSchemePeriodsBo bo);


    /**
     * 分页查询方案期数列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 方案期数分页列表
     */
    TableDataInfo<BizSchemePeriodsVo> queryPageListFull(BizSchemePeriodsBo bo, PageQuery pageQuery);

    /**
     *
     * 为一个方案期数列表批量填充其详细信息（比赛、球队等）
     *
     * @param periods 待填充的方案期数列表
     */
    void fillDetailsForPeriods(List<BizSchemePeriodsVo> periods);

    /**
     * 根据多个方案ID查询，并以Map形式返回
     * @param ids 方案ID列表
     * @return Key为方案ID，Value为方案信息的Map
     */
    Map<Long, BizSchemePeriodsVo> queryMapByIds(List<Long> ids);

    BizSchemePeriodsVo findLastWonPeriodByUserId(Long userId);



    /**
     * 查找在指定期数ID之后，用户跟投且结果为'lost'的所有期数
     *
     * @param userId   用户ID
     * @param periodId 起始的期数ID (不包含)
     * @return 输掉的期数列表
     */
    List<BizSchemePeriodsVo> findLostPeriodsAfter(Long userId, Long periodId);

    /**
     * 【核心新增】查询最近N期的开奖结果 (红/黑)
     * @param limit 查询的期数
     * @return "red" 或 "black" 的字符串列表
     */
    List<String> queryRecentResultStats(int limit);

    BizSchemePeriodsVo lastWonOrLost();

}
