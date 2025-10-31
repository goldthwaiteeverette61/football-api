package org.dromara.common.web.config;

import org.dromara.common.web.handler.GlobalExceptionHandler;
import org.dromara.common.web.interceptor.PlusWebInvokeTimeInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 通用配置
 *
 * @author Lion Li
 */
@AutoConfiguration
public class ResourcesConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 全局访问性能拦截
        registry.addInterceptor(new PlusWebInvokeTimeInterceptor());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    }

//    @Bean
//    public CorsFilter corsFilter() {
//        CorsConfiguration config = new CorsConfiguration();
//        // 如需携带 Cookie/Authorization 等凭证，必须按来源精确回显，不能用 addAllowedOrigin("*")
//        config.setAllowCredentials(true);
//        // 开发与生产的允许来源（按需增删）
//        config.setAllowedOriginPatterns(Arrays.asList(
//            "http://localhost:8081",     // Expo Web
//            "http://localhost:5173",     // Vue Dev（你已同源代理，保留也无害）
//            "http://127.0.0.1:*",
//            "http://192.168.*:*",
//            "https://score.red",         // 生产前端域（示例，替换为你的实际域）
//            "https://sportlt.dpdns.org"  // 另一可能的前端域（示例，按需）
//        ));
//        config.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
//        // 你项目里会用到的请求头，包含自定义头
//        config.setAllowedHeaders(Arrays.asList(
//            "Authorization","Content-Type","Accept",
//            "Clientid","Encrypt-Key",
//            "X-Requested-With","Origin","Referer","User-Agent"
//        ));
//        // 前端可读取的响应头（按需要补充）
//        config.setExposedHeaders(Arrays.asList("Authorization","Content-Disposition","Content-Length"));
//        config.setMaxAge(1800L);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//        return new CorsFilter(source);
//    }

    /**
     * 跨域配置
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        // 设置访问源地址
        config.addAllowedOriginPattern("*");
        // 设置访问源请求头
        config.addAllowedHeader("*");
        // 设置访问源请求方法
        config.addAllowedMethod("*");
        // 有效期 1800秒
        config.setMaxAge(1800L);
        // 添加映射路径，拦截一切请求
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        // 返回新的CorsFilter
        return new CorsFilter(source);
    }

    /**
     * 全局异常处理器
     */
    @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }
}
