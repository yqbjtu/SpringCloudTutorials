package com.yq.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractNameValueGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

/**
 * Simple to Introduction
 * className: MyPreGatewayFilterFactory
 *  个Pre类型的Filter，code将会在chain.filter() 之前被执行,代
 * @author EricYang
 * @version 2018/7/12 10:15
 */
@Component
@Slf4j
public class MyPreGatewayFilterFactory extends AbstractNameValueGatewayFilterFactory {
    @Override
    public GatewayFilter apply(NameValueConfig config) {
        log.info("MyPreGatewayFilterFactory name={}, value={}", config.getName(), config.getValue());
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest().mutate()
                    .header(config.getName(), config.getValue())
                    .build();
            return chain.filter(exchange.mutate().request(request).build());
        };
    }
}
