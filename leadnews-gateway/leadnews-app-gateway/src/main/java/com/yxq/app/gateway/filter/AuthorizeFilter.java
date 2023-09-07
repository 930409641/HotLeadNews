package com.yxq.app.gateway.filter;

import com.yxq.app.gateway.utils.AppJwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Description:
 * @Author: yxq
 * @Date: 2023/9/7
 */
@Component
@Slf4j
public class AuthorizeFilter implements Ordered, GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        //判断是否登录
        if(request.getURI().getPath().contains("/login")) {
            //已登录，就放行
            return chain.filter(exchange);
        }

        //判断token是否存在
        String token = request.getHeaders().getFirst("token");
        if(StringUtils.isBlank(token)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        //判断token是否正确
        try {
            Claims claimsBody = AppJwtUtil.getClaimsBody(token);
            //判断是否过期
            int verifyToken = AppJwtUtil.verifyToken(claimsBody);
            if(verifyToken == 1 || verifyToken == 2) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }

        }catch (Exception e) {
            e.printStackTrace();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        //放行
        return chain.filter(exchange);
    }

    /**
     * 优先级，数越小，优先级越高
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
