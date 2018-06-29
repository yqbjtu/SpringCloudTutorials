package com.yq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient


public class EurekaUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaUserApplication.class, args);
	}
}
