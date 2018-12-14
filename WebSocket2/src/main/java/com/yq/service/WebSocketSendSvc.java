
package com.yq.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * Simple to Introduction
 * className: WebSocketSendSvc
 *
 * @author EricYang
 * @version 2018/7/2 18:13
 */

@Service
@Slf4j
public class WebSocketSendSvc {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void send(String topic, String messageBody) {
        log.debug("topic={}, messageBody={}", messageBody);
        messagingTemplate.convertAndSend(topic, messageBody);
    }
}
