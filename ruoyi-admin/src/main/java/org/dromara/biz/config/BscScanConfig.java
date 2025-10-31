package org.dromara.biz.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * BSCScan API 配置类
 * @description: 从 application.yml 文件中读取 bscscan 相关的配置项
 */
@Data
@Component
@ConfigurationProperties(prefix = "bscscan")
public class BscScanConfig {
    private String apiUrl;
    private String apiKey;
    private String usdtContractAddress;
    private String net;
    private int chainId;
    private String wssUrl;
}
