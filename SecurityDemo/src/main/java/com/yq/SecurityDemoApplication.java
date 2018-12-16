package com.yq;


import org..Logger;
import org..LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;


@SpringBootApplication
@EnableOAuth2Client
public class SecurityDemoApplication {
    private static final Logger logger = LoggerFactory.getLogger(SecurityDemoApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SecurityDemoApplication.class, args);
        logger.info("Start SecurityDemoApplication Done");
    }

}


