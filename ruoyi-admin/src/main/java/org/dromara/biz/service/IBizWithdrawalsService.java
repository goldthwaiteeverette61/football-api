package org.dromara.biz.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.dromara.biz.domain.BizWithdrawals;
import org.dromara.biz.domain.bo.WithdrawalApplyBo;
import org.dromara.biz.domain.bo.WithdrawalAuditBo;
import org.dromara.biz.domain.vo.BizWithdrawalsVo;
import org.dromara.biz.domain.bo.BizWithdrawalsBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 用户提现申请Service接口
 *
 * @author Lion Li
 * @date 2025-08-11
 */
public interface IBizWithdrawalsService {

    /**
     * 查询用户提现申请
     *
     * @param withdrawalId 主键
     * @return 用户提现申请
     */
    BizWithdrawalsVo queryById(Long withdrawalId);

    /**
     * 分页查询用户提现申请列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 用户提现申请分页列表
     */
    TableDataInfo<BizWithdrawalsVo> queryPageList(BizWithdrawalsBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的用户提现申请列表
     *
     * @param bo 查询条件
     * @return 用户提现申请列表
     */
    List<BizWithdrawalsVo> queryList(BizWithdrawalsBo bo);

    /**
     * 新增用户提现申请
     *
     * @param bo 用户提现申请
     * @return 是否新增成功
     */
    Boolean insertByBo(BizWithdrawalsBo bo);

    /**
     * 修改用户提现申请
     *
     * @param bo 用户提现申请
     * @return 是否修改成功
     */
    Boolean updateByBo(BizWithdrawalsBo bo);

    /**
     * 校验并批量删除用户提现申请信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);


    List<BizWithdrawalsVo> queryList(LambdaQueryWrapper<BizWithdrawals> lqw);

    BizWithdrawalsVo queryOne(LambdaQueryWrapper<BizWithdrawals> lqw);

    void applyForWithdrawal(WithdrawalApplyBo applyBo);

    /**
     * 核心业务：后台管理员审核提现申请
     * @param auditBo 包含提现ID、审核结果和备注的请求对象
     */
    void auditWithdrawal(WithdrawalAuditBo auditBo);
}
