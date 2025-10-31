package org.dromara.biz.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.dromara.biz.domain.BizUserFollowDetails;
import org.dromara.biz.domain.bo.BizUserFollowDetailsBo;
import org.dromara.biz.domain.vo.BizUserFollowDetailsVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 用户跟投详情Service接口
 *
 * @author Lion Li
 * @date 2025-08-25
 */
public interface IBizUserFollowDetailsService {

    void insertBatch(List<BizUserFollowDetails> allNewDetailsToInsert);

    void delete(List<Long> followIds);

    /**
     * 【核心补充】根据跟投ID查询详情列表
     */
    List<BizUserFollowDetailsVo> queryByFollowId(Long followId);

    /**
     * 【核心补充】根据多个跟投ID查询所有详情
     */
    List<BizUserFollowDetailsVo> queryByFollowIds(List<Long> followIds);



    /**
     * 查询用户跟投详情
     *
     * @param followDetailId 主键
     * @return 用户跟投详情
     */
    BizUserFollowDetailsVo queryById(Long followDetailId);

    /**
     * 分页查询用户跟投详情列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 用户跟投详情分页列表
     */
    TableDataInfo<BizUserFollowDetailsVo> queryPageList(BizUserFollowDetailsBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的用户跟投详情列表
     *
     * @param bo 查询条件
     * @return 用户跟投详情列表
     */
    List<BizUserFollowDetailsVo> queryList(BizUserFollowDetailsBo bo);

    /**
     * 新增用户跟投详情
     *
     * @param bo 用户跟投详情
     * @return 是否新增成功
     */
    Boolean insertByBo(BizUserFollowDetailsBo bo);

    /**
     * 修改用户跟投详情
     *
     * @param bo 用户跟投详情
     * @return 是否修改成功
     */
    Boolean updateByBo(BizUserFollowDetailsBo bo);

    /**
     * 校验并批量删除用户跟投详情信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);


    List<BizUserFollowDetailsVo> queryList(LambdaQueryWrapper<BizUserFollowDetails> lqw);

    BizUserFollowDetailsVo queryOne(LambdaQueryWrapper<BizUserFollowDetails> lqw);

    /**
     * 新增或修改
     *
     * @param bo 用户跟投详情
     * @return 是否修改成功
     */
    Boolean saveOrUpdate(BizUserFollowDetailsBo bo);
}
