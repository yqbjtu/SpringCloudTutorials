package com.yq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

@SpringBootApplication
@EnableDiscoveryClient
public class ConsulCuratorApplication {
	private static final Logger logger = LoggerFactory.getLogger(ConsulCuratorApplication.class);

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(ConsulCuratorApplication.class, args);
		String[] activeProfiles = ctx.getEnvironment().getActiveProfiles();
		if (activeProfiles == null || activeProfiles.length == 0) {
			logger.info("没有设置profile");
		}
		Arrays.asList(activeProfiles).forEach(
				profile -> logger.info("Spring Boot profile为 {}", profile)
		);
		logger.info("ConsulCuratorApplication Start done.");
	}
}
