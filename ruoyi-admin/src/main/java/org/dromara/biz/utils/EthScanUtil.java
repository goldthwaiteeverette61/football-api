package org.dromara.biz.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.dromara.biz.config.BscScanConfig;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

@Slf4j
public class EthScanUtil {

    private static final OkHttpClient httpClient = new OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build();

    private static final Gson gson = new Gson();

    //转账函数
    private static final String TRANSFER_EVENT_TOPIC = "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a5632bdac9ea242e5b9b";

    // 【新增】安全緩衝區塊數量，避免處理可能被重組的最新區塊
    private static final long SAFETY_BUFFER = 60;

    public static JsonArray fetchTransactions(BscScanConfig bscScanConfig,long startBlock, long endBlock) throws IOException {
        String url = String.format(
            "%s?chainid=%d&module=account&action=tokentx&contractaddress=%s&startblock=%d&endblock=%d&topic0=%s&sort=asc&apikey=%s",
            bscScanConfig.getApiUrl(),
            bscScanConfig.getChainId(),
            bscScanConfig.getUsdtContractAddress(),
            startBlock,
            endBlock,
            TRANSFER_EVENT_TOPIC,
            bscScanConfig.getApiKey()
        );

        Request request = new Request.Builder().url(url).get().build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                log.error("BSC - [高效备用轮询] 请求API失败: {}", response);
                return null;
            }

            String responseBody = response.body().string();
            JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);

            if (jsonObject.has("status") && "1".equals(jsonObject.get("status").getAsString()) && jsonObject.has("result") && jsonObject.get("result").isJsonArray()) {
                return jsonObject.getAsJsonArray("result");
            } else if (jsonObject.has("message") && jsonObject.get("message").getAsString().contains("No transactions found")) {
                return new JsonArray(); // 返回空数组表示此区间无记录，是正常情况
            } else {
                log.warn("BSC - [高效备用轮询] API返回异常: status={}, message={}",
                    jsonObject.get("status"), jsonObject.get("message"));
                return null;
            }
        }
    }

    /**
     * 【已重構】獲取最新區塊號，增加了完整的錯誤處理邏輯
     */
    public static long getBscLatestBlockNumber(BscScanConfig bscScanConfig) throws IOException {
        String url = String.format(
            "%s?module=proxy&action=eth_blockNumber&apikey=%s&chainid=%d",
            bscScanConfig.getApiUrl(),
            bscScanConfig.getApiKey(),
            bscScanConfig.getChainId()
        );
        Request request = new Request.Builder().url(url).get().build();
        String responseBody = "";
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                log.error("BSC - 获取最新区块号失败: {}", response);
                throw new IOException("Failed to fetch latest block number from API. Response: " + response);
            }
            responseBody = response.body().string();
            JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);

            if (jsonObject.has("result") && !jsonObject.get("result").isJsonNull()) {
                String result = jsonObject.get("result").getAsString();
                long latestBlock = new BigInteger(result.substring(2), 16).longValue();
                // 應用安全緩衝，返回一個相對安全的、已基本確認的區塊高度
                return latestBlock - SAFETY_BUFFER;
            } else {
                throw new IOException("API did not return a valid block number: " + responseBody);
            }
        } catch (Exception e) {
            // 捕獲所有潛在異常（包括 JSON 解析錯誤），並重新拋出為 IOException
            log.error("BSC - 解析最新区块号响应时出错。API 响应: {}", responseBody, e);
            throw new IOException("Error processing latest block number response.", e);
        }
    }
}
