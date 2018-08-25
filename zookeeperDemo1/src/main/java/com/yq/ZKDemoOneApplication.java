package com.yq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Simple to Introduction
 * className: ZKDemoOneApplication
 *
 * @author EricYang
 * @version 2018/8/24 23:43
 */


@SpringBootApplication
public class ZKDemoOneApplication {
    private static final Logger log = LoggerFactory.getLogger(ZKDemoOneApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ZKDemoOneApplication.class, args);
        log.info("Spring Boot start done.");
    }
}