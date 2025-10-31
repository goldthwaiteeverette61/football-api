package org.dromara.biz.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.dromara.biz.domain.BizMatches;
import org.dromara.biz.domain.bo.BizMatchesBo;
import org.dromara.biz.domain.vo.BizMatchesGroupVo;
import org.dromara.biz.domain.vo.BizMatchesVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 比赛信息Service接口
 *
 * @author Lion Li
 * @date 2025-07-24
 */
public interface IBizMatchesService {

    /**
     * 【新增】根据ID列表批量查询比赛信息
     *
     * @param matchIds 比赛ID列表
     * @return 比赛信息列表
     */
    List<BizMatchesVo> queryListByIds(List<Long> matchIds);

    LambdaQueryWrapper<BizMatches> getLqw();

    /**
     * 新增或更新
     *
     * @param bo 对象
     * @return 是否成功
     */
    Boolean insertOrUpdate(BizMatchesBo bo);

    List<BizMatchesVo> queryFinishedMatches();

    /**
     * 查询在指定时间范围内，需要更新实时比分的比赛列表
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 比赛列表
     */
    List<BizMatchesVo> queryMatchesForLiveScoreUpdate(Date startTime, Date endTime);

    /**
     * 查询比赛信息
     *
     * @param matchId 主键
     * @return 比赛信息
     */
    BizMatchesVo queryById(Long matchId);

    /**
     * 分页查询比赛信息列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 比赛信息分页列表
     */
    TableDataInfo<BizMatchesVo> queryPageList(BizMatchesBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的比赛信息列表
     *
     * @param bo 查询条件
     * @return 比赛信息列表
     */
    List<BizMatchesVo> queryList(BizMatchesBo bo);

    /**
     * 新增比赛信息
     *
     * @param bo 比赛信息
     * @return 是否新增成功
     */
    Boolean insertByBo(BizMatchesBo bo);

    /**
     * 修改比赛信息
     *
     * @param bo 比赛信息
     * @return 是否修改成功
     */
    Boolean updateByBo(BizMatchesBo bo);

    /**
     * 校验并批量删除比赛信息信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    List<BizMatchesVo> nePayoutList();

    /**
     * 查询用于足球计算器页面的比赛列表，并组装好赔率数据
     * @param bo 查询条件
     * @return 按日期分组的比赛列表
     */
    List<BizMatchesGroupVo> queryCalculatorList(BizMatchesBo bo);

    List<BizMatchesGroupVo> queryCalculatorListWithoutOdds(BizMatchesBo bo);

    /**
     * 根据多个比赛ID查询，并以Map形式返回
     * @param matchIds 比赛ID列表
     * @return Key为比赛ID，Value为比赛信息的Map
     */
    Map<Long, BizMatchesVo> queryMapByIds(List<Long> matchIds);

    List<BizMatchesVo> queryList(LambdaQueryWrapper<BizMatches> lqw);

    BizMatchesVo queryOne(LambdaQueryWrapper<BizMatches> lqw);

    /**
     * 新增或修改
     *
     * @param bo 比赛信息
     * @return 是否修改成功
     */
    Boolean saveOrUpdate(BizMatchesBo bo);

}
