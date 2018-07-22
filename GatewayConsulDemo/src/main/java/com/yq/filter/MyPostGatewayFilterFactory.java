package com.yq.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractNameValueGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Simple to Introduction
 * className: MyPreGatewayFilterFactory
 *  对于Post类型的Filter，SetStatus
 代码将会在chain.filter(exchange).then()里面的代码运行。

 https://github.com/spring-cloud/spring-cloud-gateway/blob/master/spring-cloud-gateway-core/src/main/java/org/springframework/cloud/gateway/filter/factory/AddResponseHeaderGatewayFilterFactory.java
 * @author EricYang
 * @version 2018/7/12 10:15
 */
@Component
@Slf4j
public class MyPostGatewayFilterFactory extends AbstractNameValueGatewayFilterFactory {
    @Override
    public GatewayFilter apply(NameValueConfig config) {
        log.info("MyPostGatewayFilterFactory name={}, value={}", config.getName(), config.getValue());

        return (exchange, chain) -> {
            exchange.getResponse().getHeaders().add(config.getName(), config.getValue());

            return chain.filter(exchange);
        };

    }
}

