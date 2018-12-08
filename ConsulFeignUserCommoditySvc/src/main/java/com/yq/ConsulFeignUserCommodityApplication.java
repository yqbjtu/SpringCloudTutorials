package com.yq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.yq.client"})
public class ConsulFeignUserCommodityApplication {
	private static final Logger logger = LoggerFactory.getLogger(ConsulFeignUserCommodityApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ConsulFeignUserCommodityApplication.class, args);
		logger.info("SpringBoot start done.");

	}
}
