package com.yq.exceptiondemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class ExecptionDemoApplication {
    private static final Logger log = LoggerFactory.getLogger(ExecptionDemoApplication.class);

    public static void main(String[] args) {

        SpringApplication.run(ExecptionDemoApplication.class, args);
        log.info("Spring Boot start done!");
    }

}
