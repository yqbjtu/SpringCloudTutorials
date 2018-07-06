

package com.yq.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.TextMessage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple to Introduction
 * className: SocketController
 *
 * @author EricYang
 * @version 2018/5/20 14:47
 */


@RestController
public class SocketController {

    private static final Logger log = LoggerFactory.getLogger(GreetingController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @ApiOperation(value = "messageWithTopic")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "topic", value = "topic", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "message", value = "message", required = true, dataType = "string", paramType = "query"),
    })
    @PostMapping(value = "/messageWithTopic", produces = "application/json;charset=UTF-8")
    public String messageWithTopic(@RequestParam String topic, @RequestParam String message) {
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