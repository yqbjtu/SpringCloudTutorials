package com.yq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ELKApplication {
	private static final Logger logger = LoggerFactory.getLogger(ELKApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ELKApplication.class, args);
		logger.info("ELKApplication Start done.");
	}
}
