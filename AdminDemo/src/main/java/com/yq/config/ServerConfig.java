package com.yq.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Simple to Introduction
 * className: ServerConfig
 *
 * @author EricYang
 * @version 2018/9/8 23:39
 */
@Component
@Slf4j
public class ServerConfig implements ApplicationListener<WebServerInitializedEvent> {

    private int serverPort;

    public int getPort() {
        return this.serverPort;
    }

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        this.serverPort = event.getWebServer().getPort();
        log.info("Get WebServer port {}", serverPort);
    }
}

////for 1.5.x  Spring Boot
//public class ServerConfig implements ApplicationListener<EmbeddedServletContainerInitializedEvent> {
//    private int serverPort;;
//
//    public int getPort() {
//        return this.serverPort;
//    }
//
//    @Override
//    public void onApplicationEvent(EmbeddedServletContainerInitializedEvent event) {
//        this.serverPort = event.getEmbeddedServletContainer().getPort();
//    }
//}