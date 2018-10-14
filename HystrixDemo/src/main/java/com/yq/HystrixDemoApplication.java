package com.yq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

@SpringBootApplication
@EnableHystrix
@EnableHystrixDashboard
@EnableDiscoveryClient
@EnableCircuitBreaker
//@EnableTurbine
public class HystrixDemoApplication {
	private static final Logger logger = LoggerFactory.getLogger(HystrixDemoApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(HystrixDemoApplication.class, args);
		logger.info("HystrixDemoApplication Start done.");
	}
}
