package com.yq.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * Simple to Introduction
 * className: CustomFilter
 *
 * @author EricYang
 * @version 2018/7/11 18:52
 */
public class CustomFilter implements GatewayFilter, Ordered {

    private static final Log log = LogFactory.getLog(GatewayFilter.class);
    private static final String COUNT_START_TIME = "countStartTime";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        exchange.getAttributes().put(COUNT_START_TIME, System.currentTimeMillis());

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        URI uri = request.getURI();
        return chain.filter(exchange).
                then(
                Mono.fromRunnable(() -> {
                    Long startTime = exchange.getAttribute(COUNT_START_TIME);
                    Long endTime=(System.currentTimeMillis() - startTime);

                    String path = uri.getPath();
                    int index = path.indexOf("api2");
                    if (startTime != null) {
                        log.info(exchange.getRequest().getURI().getRawPath() + ": " + endTime + "ms");
                    }
                })
        );
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}