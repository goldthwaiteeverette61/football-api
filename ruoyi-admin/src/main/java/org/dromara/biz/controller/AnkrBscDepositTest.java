//package org.dromara.biz.controller;
//
//import org.web3j.abi.FunctionEncoder;
//import org.web3j.abi.TypeReference;
//import org.web3j.abi.datatypes.Address;
//import org.web3j.abi.datatypes.Function;
//import org.web3j.abi.datatypes.Type;
//import org.web3j.abi.datatypes.generated.Uint256;
//import org.web3j.protocol.Web3j;
//import org.web3j.protocol.core.DefaultBlockParameterName;
//import org.web3j.protocol.core.methods.request.Transaction;
//import org.web3j.protocol.core.methods.response.EthCall;
//import org.web3j.protocol.http.HttpService;
//
//import java.math.BigDecimal;
//import java.math.BigInteger;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.concurrent.TimeUnit;
//
///**
// * 基于Ankr的BSC RPC Java测试代码
// * 使用Web3j库（推荐Maven依赖：org.web3j:core:4.9.8 或最新版）
// * 功能：
// * 1. 连接Ankr公共RPC，查询最新块号（测试连接）
// * 2. 查询指定地址的USDT余额（示例代币充值检测）
// * 3. （可选）轮询监控余额变化（模拟充值监听）
// *
// * Ankr公共RPC: https://rpc.ankr.com/bsc (免费，每日限额300K请求，适合测试)
// * USDT合约地址（BSC主网）: 0x55d398326f99059fF775485246999027B3197955
// *
// * 运行前：Maven添加 <dependency><groupId>org.web3j</groupId><artifactId>core</artifactId><version>4.9.8</version></dependency>
// */
//public class AnkrBscDepositTest {
//
//    // Ankr BSC公共RPC URL
//    private static final String ANKR_RPC_URL = "https://rpc.ankr.com/bsc";
//
//    // 示例：监听的钱包地址（替换为你的充值地址）
//    private static final String DEPOSIT_ADDRESS = "0xYourDepositAddressHere";
//
//    // USDT合约地址 (BEP20)
//    private static final String USDT_CONTRACT = "0x55d398326f99059fF775485246999027B3197955";
//
//    // Web3j实例
//    private static Web3j web3j;
//
//    public static void main(String[] args) throws Exception {
//        // 初始化Web3j连接Ankr RPC
//        web3j = Web3j.build(new HttpService(ANKR_RPC_URL));
//        System.out.println("连接Ankr BSC RPC成功！");
//
//        // 测试1: 查询最新块号
//        BigInteger latestBlock = web3j.ethBlockNumber().send().getBlockNumber();
//        System.out.println("最新块号: " + latestBlock);
//
//        // 测试2: 查询当前USDT余额
//        BigDecimal balance = getUsdtBalance(DEPOSIT_ADDRESS);
//        System.out.println("当前USDT余额: " + balance + " USDT");
//
//        // 测试3: 简单轮询监控充值（每10秒查一次，检测变化，运行5分钟演示）
//        monitorDeposits(DEPOSIT_ADDRESS, 10, 30);  // 每10秒查，运行30次
//
//        // 关闭连接
//        web3j.shutdown();
//    }
//
//    /**
//     * 查询USDT余额
//     */
//    private static BigDecimal getUsdtBalance(String address) throws Exception {
//        Function function = new Function("balanceOf",
//            Arrays.asList(new Address(address)),
//            Arrays.asList(new TypeReference<Uint256>() {}));
//
//        String encodedFunction = FunctionEncoder.encode(function);
//
//        EthCall response = web3j.ethCall(
//                Transaction.createEthCallTransaction(address, USDT_CONTRACT, encodedFunction),
//                DefaultBlockParameterName.LATEST)
//            .send();
//
//        if (response.hasError()) {
//            throw new RuntimeException("查询失败: " + response.getError().getMessage());
//        }
//
//        BigInteger balance = (BigInteger) org.web3j.abi.datatypes.TypeDecoder.decodeNumeric(
//            response.getValue(), Uint256.class).getValue();
//
//        // USDT有18位小数
//        return new BigDecimal(balance).divide(BigDecimal.TEN.pow(18));
//    }
//
//    /**
//     * 轮询监控充值（生产建议用事件订阅WebSocket，或自建节点）
//     * @param address 监听地址
//     * @param intervalSeconds 轮询间隔
//     * @param times 轮询次数
//     */
//    private static void monitorDeposits(String address, int intervalSeconds, int times) throws Exception {
//        BigDecimal previousBalance = getUsdtBalance(address);
//        System.out.println("开始监控充值，初始余额: " + previousBalance + " USDT");
//
//        for (int i = 0; i < times; i++) {
//            TimeUnit.SECONDS.sleep(intervalSeconds);
//            BigDecimal currentBalance = getUsdtBalance(address);
//
//            if (currentBalance.compareTo(previousBalance) > 0) {
//                BigDecimal depositAmount = currentBalance.subtract(previousBalance);
//                System.out.println("检测到充值！金额: " + depositAmount + " USDT");
//                // 这里添加业务逻辑：更新数据库、通知用户等
//            } else {
//                System.out.println("无新充值，当前余额: " + currentBalance + " USDT");
//            }
//            previousBalance = currentBalance;
//        }
//        System.out.println("监控结束");
//    }
//}
