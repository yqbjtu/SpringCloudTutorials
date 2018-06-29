//
//package com.yq.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.socket.WebSocketHandler;
//import org.springframework.web.socket.config.annotation.EnableWebSocket;
//import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
//import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
//
///**
// * Simple to Introduction
// * className: MyWebSocketConfig
// *
// * @author EricYang
// * @version 2018/5/20 14:39
// */
//
//@Configuration
//@EnableWebSocket
//public class MyWebSocketConfig implements WebSocketConfigurer {
//
//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry.addHandler(myHandler(), "/myHandler").addInterceptors(new WebSocketInterceptor());
//    }
//
//    @Bean
//    public WebSocketHandler myHandler() {
//        return new MyHandler();
//    }
//}
