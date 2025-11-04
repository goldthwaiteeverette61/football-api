import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

public class BscUsdtTransferTest {

    private static final String RPC_URL = "https://rpc.ankr.com/bsc_testnet_chapel";
    private static final String USDT_CONTRACT = "0x337610d27c682E347C9cD60BD4b3b107C9d34dDd";
    private static final String PRIVATE_KEY = "17cdc26a68799c28837d39268411f415563ee9b69d1a65948f355dd84ad5f1ac"; // 替换
    private static final String TO_ADDRESS = "0xBA0e02dD1610F717fb1ab8E5Ff3d2c5FD5306ef5";     // 替换

    private static final long CHAIN_ID = 97L; // BSC Testnet

    public static void main(String[] args) throws Exception {
        Web3j web3j = Web3j.build(new HttpService(RPC_URL));
        Credentials credentials = Credentials.create(PRIVATE_KEY);
        String from = credentials.getAddress();

        System.out.println("发送方: " + from);

        // 获取 nonce
        EthGetTransactionCount txCount = web3j.ethGetTransactionCount(
            from, DefaultBlockParameterName.PENDING).send();
        BigInteger nonce = txCount.getTransactionCount();

        // 获取 gasPrice
        EthGasPrice gasPriceResponse = web3j.ethGasPrice().send();
        BigInteger gasPrice = gasPriceResponse.getGasPrice();

        // 构造 transfer data
        String data = encodeTransferData(TO_ADDRESS, Convert.toWei("5", Convert.Unit.ETHER).toBigInteger());

        // 创建交易（6参数）
        RawTransaction rawTx = RawTransaction.createTransaction(
            nonce,
            gasPrice,
            BigInteger.valueOf(100_000L),
            USDT_CONTRACT,
            BigInteger.ZERO,
            data
        );

        // 签名（关键：传入 chainId）
        byte[] signed = TransactionEncoder.signMessage(rawTx, CHAIN_ID, credentials);
        String hexTx = Numeric.toHexString(signed);

        // 发送
        EthSendTransaction response = web3j.ethSendRawTransaction(hexTx).send();
        if (response.hasError()) {
            System.err.println("失败: " + response.getError().getMessage());
        } else {
            String txHash = response.getTransactionHash();
            System.out.println("成功！TxHash: " + txHash);
            System.out.println("查看: https://testnet.bscscan.com/tx/" + txHash);
        }

        web3j.shutdown();
    }

    private static String encodeTransferData(String to, BigInteger amount) {
        String methodId = "a9059cbb";
        String addr = Numeric.cleanHexPrefix(to);
        addr = String.format("%64s", addr).replace(' ', '0');
        String amt = String.format("%64s", amount.toString(16)).replace(' ', '0');
        return "0x" + methodId + addr + amt;
    }
}
