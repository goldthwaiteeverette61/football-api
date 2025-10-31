package org.dromara.biz.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.dromara.biz.domain.BizUserInvitations;
import org.dromara.biz.domain.bo.BizUserInvitationsBo;
import org.dromara.biz.domain.vo.BizUserInvitationsVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 用户邀请记录Service接口
 *
 * @author Lion Li
 * @date 2025-08-28
 */
public interface IBizUserInvitationsService {

    Map<String, Long> getInvitationSummary(Long inviterId);

    /**
     * 查询用户邀请记录
     *
     * @param invitationId 主键
     * @return 用户邀请记录
     */
    BizUserInvitationsVo queryById(Long invitationId);

    /**
     * 分页查询用户邀请记录列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 用户邀请记录分页列表
     */
    TableDataInfo<BizUserInvitationsVo> queryPageList(BizUserInvitationsBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的用户邀请记录列表
     *
     * @param bo 查询条件
     * @return 用户邀请记录列表
     */
    List<BizUserInvitationsVo> queryList(BizUserInvitationsBo bo);

    /**
     * 新增用户邀请记录
     *
     * @param bo 用户邀请记录
     * @return 是否新增成功
     */
    Boolean insertByBo(BizUserInvitationsBo bo);

    /**
     * 修改用户邀请记录
     *
     * @param bo 用户邀请记录
     * @return 是否修改成功
     */
    Boolean updateByBo(BizUserInvitationsBo bo);

    /**
     * 校验并批量删除用户邀请记录信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);


    List<BizUserInvitationsVo> queryList(LambdaQueryWrapper<BizUserInvitations> lqw);

    BizUserInvitationsVo queryOne(LambdaQueryWrapper<BizUserInvitations> lqw);

    /**
     * 新增或修改
     *
     * @param bo 用户邀请记录
     * @return 是否修改成功
     */
    Boolean saveOrUpdate(BizUserInvitationsBo bo);
}
