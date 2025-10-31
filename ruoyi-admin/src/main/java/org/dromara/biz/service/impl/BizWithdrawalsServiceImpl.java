package org.dromara.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.biz.domain.BizWithdrawals;
import org.dromara.biz.domain.bo.BizTransactionsBo;
import org.dromara.biz.domain.bo.BizWithdrawalsBo;
import org.dromara.biz.domain.bo.WithdrawalApplyBo;
import org.dromara.biz.domain.bo.WithdrawalAuditBo;
import org.dromara.biz.domain.vo.BizWithdrawalsVo;
import org.dromara.biz.mapper.BizWithdrawalsMapper;
import org.dromara.biz.service.IBizTransactionsService;
import org.dromara.biz.service.IBizWithdrawalsService;
import org.dromara.common.core.domain.model.LoginUser;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.system.domain.vo.SysUserVo;
import org.dromara.system.service.ISysConfigService;
import org.dromara.system.service.ISysUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 用户提现申请Service业务层处理
 *
 * @author Lion Li
 * @date 2025-08-11
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class BizWithdrawalsServiceImpl extends BaseImpl<BizWithdrawals, BizWithdrawalsVo> implements IBizWithdrawalsService {

    private final BizWithdrawalsMapper baseMapper;

    private final ISysUserService sysUserService;
    private final IBizTransactionsService bizTransactionsService;
    private final ISysConfigService iSysConfigService;

    /**
     * 核心业务：后台管理员审核提现申请
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditWithdrawal(WithdrawalAuditBo auditBo) {
        // 2. 获取审核员信息
        Long auditorId = LoginHelper.getUserId();
        // 1. 获取并校验提现申请记录
        BizWithdrawalsVo withdrawal = this.queryById(auditBo.getWithdrawalId());
        if (withdrawal == null) {
            throw new ServiceException("提现申请不存在");
        }
        if (!"PENDING".equals(withdrawal.getStatus())) {
            throw new ServiceException("该申请已被处理，请勿重复操作");
        }

        BizWithdrawalsBo updateBo = new BizWithdrawalsBo();
        updateBo.setWithdrawalId(withdrawal.getWithdrawalId());
        updateBo.setAuditBy(auditorId);
        updateBo.setAuditTime(new Date());
        updateBo.setAuditRemarks(auditBo.getAuditRemarks());

        // 3. 根据审核结果分别处理
        if (auditBo.getIsApproved()) {
            updateBo.setTxHash(auditBo.getTxHash());
            // ========== 审核通过 ==========
            updateBo.setStatus("APPROVED");
            // 更新提现申请状态
            this.updateByBo(updateBo);
            // 更新关联的交易流水状态
            bizTransactionsService.updateStatusAndTxIdBySourceId(withdrawal.getWithdrawalId().toString(), auditBo.getTxHash(), "APPROVED");
            log.info("提现申请ID: {} 已被管理员ID: {} 批准。", withdrawal.getWithdrawalId(), auditorId);

        } else {
            // ========== 审核拒绝 ==========
            if (StringUtils.isBlank(auditBo.getAuditRemarks())) {
                throw new ServiceException("拒绝提现申请时，必须填写备注说明");
            }
            updateBo.setStatus("REJECTED");
            // 更新提现申请状态
            this.updateByBo(updateBo);
            // 关键：将冻结的金额返还给用户
            sysUserService.addBalance(withdrawal.getUserId(), withdrawal.getAmount());
            // 更新关联的交易流水状态为“失败”或“已取消”
            bizTransactionsService.updateStatusBySourceId(withdrawal.getWithdrawalId().toString(), "REJECTED");

            log.info("提现申请ID: {} 已被管理员ID: {} 拒绝。原因: {}", withdrawal.getWithdrawalId(), auditorId, auditBo.getAuditRemarks());
        }
    }

    /**
     * 查询用户提现申请
     *
     * @param withdrawalId 主键
     * @return 用户提现申请
     */
    @Override
    public BizWithdrawalsVo queryById(Long withdrawalId){
        return baseMapper.selectVoById(withdrawalId);
    }

    /**
     * 分页查询用户提现申请列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 用户提现申请分页列表
     */
    @Override
    public TableDataInfo<BizWithdrawalsVo> queryPageList(BizWithdrawalsBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizWithdrawals> lqw = buildQueryWrapper(bo);
        Page<BizWithdrawalsVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的用户提现申请列表
     *
     * @param bo 查询条件
     * @return 用户提现申请列表
     */
    @Override
    public List<BizWithdrawalsVo> queryList(BizWithdrawalsBo bo) {
        LambdaQueryWrapper<BizWithdrawals> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BizWithdrawals> buildQueryWrapper(BizWithdrawalsBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<BizWithdrawals> lqw = Wrappers.lambdaQuery();
        lqw.orderByDesc(BizWithdrawals::getWithdrawalId);
        lqw.eq(bo.getUserId() != null, BizWithdrawals::getUserId, bo.getUserId());
        lqw.eq(bo.getAmount() != null, BizWithdrawals::getAmount, bo.getAmount());
        lqw.eq(bo.getNetworkFee() != null, BizWithdrawals::getNetworkFee, bo.getNetworkFee());
        lqw.eq(bo.getFinalAmount() != null, BizWithdrawals::getFinalAmount, bo.getFinalAmount());
        lqw.eq(StringUtils.isNotBlank(bo.getToWalletAddress()), BizWithdrawals::getToWalletAddress, bo.getToWalletAddress());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), BizWithdrawals::getStatus, bo.getStatus());
        lqw.eq(bo.getRequestTime() != null, BizWithdrawals::getRequestTime, bo.getRequestTime());
        lqw.eq(bo.getAuditBy() != null, BizWithdrawals::getAuditBy, bo.getAuditBy());
        lqw.eq(bo.getAuditTime() != null, BizWithdrawals::getAuditTime, bo.getAuditTime());
        lqw.eq(StringUtils.isNotBlank(bo.getAuditRemarks()), BizWithdrawals::getAuditRemarks, bo.getAuditRemarks());
        lqw.eq(StringUtils.isNotBlank(bo.getTxHash()), BizWithdrawals::getTxHash, bo.getTxHash());
        lqw.eq(bo.getCompletionTime() != null, BizWithdrawals::getCompletionTime, bo.getCompletionTime());
        return lqw;
    }

    /**
     * 新增用户提现申请
     *
     * @param bo 用户提现申请
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BizWithdrawalsBo bo) {
        BizWithdrawals add = MapstructUtils.convert(bo, BizWithdrawals.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setWithdrawalId(add.getWithdrawalId());
        }
        return flag;
    }

    /**
     * 修改用户提现申请
     *
     * @param bo 用户提现申请
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BizWithdrawalsBo bo) {
        BizWithdrawals update = MapstructUtils.convert(bo, BizWithdrawals.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizWithdrawals entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除用户提现申请信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }

    /**
     * 执行列表查询
     * (这个方法会使用 lqw() 构建的查询条件)
     */
    @Override
    public List<BizWithdrawalsVo> queryList(LambdaQueryWrapper<BizWithdrawals> lqw) {
        // 直接调用父类 BaseImpl 中的 queryList() 方法
        return super.queryList(lqw);
    }

    /**
     * 执行单条记录查询
     * (这个方法会使用 lqw() 构建的查询条件)
     */
    @Override
    public BizWithdrawalsVo queryOne(LambdaQueryWrapper<BizWithdrawals> lqw) {
        // 直接调用父类 BaseImpl 中的 queryOne() 方法
        return super.queryOne(lqw);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyForWithdrawal(WithdrawalApplyBo applyBo) {
        LoginUser loginUser = LoginHelper.getLoginUser();
        Long userId = loginUser.getUserId();
        String username = loginUser.getUsername();

        // 1. 获取当前登录的用户信息
        SysUserVo user = sysUserService.selectUserById(userId);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }

        sysUserService.verifyPayPassword(userId,applyBo.getPayPassword());

        // 2. 参数校验
        if (applyBo.getAmount() == null || applyBo.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("提现金额必须为正数");
        }

        BigDecimal withdrawalMin = new BigDecimal(iSysConfigService.selectConfigByKey("sys.biz.withdrawalMin"));
        if (applyBo.getAmount().compareTo(withdrawalMin) < 0) {
            throw new ServiceException("提现金额不能小于" + withdrawalMin);
        }

        // 此处可以增加对 toWalletAddress 格式的校验，例如BSC地址以'0x'开头，长度为42位
        if (applyBo.getToWalletAddress() == null || !applyBo.getToWalletAddress().startsWith("0x") || applyBo.getToWalletAddress().length() != 42) {
            throw new ServiceException("无效的BSC钱包地址");
        }

        // 3. 检查余额是否充足
        BigDecimal withdrawalAmount = applyBo.getAmount();
        if (user.getBalance() == null || user.getBalance().compareTo(withdrawalAmount) < 0) {
            throw new ServiceException("账户余额不足");
        }

        // 4. 扣除用户余额 (重要：此操作在事务中)
        // 注意：为简化，这里直接扣除。更复杂的系统可能会使用“冻结余额”字段。
        boolean balanceDecreased = sysUserService.deductBalance(userId, withdrawalAmount);
        if (!balanceDecreased) {
            // 如果更新失败（例如，并发情况下），抛出异常以回滚事务
            throw new ServiceException("扣除余额失败，请重试");
        }


        BigDecimal withdrawalFee = new BigDecimal(iSysConfigService.selectConfigByKey("withdrawalFee"));
        // 5. 创建并保存提现申请记录
        BizWithdrawalsBo withdrawalBo = new BizWithdrawalsBo();
        withdrawalBo.setUserId(userId);
        withdrawalBo.setAmount(withdrawalAmount);
        withdrawalBo.setNetworkFee(withdrawalFee);
        withdrawalBo.setFinalAmount(withdrawalAmount.subtract(withdrawalFee));
        withdrawalBo.setToWalletAddress(applyBo.getToWalletAddress());
        withdrawalBo.setStatus("PENDING"); // 初始状态为待审核
        withdrawalBo.setRequestTime(new Date()); // 使用java.util.Date
        withdrawalBo.setUserName(username);
        this.insertByBo(withdrawalBo);

        // 6. 创建关联的交易流水记录
        BizTransactionsBo transactionBo = new BizTransactionsBo();
        transactionBo.setUserId(userId);
        // 交易流水中金额记录为负数，表示支出
        transactionBo.setAmount(withdrawalAmount.negate());
        transactionBo.setCurrency("USDT");
        transactionBo.setTransactionType("WITHDRAWAL");
        transactionBo.setStatus("PENDING"); // 流水状态与提现申请状态保持一致
        transactionBo.setBlockchainNetwork("BSC");
        transactionBo.setToAddress(applyBo.getToWalletAddress());
        transactionBo.setUserName(username);
        // 将提现申请ID关联到流水记录，便于追溯
        transactionBo.setSourceId(withdrawalBo.getWithdrawalId().toString());
        transactionBo.setRemarks("用户申请提现，待审核");
        bizTransactionsService.insertByBo(transactionBo);

        log.info("用户ID: {} 成功提交提现申请，申请ID: {}", userId, withdrawalBo.getWithdrawalId());
    }
}
