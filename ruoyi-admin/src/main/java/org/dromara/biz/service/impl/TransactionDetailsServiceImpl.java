package org.dromara.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.biz.domain.vo.*;
import org.dromara.biz.service.*;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.system.domain.vo.SysUserVo;
import org.dromara.system.service.ISysUserService;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionDetailsServiceImpl implements ITransactionDetailsService {

    private final IBizTransactionsService bizTransactionsService;
    private final IBizUserFollowsService bizUserFollowsService;
    private final IBizWithdrawalsService bizWithdrawalsService;
    private final ISysUserService sysUserService;
    @Override
    public TransactionDetailsVo getTransactionDetails(Long transactionId) {
        // 1. 从流水表获取基础交易信息
        BizTransactionsVo tx = bizTransactionsService.queryById(transactionId);
        if (tx == null) {
            throw new ServiceException("交易记录不存在");
        }

        // 2. 将基础信息转换为我们的统一详情VO (现在这行代码可以正确工作了)
        TransactionDetailsVo detailsVo = new TransactionDetailsVo();
        BeanUtil.copyProperties(tx,detailsVo);

        String type = tx.getTransactionType();
        String sourceIdStr = tx.getSourceId();

        // 3. 如果有关联的源ID，则根据类型去查询对应的业务详情
        if (StringUtils.isNotBlank(sourceIdStr)) {
            try {
                Long sourceId = Long.parseLong(sourceIdStr);
                switch (type.toUpperCase()) {
                    case "BONUS":
                    case "FOLLOW_BET":
                        BizUserFollowsVo followDetails = bizUserFollowsService.queryDetailsById(sourceId);
                        detailsVo.setBonusDetails(followDetails);
                        break;

                    case "WITHDRAWAL":
                        BizWithdrawalsVo withdrawalDetails = bizWithdrawalsService.queryById(sourceId);
                        detailsVo.setWithdrawalDetails(withdrawalDetails);
                        break;

                    case "INTERNAL_TRANSFER_IN":
                    case "INTERNAL_TRANSFER_OUT":
                        SysUserVo user = sysUserService.selectUserById(sourceId);
                        if (user != null) {
                            TransferDetailsVo transferDetails = new TransferDetailsVo();
                            transferDetails.setOtherPartyUserId(user.getUserId());
                            transferDetails.setOtherPartyUsername(user.getNickName());
                            detailsVo.setTransferDetails(transferDetails);
                        }
                        break;
                }
            } catch (NumberFormatException e) {
                log.warn("交易 {} 的 Source ID '{}' 不是一个有效的数字，无法查询详情。", transactionId, sourceIdStr);
            }
        }

        return detailsVo;
    }
}
