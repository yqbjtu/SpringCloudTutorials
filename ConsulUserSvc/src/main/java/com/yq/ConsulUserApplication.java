package com.yq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ConsulUserApplication {
	private static final Logger logger = LoggerFactory.getLogger(ConsulUserApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ConsulUserApplication.class, args);
		logger.info("ConsulUserApplication Start done.");
	}
}
