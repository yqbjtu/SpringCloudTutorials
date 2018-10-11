package com.yq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ConsulCommodityApplication {
	private static final Logger logger = LoggerFactory.getLogger(ConsulCommodityApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ConsulCommodityApplication.class, args);
		logger.info("SpringBoot start done.");

	}
}
