package org.dromara.biz.service;

import org.dromara.biz.domain.bo.BizTeamsBo;
import org.dromara.biz.domain.vo.BizTeamsVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 球队信息Service接口
 *
 * @author Lion Li
 * @date 2025-07-24
 */
public interface IBizTeamsService {

    Map<Long, BizTeamsVo> queryMapByIds(List<Long> ids);

    boolean saveOrUpdate(BizTeamsBo bo);
    List<BizTeamsVo> queryTeamsWithoutLogo();

    /**
     * 查询球队信息
     *
     * @param teamId 主键
     * @return 球队信息
     */
    BizTeamsVo queryById(Long teamId);

    /**
     * 分页查询球队信息列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 球队信息分页列表
     */
    TableDataInfo<BizTeamsVo> queryPageList(BizTeamsBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的球队信息列表
     *
     * @param bo 查询条件
     * @return 球队信息列表
     */
    List<BizTeamsVo> queryList(BizTeamsBo bo);

    /**
     * 新增球队信息
     *
     * @param bo 球队信息
     * @return 是否新增成功
     */
    Boolean insertByBo(BizTeamsBo bo);

    /**
     * 修改球队信息
     *
     * @param bo 球队信息
     * @return 是否修改成功
     */
    Boolean updateByBo(BizTeamsBo bo);

    /**
     * 校验并批量删除球队信息信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
