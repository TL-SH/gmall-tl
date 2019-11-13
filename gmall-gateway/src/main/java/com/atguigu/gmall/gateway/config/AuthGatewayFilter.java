package com.atguigu.gmall.gateway.config;

import com.atguigu.core.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author tanglei
 *
 * 过滤器
 */
@Component
@EnableConfigurationProperties({JwtProperties.class})
public class AuthGatewayFilter implements GatewayFilter, Ordered {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 拦截的方法
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //获取cookie 里面的token
        MultiValueMap<String, HttpCookie> cookies = request.getCookies();
        //判断cookie 是否存在,不存在就重定向到登录页面
        if(cookies==null || !cookies.containsKey(this.jwtProperties.getCookieName())){
            // 设置响应状态码为未验证,结束请求
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        //存在,解析cookie
        HttpCookie cookie = cookies.getFirst(this.jwtProperties.getCookieName());
        try {
            JwtUtils.getInfoFromToken(cookie.getValue(),this.jwtProperties.getPublicKey());
        } catch (Exception e) {
            e.printStackTrace();
            // 设置响应状态码为未验证,结束请求
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();

        }
        //认证通过放行
        return chain.filter(exchange);
    }

    /**
     * 数字的越低级别越高
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
