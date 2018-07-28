package com.yq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication

@Slf4j
public class StaticResDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(StaticResDemoApplication.class, args);
		log.info("Spring Boot start done!");
	}

}
