package org.dromara.biz.service;

import org.dromara.biz.domain.vo.BizReferralsVo;
import org.dromara.biz.domain.bo.BizReferralsBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 好友推荐关系Service接口
 *
 * @author Lion Li
 * @date 2025-07-24
 */
public interface IBizReferralsService {

    /**
     * 查询好友推荐关系
     *
     * @param id 主键
     * @return 好友推荐关系
     */
    BizReferralsVo queryById(Long id);

    /**
     * 分页查询好友推荐关系列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 好友推荐关系分页列表
     */
    TableDataInfo<BizReferralsVo> queryPageList(BizReferralsBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的好友推荐关系列表
     *
     * @param bo 查询条件
     * @return 好友推荐关系列表
     */
    List<BizReferralsVo> queryList(BizReferralsBo bo);

    /**
     * 新增好友推荐关系
     *
     * @param bo 好友推荐关系
     * @return 是否新增成功
     */
    Boolean insertByBo(BizReferralsBo bo);

    /**
     * 修改好友推荐关系
     *
     * @param bo 好友推荐关系
     * @return 是否修改成功
     */
    Boolean updateByBo(BizReferralsBo bo);

    /**
     * 校验并批量删除好友推荐关系信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
