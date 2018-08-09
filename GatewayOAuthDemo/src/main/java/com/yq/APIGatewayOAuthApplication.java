package com.yq;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;



@SpringCloudApplication

public class APIGatewayOAuthApplication {
    private static final Logger logger = LoggerFactory.getLogger(APIGatewayOAuthApplication.class);

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                //basic proxy
                .route(r -> r.path("/baidu")
                        .uri("http://baidu.com:80/")
                )
               .route("websocket_route", r -> r.path("/apitopic1/**")
                .uri("ws://127.0.0.1:6605"))
                .route(r -> r.path("/userapi3/**")
                        .filters(f -> f.addResponseHeader("X-AnotherHeader", "testapi3"))

                        .uri("lb://user-service/")
                )
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(APIGatewayOAuthApplication.class, args);
        logger.info(" Start APIGatewayApplication Done");
    }

    /*
    当请求的路径为 /testfun 时，直接返回ok的状态码，且响应体为 hello 字符串。
     */
    @Bean
    public RouterFunction<ServerResponse> testFunRouterFunction() {
        RouterFunction<ServerResponse> route = RouterFunctions.route(
                RequestPredicates.path("/testfun"),
                request -> ServerResponse.ok().body(BodyInserters.fromObject("hello")));
        return route;
    }


}


