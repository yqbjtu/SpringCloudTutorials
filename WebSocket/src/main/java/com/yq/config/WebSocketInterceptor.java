//
//
//package com.yq.config;
//
//import org.springframework.http.server.ServerHttpRequest;
//import org.springframework.http.server.ServerHttpResponse;
//import org.springframework.http.server.ServletServerHttpRequest;
//import org.springframework.web.socket.WebSocketHandler;
//import org.springframework.web.socket.server.HandshakeInterceptor;
//
//import javax.servlet.http.HttpSession;
//import java.util.Map;
//
///**
// * Simple to Introduction
// * className: WebSocketInterceptor
// *
// * @author EricYang
// * @version 2018/5/20 14:42
// */
//
//public class WebSocketInterceptor implements HandshakeInterceptor {
//
//    @Override
//    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler handler, Map<String, Object> map) throws Exception {
//        if (request instanceof ServletServerHttpRequest) {
//            ServletServerHttpRequest serverHttpRequest = (ServletServerHttpRequest) request;
//            HttpSession session = serverHttpRequest.getServletRequest().getSession();
////            Map parameterMap = serverHttpRequest.getServletRequest().getParameterMap();
////            System.out.println(parameterMap);
//            if (session != null) {
//                map.put("userId", session.getAttribute("userId"));
//            }
//
//        }
//        return true;
//    }
//
//    @Override
//    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {
//
//    }
//}
