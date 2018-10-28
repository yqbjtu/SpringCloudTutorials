package com.yq.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * Simple to Introduction
 * className: MyRunnable
 *
 * @author EricYang
 * @version 2018/10/25 18:11
 */
@Slf4j
public class MyRunnable implements Runnable {
    private SimpMessagingTemplate simpMessagingTemplate;
    private String deviceId;

    public MyRunnable(SimpMessagingTemplate simpMessagingTemplate, String deviceId) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.deviceId = deviceId;
    }
    @Override
    public void run() {
//        try {
//            Thread.sleep(1* 1000);
//        }
//        catch (Exception ex) {
//            log.warn("sleep exception", ex);
//        }

        String payload = "{\"name\":\"afterSendCompletion\"}";
        simpMessagingTemplate.convertAndSend(deviceId, payload);
        log.info("send complete. deviceId={}", deviceId);

    }
}
