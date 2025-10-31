package org.dromara.system.util;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import okhttp3.*;

public class ImageTranslator {
    private static final String API_URL = "http://api.tosoiot.com";
    private static final String USER_KEY = "7696769912"; // 替换为您的 UserKey
    private static final String IMG_TRANS_KEY = "4599181078"; // 替换为您的 ImgTransKey

//    public static void main(String[] args) {
//        String imageUrl = "https://img.xx.com/O107064055.jpg"; // 替换为图片 URL
//        String sourceLang = "CHS"; // 源语言
//        String targetLang = "KOR"; // 目标语言
//
//        try {
//            String result = translateImageUrl(imageUrl, sourceLang, targetLang);
//            System.out.println("翻译结果: " + result);
//        } catch (Exception e) {
//            System.err.println("请求失败: " + e.getMessage());
//        }
//    }

    public static String translateImageUrl(String imageUrl, String sourceLang, String targetLang) throws Exception {
        // 生成时间戳和签名
        String commitTime = String.valueOf(System.currentTimeMillis() / 1000);
        String sign = generateSign(commitTime);

        // 对图片 URL 进行 URL 编码
        String encodedImageUrl = URLEncoder.encode(imageUrl, StandardCharsets.UTF_8.toString());

        // 构建请求参数
        HttpUrl.Builder urlBuilder = HttpUrl.parse(API_URL).newBuilder();
        urlBuilder.addQueryParameter("Action", "GetImageTranslate");
        urlBuilder.addQueryParameter("SourceLanguage", sourceLang);
        urlBuilder.addQueryParameter("TargetLanguage", targetLang);
        urlBuilder.addQueryParameter("Url", encodedImageUrl);
        urlBuilder.addQueryParameter("ImgTransKey", IMG_TRANS_KEY);
        urlBuilder.addQueryParameter("CommitTime", commitTime);
        urlBuilder.addQueryParameter("Sign", sign);
        urlBuilder.addQueryParameter("NeedWatermark", "0"); // 默认不添加水印
        urlBuilder.addQueryParameter("NeedRmUrl", "1"); // 返回去文字图片链接
        urlBuilder.addQueryParameter("Qos", "LowLatency"); // 偏好速度

        // 发送 GET 请求
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
            .url(urlBuilder.build())
            .get()
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("请求失败: " + response.code() + " " + response.message());
            }
            return response.body().string();
        }
    }

    private static String generateSign(String commitTime) {
        String signStr = commitTime + "_" + USER_KEY + "_" + IMG_TRANS_KEY;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(signStr.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 算法不可用", e);
        }
    }
}
