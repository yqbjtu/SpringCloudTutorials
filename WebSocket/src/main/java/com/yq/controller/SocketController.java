//
//
//package com.yq.controller;
//
//import com.yq.config.MyHandler;
//import io.swagger.annotations.ApiImplicitParam;
//import io.swagger.annotations.ApiOperation;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.socket.TextMessage;
//
//import javax.servlet.http.HttpSession;
//
///**
// * Simple to Introduction
// * className: SocketController
// *
// * @author EricYang
// * @version 2018/5/20 14:47
// */
//
//
//@Controller
//public class SocketController {
//
//    @Autowired
//    MyHandler handler;
//
//    @ApiOperation(value = "sendMessage2")
//    @ApiImplicitParam(name = "message", value = "message", required = true, dataType = "String", paramType = "query")
//    @RequestMapping("/login/{userId}")
//    public @ResponseBody
//    String login(HttpSession session, @PathVariable("userId") Integer userId) {
//        System.out.println("登录接口,userId="+userId);
//        session.setAttribute("userId", userId);
//        System.out.println(session.getAttribute("userId"));
//
//        return "success";
//    }
//
//    @ApiOperation(value = "sendMessage2")
//    @ApiImplicitParam(name = "message", value = "message", required = true, dataType = "String", paramType = "query")
//    @RequestMapping("/message")
//    public @ResponseBody String sendMessage() {
//        boolean hasSend = handler.sendMessageToUser(4, new TextMessage("发送一条msg"));
//        System.out.println(hasSend);
//        return "message";
//    }
//
//}