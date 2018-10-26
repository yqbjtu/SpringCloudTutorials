package com.yq.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yq.WebSocketApplication;
import com.yq.domain.Greeting;
import com.yq.domain.HelloMessage;
import com.yq.service.WebSocketSendSvc;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

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

    @MessageMapping("/hello2")
    @SendTo("/topic/myTopic02")
    public Greeting greeting(String msg) throws Exception {
        Thread.sleep(1000); // simulated delay
        log.info("send to /topic/myTopic02 with ", msg);
        return new Greeting("Hello2, " + HtmlUtils.htmlEscape(msg) + "!");
    }

    @ApiOperation(value = "sendMessage", notes = "")
    @ApiImplicitParam(name = "message",  value = "message, 直接发送到/topic/greetings没有经过inbound的filter", required = true, dataType = "String", paramType = "query")
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

    @ApiOperation(value = "messageWithTopic")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "topic", defaultValue = "/topic/app01", value = "topic", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "message", defaultValue = "{\"yqvalue\":\"test1\"}", value = "message", required = true, dataType = "string", paramType = "query"),
    })
    @PostMapping(value = "/messageWithTopic", produces = "application/json;charset=UTF-8")
    public String messageWithTopic(@RequestParam String topic,  @RequestParam String message) {
        String getTimestamp = LocalDateTime.now().toString();

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("currentTime", getTimestamp);
        jsonObj.put("topic", topic);
        jsonObj.put("content", message);

        messagingTemplate.convertAndSend(topic, message);
        log.info("sendMessage to {} with {}", topic, message);
        return jsonObj.toJSONString();
    }

    @ApiOperation(value = "测试告警消息的url输出", notes = "private ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "alarmMsg", defaultValue = "{\"L001\":50, \"S001\":80}", value = "json字符串格式", example="{\"L001\":50, \"S001\":80}",
                    required = true, dataType = "string", paramType = "body")
    })
    @PostMapping (value = "/rules/alarmMsgUrlTest", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String alarmMsgUrlTest(@RequestBody String alarmMsg, HttpServletRequest request){
        log.info("Enter alarmMsgUrlTest alarmMsg={}", alarmMsg);

        Map<String, String> map = new HashMap<String, String>();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        String headerStr = JSON.toJSONString(map);
        log.info("Enter alarmMsgUrlTest headerStr={}", headerStr);

        return alarmMsg + headerStr;
    }

}
