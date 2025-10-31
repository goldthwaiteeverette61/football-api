package org.dromara.biz.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.biz.domain.bo.BizDepositWalletsBo;
import org.dromara.biz.domain.vo.BizDepositWalletsVo;
import org.dromara.biz.service.IBizDepositWalletsService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.system.domain.bo.SysUserBo;
import org.dromara.system.domain.vo.SysUserVo;
import org.dromara.system.service.ISysUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户钱包分配服务
 * @description: 为系统中没有充值钱包的用户，从预备库存中分配BSC钱包。
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UserWalletAssignmentServiceImpl {

    private final ISysUserService sysUserService;
    private final IBizDepositWalletsService bizDepositWalletsService;

    /**
     * 为所有没有钱包的用户分配新的 BSC 钱包。
     * 这是一个幂等操作，重复执行不会为已有钱包的用户重复创建。
     * @return 返回处理结果的摘要信息。
     */
    @Transactional(rollbackFor = Exception.class)
    public String assignWalletsToAllUsers() {
        log.info("开始为所有用户分配BSC充值钱包...");

        // 1. 找出已有有效钱包的用户ID集合，确保操作的幂等性
        BizDepositWalletsBo activeWalletQuery = new BizDepositWalletsBo();
        activeWalletQuery.setStatus("active");
        List<BizDepositWalletsVo> activeWallets = bizDepositWalletsService.queryList(activeWalletQuery);
        Set<Long> usersWithWallets = activeWallets.stream()
            .map(BizDepositWalletsVo::getUserId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        log.info("查询到 {} 个用户已有有效钱包。", usersWithWallets.size());

        // 2. 筛选出真正需要分配钱包的用户
        List<SysUserVo> usersToAssign = sysUserService.selectUserList(new SysUserBo()).stream()
            .filter(user -> !usersWithWallets.contains(user.getUserId()))
            .collect(Collectors.toList());

        if (usersToAssign.isEmpty()) {
            String msg = "所有用户均已有钱包，无需分配新钱包。";
            log.info(msg);
            return msg;
        }
        log.info("发现 {} 个用户需要分配钱包。", usersToAssign.size());

        int assignedCount = 0;
        // 3. 遍历需要分配钱包的用户
        for (SysUserVo user : usersToAssign) {
            // 4. 为每个用户单独获取一个未激活的钱包
            BizDepositWalletsBo availableWalletQuery = new BizDepositWalletsBo();
            availableWalletQuery.setStatus("inactive");

            // 每次查询一条可用的钱包
            List<BizDepositWalletsVo> availableWallets = bizDepositWalletsService.queryList(availableWalletQuery);

            if (availableWallets.isEmpty()) {
                String errorMessage = String.format("可用钱包库存不足！已成功分配 %d 个，但库存已用尽。", assignedCount);
                log.error(errorMessage);
                // 抛出异常以回滚已成功分配的记录，确保事务一致性
                throw new ServiceException(errorMessage);
            }

            BizDepositWalletsVo walletToAssign = availableWallets.get(0); // 取出列表中的第一个可用钱包

            // 5. 更新该钱包信息，将其分配给当前用户
            BizDepositWalletsBo walletBo = new BizDepositWalletsBo();
            walletBo.setWalletId(walletToAssign.getWalletId());
            walletBo.setUserId(user.getUserId());
            walletBo.setUserName(user.getUserName());
            walletBo.setStatus("active"); // 更新状态为已激活

            bizDepositWalletsService.updateByBo(walletBo);
            assignedCount++;
            log.info("成功为用户 ID: {} ({}) 分配钱包地址: {}", user.getUserId(), user.getUserName(), walletToAssign.getWalletAddress());
        }

        String resultMessage = String.format("任务完成：成功为 %d 个新用户分配了钱包。", assignedCount);
        log.info(resultMessage);
        return resultMessage;
    }
}
