package com.atguigu.gmall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;

import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * @author tanglei
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter(){
        //初始化cors对象
        CorsConfiguration configuration = new CorsConfiguration();
        //允许请求的域,不要写* ,不然无法保存cookie
        configuration.addAllowedOrigin("http://localhost:1000");
        //允许请求的头
        configuration.addAllowedHeader("*");
        //允许的请求的方法
        configuration.addAllowedMethod("*");

        //设置是否携带cookie信息
        configuration.setAllowCredentials(true);

        //添加映射路径,拦截一切请求
        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
        corsConfigurationSource.registerCorsConfiguration("/**",configuration);


        return new CorsWebFilter(corsConfigurationSource);
    }

}
