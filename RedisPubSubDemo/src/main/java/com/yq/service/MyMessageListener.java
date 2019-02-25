package com.yq.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.lang.Nullable;

/**
 * Simple to Introduction
 * className: MyMessageListener
 *
 * @author EricYang
 * @version 2019/2/23 13:16
 */
@Slf4j
public class MyMessageListener implements MessageListener {
    @Override
    public void onMessage(Message message, @Nullable byte[] pattern) {
        byte[] bytesBody = message.getBody();
        byte[] bytesChannel = message.getChannel();
        String patternStr = pattern != null? new String(bytesChannel): null;
        //msgBody=qq, channel=__keyevent@1__:expired
        log.info("msgBody={}, channel={}, pattern={}",  bytesBody!=null? new String(bytesBody): null, bytesChannel!=null? new String(bytesChannel): null, pattern);
    }
}
