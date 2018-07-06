package com.yq.controller;

import com.alibaba.fastjson.JSONObject;
import com.yq.domain.Greeting;
import com.yq.domain.HelloMessage;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDateTime;

@RestController
public class GreetingController {

    private static final Logger log = LoggerFactory.getLogger(GreetingController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private static final String TOPIC_GREETING = "/topic/greetings";

    @MessageMapping("/hello")
    @SendTo(TOPIC_GREETING)
    //虽然在inbound中给message增加了新的字段，但是当json解析成message.getName()，也就是只需要name部分，而jsonObj.put(logFlag + "ChannelContent2", "add to");
    //被丢弃了。但是因为我们重新sendTo了一遍，所以又加上{"InboundChannelContent2":"add to","content":"Hello1, Inboundadd to qqq!","name":"Inboundadd to null"}
    public Greeting greeting(HelloMessage message) throws Exception {
        log.info("greeting to " + TOPIC_GREETING + " with {}", message.getName());
        Thread.sleep(1000); // simulated delay
        return new Greeting("Hello1, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }


    @ApiOperation(value = "sendMessage", notes = "")
    @ApiImplicitParam(name = "message", value = "message, 直接发送到/topic/greetings没有经过inbound的filter", required = true, dataType = "String", paramType = "query")
    @PostMapping(value = "/message", produces = "application/json;charset=UTF-8")
    public String sendMessage(@RequestParam String message) {
        String getTimestamp = LocalDateTime.now().toString();
        String text = "[" + getTimestamp + "]:" + message;

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("currentTime", getTimestamp);
        jsonObj.put("content", message);

        messagingTemplate.convertAndSend(TOPIC_GREETING, jsonObj.toJSONString());
        log.info("sendMessage to " + TOPIC_GREETING + " with {}", message);
        return jsonObj.toJSONString();
    }
}
