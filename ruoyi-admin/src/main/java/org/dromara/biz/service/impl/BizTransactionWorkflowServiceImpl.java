
// 文件路径: org/dromara/biz/service/impl/BizTransactionWorkflowServiceImpl.java
package org.dromara.biz.service.impl;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.biz.domain.bo.BizTransactionsBo;
import org.dromara.biz.domain.bo.TransactionHistoryBo;
import org.dromara.biz.domain.dto.UserTransferDto;
import org.dromara.biz.domain.vo.TransactionHistoryVo;
import org.dromara.biz.mapper.BizTransactionsMapper;
import org.dromara.biz.service.IBizTransactionWorkflowService;
import org.dromara.biz.service.IBizTransactionsService;
import org.dromara.common.core.domain.model.LoginUser;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.system.domain.vo.SysUserVo;
import org.dromara.system.service.ISysUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BizTransactionWorkflowServiceImpl implements IBizTransactionWorkflowService {


    private final ISysUserService sysUserService;
    private final IBizTransactionsService bizTransactionsService;
    private final BizTransactionsMapper bizTransactionsMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initiateTransfer(UserTransferDto dto) {
        LoginUser loginUser = LoginHelper.getLoginUser();
        Long fromUserId = loginUser.getUserId();
        String toUserName = dto.getToUserName();

        sysUserService.verifyPayPassword(fromUserId,dto.getPayPassword());

        // 1. 查找收款人
        SysUserVo toUser = sysUserService.selectUserByUserName(toUserName);
        if (toUser == null) {
            throw new ServiceException("收款用户不存在");
        }
        Long toUserId = toUser.getUserId();

        // 2. 校验不能给自己转账
        if (fromUserId.equals(toUserId)) {
            throw new ServiceException("不能给自己转账");
        }

        // 3. 执行原子性资金划转
        // 3.1 扣除转账人余额
        boolean deductSuccess = sysUserService.deductBalance(fromUserId, dto.getAmount());
        if (!deductSuccess) {
            throw new ServiceException("您的余额不足");
        }
        // 3.2 增加收款人余额
        boolean addSuccess = sysUserService.addBalance(toUserId, dto.getAmount());
        if (!addSuccess) {
            throw new ServiceException("系统错误，收款人账户异常"); // 抛出异常以回滚事务
        }

        // 4. 创建转账流水记录
        String referenceId = UUID.fastUUID().toString(); // 生成唯一的关联ID

        // 4.1 创建转出记录
        BizTransactionsBo transferOutBo = new BizTransactionsBo();
        transferOutBo.setUserId(fromUserId);
        transferOutBo.setUserName(loginUser.getUsername());
        transferOutBo.setAmount(dto.getAmount().negate()); // 金额为负数
        transferOutBo.setCurrency("USDT");
        transferOutBo.setTransactionType("INTERNAL_TRANSFER_OUT");
        transferOutBo.setStatus("CONFIRMED");
        transferOutBo.setReferenceId(referenceId);
        transferOutBo.setRemarks("转给 "+toUserName+"，备注："+dto.getRemark());
        bizTransactionsService.insertByBo(transferOutBo);

        // 4.2 创建转入记录
        BizTransactionsBo transferInBo = new BizTransactionsBo();
        transferInBo.setUserId(toUserId);
        transferInBo.setUserName(toUserName);
        transferInBo.setAmount(dto.getAmount()); // 金额为正数
        transferInBo.setCurrency("USDT");
        transferInBo.setTransactionType("INTERNAL_TRANSFER_IN");
        transferInBo.setStatus("CONFIRMED");
        transferInBo.setReferenceId(referenceId);
        transferInBo.setRemarks("来自 "+transferOutBo.getUserName()+"，备注："+dto.getRemark());
        bizTransactionsService.insertByBo(transferInBo);

        log.info("用户 {} 向用户 {} 成功转账 {}", fromUserId, toUserId, dto.getAmount());
    }

    @Override
    public TableDataInfo<TransactionHistoryVo> queryMyTransactionHistory(TransactionHistoryBo bo, PageQuery pageQuery) {
        // 获取当前登录的用户ID
        Long userId = LoginHelper.getUserId();
        // 调用 Mapper 方法，传入分页对象、用户ID和筛选条件
        Page<TransactionHistoryVo> page = bizTransactionsMapper.selectMyHistoryPage(pageQuery.build(), userId, bo);
        return TableDataInfo.build(page);
    }
}
