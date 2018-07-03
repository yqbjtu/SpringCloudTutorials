/*
 * Copyright (C) 2018 org.citic.iiot, Inc. All Rights Reserved.
 */


package com.yq.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
public class WebSocketSendSvc {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketSendSvc.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void send(String topic, String messageBody) {
        logger.debug("messageBody={}", messageBody);
        messagingTemplate.convertAndSend(topic, messageBody);
    }
}
