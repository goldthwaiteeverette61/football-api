package org.dromara.biz.service;

import org.dromara.biz.domain.bo.BizLeaguesBo;
import org.dromara.biz.domain.vo.BizLeaguesVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 联赛信息Service接口
 *
 * @author Lion Li
 * @date 2025-07-24
 */
public interface IBizLeaguesService {

    Map<String, BizLeaguesVo> queryMapByIds(List<String> ids);

    /**
     * 查询联赛信息
     *
     * @param leagueId 主键
     * @return 联赛信息
     */
    BizLeaguesVo queryById(String leagueId);

    /**
     * 分页查询联赛信息列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 联赛信息分页列表
     */
    TableDataInfo<BizLeaguesVo> queryPageList(BizLeaguesBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的联赛信息列表
     *
     * @param bo 查询条件
     * @return 联赛信息列表
     */
    List<BizLeaguesVo> queryList(BizLeaguesBo bo);

    /**
     * 新增联赛信息
     *
     * @param bo 联赛信息
     * @return 是否新增成功
     */
    Boolean insertByBo(BizLeaguesBo bo);

    /**
     * 修改联赛信息
     *
     * @param bo 联赛信息
     * @return 是否修改成功
     */
    Boolean updateByBo(BizLeaguesBo bo);

    /**
     * 校验并批量删除联赛信息信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);

    /**
     * 新增或修改
     * @param bo
     * @return
     */
    Boolean saveOrUpdate(BizLeaguesBo bo);

}
