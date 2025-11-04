package org.dromara.biz.config;

import lombok.RequiredArgsConstructor;
// 1. 导入 Spring 的 @Configuration 和 @Bean
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// 2. 移除 anyline 的 @Component
// import org.anyline.annotation.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@RequiredArgsConstructor
// 3. 使用 @Configuration
@Configuration
public class BscNodeConfig {

    private final BscScanConfig bscScanConfig;

    /**
     * 5. 定义一个 @Bean 方法来创建 Web3j 实例
     * Spring 会自动将其注册为单例 Bean
     * @return Web3j 实例
     */
    @Bean
    public Web3j web3j() {
        // Spring 的 @Bean 默认是单例的，所以这里是线程安全的
        return Web3j.build(new HttpService(bscScanConfig.getApiUrl()));
    }

}

