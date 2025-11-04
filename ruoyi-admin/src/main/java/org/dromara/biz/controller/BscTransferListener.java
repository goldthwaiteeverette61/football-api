package org.dromara.biz.controller;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.websocket.WebSocketService;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

public class BscTransferListener {

    private static final String WS_RPC_URL = "wss://rpc.ankr.com/bsc/ws";
    private static final String DEPOSIT_ADDRESS = "0xe7bdfaa877cc4c74f5aa72b7ac66a2856d69ec26";
    private static final String TOKEN_CONTRACT = "0xF761eA5f045eCB07d2bB57ce6C87D98d9FE4EAc0";
    private static final int DECIMALS = 18;

    private static BigDecimal systemBalance = BigDecimal.ZERO;

    public static void main(String[] args) throws Exception {
        WebSocketService ws = new WebSocketService(WS_RPC_URL, false);
        ws.connect();
        Web3j web3j = Web3j.build(ws);

        System.out.println("已连接 Ankr BSC WebSocket，开始监听充值...");

        // 正确构造 EthFilter（关键！）
        EthFilter filter = new EthFilter(
            DefaultBlockParameterName.LATEST,
            DefaultBlockParameterName.PENDING,
            TOKEN_CONTRACT
        );

        // Transfer 事件签名
        Event transferEvent = new Event("Transfer", Arrays.asList(
            new TypeReference<Address>(true) {},
            new TypeReference<Address>(true) {},
            new TypeReference<Uint256>() {}
        ));
        String eventSig = EventEncoder.encode(transferEvent);
        filter.addSingleTopic(eventSig);

        // 正确：Address.getValue() 返回 BigInteger
        String paddedTo = Numeric.toHexStringWithPrefixZeroPadded(
            new BigInteger(new Address(DEPOSIT_ADDRESS).getValue()),  // BigInteger
            64                                          // 补齐 64 位（32 字节）
        );
        filter.addOptionalTopics(paddedTo);

        // 订阅
        web3j.ethLogFlowable(filter).subscribe(log -> {
            if (log.getTopics().size() < 3) return;

            String txHash = log.getTransactionHash();
            String from = new Address(Numeric.toBigInt(log.getTopics().get(1)).toString()).toString();
            String to = new Address(Numeric.toBigInt(log.getTopics().get(2)).toString()).toString();

            if (!to.equalsIgnoreCase(DEPOSIT_ADDRESS)) return;

            BigInteger value = Numeric.toBigInt(log.getData());
            BigDecimal amount = new BigDecimal(value).divide(BigDecimal.TEN.pow(DECIMALS));

            System.out.println("\n检测到充值！");
            System.out.println("  TxHash: " + txHash);
            System.out.println("  From: " + from);
            System.out.println("  To: " + DEPOSIT_ADDRESS);
            System.out.println("  金额: " + amount);
            System.out.println("  查看: https://bscscan.com/tx/" + txHash);

            // 更新系统余额（替换为你的业务逻辑）
            systemBalance = systemBalance.add(amount);
            System.out.println("  系统余额: " + systemBalance);
        });

        System.out.println("监听中... (Ctrl+C 停止)");
        Thread.currentThread().join();
    }
}
