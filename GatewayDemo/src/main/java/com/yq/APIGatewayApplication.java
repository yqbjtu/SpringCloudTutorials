package com.yq;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;


//@SpringCloudApplication 包含了@SpringBootApplication和@EnableDiscoveryClient
@SpringCloudApplication
public class APIGatewayApplication  {
    private static final Logger logger = LoggerFactory.getLogger(APIGatewayApplication.class);

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                //basic proxy
                .route(r -> r.path("/baidu")
                        .uri("http://baidu.com:80/")
                )
//                .route(r -> r.path("/userapi/**")
//                        .uri("lb://user-service/")
//                )
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(APIGatewayApplication.class, args);
        logger.info(" Start APIGatewayApplication Done");
    }



}


