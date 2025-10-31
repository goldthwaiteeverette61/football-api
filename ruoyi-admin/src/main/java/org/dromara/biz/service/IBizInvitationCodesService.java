package org.dromara.biz.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.dromara.biz.domain.BizInvitationCodes;
import org.dromara.biz.domain.vo.BizInvitationCodesVo;
import org.dromara.biz.domain.bo.BizInvitationCodesBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 预生成邀请码池Service接口
 *
 * @author Lion Li
 * @date 2025-08-28
 */
public interface IBizInvitationCodesService {

    BizInvitationCodesVo findOneAvailableCode();

    void generateCodes(int count);

    /**
     * 查询预生成邀请码池
     *
     * @param codeId 主键
     * @return 预生成邀请码池
     */
    BizInvitationCodesVo queryById(Long codeId);

    /**
     * 分页查询预生成邀请码池列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 预生成邀请码池分页列表
     */
    TableDataInfo<BizInvitationCodesVo> queryPageList(BizInvitationCodesBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的预生成邀请码池列表
     *
     * @param bo 查询条件
     * @return 预生成邀请码池列表
     */
    List<BizInvitationCodesVo> queryList(BizInvitationCodesBo bo);

    /**
     * 新增预生成邀请码池
     *
     * @param bo 预生成邀请码池
     * @return 是否新增成功
     */
    Boolean insertByBo(BizInvitationCodesBo bo);

    /**
     * 修改预生成邀请码池
     *
     * @param bo 预生成邀请码池
     * @return 是否修改成功
     */
    Boolean updateByBo(BizInvitationCodesBo bo);

    /**
     * 校验并批量删除预生成邀请码池信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);


    List<BizInvitationCodesVo> queryList(LambdaQueryWrapper<BizInvitationCodes> lqw);

    BizInvitationCodesVo queryOne(LambdaQueryWrapper<BizInvitationCodes> lqw);

    /**
     * 新增或修改
     *
     * @param bo 预生成邀请码池
     * @return 是否修改成功
     */
    Boolean saveOrUpdate(BizInvitationCodesBo bo);
}
