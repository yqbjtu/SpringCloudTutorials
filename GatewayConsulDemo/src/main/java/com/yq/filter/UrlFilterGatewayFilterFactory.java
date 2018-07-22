package com.yq.filter;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * Simple to Introduction
 * className: UrlFilterGatewayFilterFactory
 *
 * @author EricYang
 * @version 2018/7/8 16:23
 */
@Component
@Slf4j
public class UrlFilterGatewayFilterFactory implements GatewayFilterFactory<Object> {

    @Override
    public Object newConfig() {
       return new String("myConfig");
    }

    @Override
    public GatewayFilter apply(Object o) {
        return (exchange, chain) -> {
            //String token = exchange.getRequest().getHeaders().getFirst("Authorization");
            //String openId =  exchange.getRequest().getQueryParams().getFirst("openId");
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            URI uri = request.getURI();
            log.info("filter. path{}.URL {}.", request.getPath(), uri);

            String path = uri.getPath();
            int index = path.indexOf("api2");
            //如果路径中包含api2也允许通过
            if(index != -1) {
                return chain.filter(exchange);
            }
            else {
                String loginInfo= (String) exchange.getAttribute("LOGIN_INFO");
                String token = exchange.getRequest().getQueryParams().getFirst("token");
                if (StringUtils.isNotBlank(token)) {
                    //查看loginInfo是否有效，无法就必须重新登录， 这里省略
                    return chain.filter(exchange);
                }
                else {
                    log.info("path {}. URL {} has no token.", request.getPath(), uri);
                }
            }

            //设置headers
            HttpHeaders httpHeaders = response.getHeaders();
            httpHeaders.add("Content-Type", "application/json; charset=UTF-8");
            httpHeaders.add("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
            //设置body
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", 500);
            jsonObject.put("message", "未登录");
            DataBuffer bodyDataBuffer = response.bufferFactory().wrap(jsonObject.toJSONString().getBytes());

            return response.writeWith(Mono.just(bodyDataBuffer));
        };
    }
}
