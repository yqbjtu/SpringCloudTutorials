package com.yq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class RestTemplateApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestTemplateApplication.class, args);
        log.info("RestTemplateApplication Start done.");
	}
}
