package com.yq.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yq.service.MyRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.SimpleMessageConverter;
import org.springframework.messaging.converter.SmartMessageConverter;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableWebSocketMessageBroker
@Service
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private static final Logger log = LoggerFactory.getLogger(WebSocketConfig.class);

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            4,             /* minimum (core) thread count */
            6,        /* maximum thread count */
            Long.MAX_VALUE, /* timeout */
            TimeUnit.NANOSECONDS,
            new LinkedBlockingQueue<Runnable>(),
            new ThreadPoolExecutor.DiscardOldestPolicy());

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        //也就是所有的topic必须以/topic开始，否则无法正常工作
        config.enableSimpleBroker("/topic");
        //config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket")
                //如果没有设置AllowedOrigins，默认只有本机的本服务端口才可以连接，其他都报CORS跨域问题
                 .setAllowedOrigins("*")
                 .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        ChannelInterceptor interceptor = new ChannelInterceptorAdapter() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                log.info("Inbound preSend. message={}", message);
//                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//                MessageHeaders header = message.getHeaders();
//                String sessionId = (String)header.get("simpSessionId");
//                if (accessor != null && accessor.getCommand() !=null && accessor.getCommand().getMessageType() != null) {
//                    SimpMessageType type = accessor.getCommand().getMessageType();
//                    if (accessor!= null && SimpMessageType.CONNECT.equals(type)) {
//                        //Authentication user = "aaa" ; // access authentication header(s)
//                        // accessor.setUser(user);
//                        String jwtToken = accessor.getFirstNativeHeader("AuthToken");
//                        log.info("Inbound preSend: sessionId={}, jwtToken={}", sessionId, jwtToken);
//                    }else if (type == SimpMessageType.DISCONNECT) {
//                        log.info("Inbound sessionId={} is disconnected", sessionId);
//                    }else if (type == SimpMessageType.SUBSCRIBE) {
//                        String topicDest = (String)header.get("simpDestination");
//                        String payload = "{\"code\":60}";
//                        //messagingTemplate.setUserDestinationPrefix("/");
//                        //messagingTemplate.convertAndSend("/app01", payload);
//                        //message = sendInitMsg(message, topicDest, null, payload);
//                        MyRunnable myRunnable = new MyRunnable(messagingTemplate, topicDest);
//                        executor.submit(myRunnable);
//                        log.info("subscribe topicDest={}, message={} SUBSCRIBE", topicDest, message);
//                    } else if (type == SimpMessageType.MESSAGE) {
//                        String topicDest = (String)header.get("simpDestination");
//                        message = UpdateMessage(message, "Inbound");
//                    }
//                }

                return message;
            }
            @Override
            public boolean preReceive(MessageChannel channel) {
                log.info("Inbound preReceive. channel={}", channel);
                return true;
            }

            @Override
            public Message<?> postReceive(Message<?> message, MessageChannel channel) {
                log.info("Inbound postReceive. message={}", message);
                return message;
            }

            @Override
            public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
                log.info("Inbound postSend. message={}", message);
            }

            @Override
            public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, @Nullable Exception ex) {
                log.info("Inbound afterSendCompletion. message={}", message);
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                MessageHeaders header = message.getHeaders();
                if (accessor != null && accessor.getCommand() !=null && accessor.getCommand().getMessageType() != null) {
                    SimpMessageType type = accessor.getCommand().getMessageType();
                    if (type == SimpMessageType.SUBSCRIBE) {
                        String topicDest = (String)header.get("simpDestination");
                        String payload = "{\"name\":\"afterSendCompletion\"}";
                        MyRunnable myRunnable = new MyRunnable(messagingTemplate, topicDest);
                        executor.submit(myRunnable);
                        log.info("subscribe topicDest={}, message={} SUBSCRIBE", topicDest, message);
                    }
                }
            }

            @Override
            public void afterReceiveCompletion(@Nullable Message<?> message, MessageChannel channel, @Nullable Exception ex) {

                log.info("Inbound afterReceiveCompletion. message={}", message);
            }
        };

        registration.interceptors(interceptor);
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        ChannelInterceptor interceptor = new ChannelInterceptorAdapter() {
            @Override
            public boolean preReceive(MessageChannel channel) {
                log.info("Outbound preReceive: channel={}", channel);
                return true;
            }

            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                log.info("Outbound preSend: message={}", message);
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

//                if (accessor != null && accessor.getCommand() !=null && accessor.getCommand().getMessageType() != null) {
//                    SimpMessageType type = accessor.getCommand().getMessageType();
//                    if (accessor!= null && SimpMessageType.CONNECT.equals(type)) {
//                        //Authentication user = "aaa" ; // access authentication header(s)
//                        // accessor.setUser(user);
//                    } else if (type == SimpMessageType.MESSAGE) {
//                        message = UpdateMessage(message, "Outbound");
//                    }
//                }

                return message;
            }

            @Override
            public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
                log.info("Outbound postSend. message={}", message);
            }

            @Override
            public Message<?> postReceive(Message<?> message, MessageChannel channel) {
                log.info("Outbound postReceive. message={}", message);
                return message;
            }

            @Override
            public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, @Nullable Exception ex) {
                log.info("Outbound afterSendCompletion. message={}", message);
            }

            @Override
            public void afterReceiveCompletion(@Nullable Message<?> message, MessageChannel channel, @Nullable Exception ex) {
                log.info("Outbound afterReceiveCompletion. message={}", message);
            }
        };

        registration.interceptors(interceptor);
    }

    private Message<?> UpdateMessage(Message<?> message, String logFlag) {
        log.info(logFlag + " preSend: message={}", message);
        MessageHeaders header = message.getHeaders();
        Object obj = message.getPayload();
        //一般都是byte[]
        JSONObject jsonObj = null;
        String strUTF8 = null;
        String strJsonUTF8 = null;
        Message<?> msg = null;
        try {
            strUTF8 = new String((byte[])obj,"UTF-8");
            jsonObj = JSON.parseObject(strUTF8);
            jsonObj.put(logFlag + "ChannelContent2", "add to");
            String value = jsonObj.getString("name");
            jsonObj.put("name", logFlag + "add to " + value);
            strJsonUTF8 = jsonObj.toJSONString();
            byte[] msgToByte = strJsonUTF8.getBytes("UTF-8");
            msg = new GenericMessage<>(msgToByte, header);
        }
        catch (Exception ex) {
            log.info("(byte[] to string exception. ex={}", ex.getLocalizedMessage());
        }

        if (msg != null) {
            log.info(logFlag + " preSend Modified: message={}, strUTF8={}, strJsonUTF8={}", msg, strUTF8, strJsonUTF8);
            return msg;
        }
        else {
            log.info(logFlag + " preSend Original: message={}, strUTF8={}", message, strUTF8);
            return message;
        }
    }
//"simpMessageType" -> "MESSAGE"
//    "contentType" -> "text/plain;charset=UTF-8"
    //GenericMessage [payload={"code":60}, headers={destination=/topic/app01, simpMessageType=MESSAGE, id=2006ec01-4144-2705-b5a2-d70bfea47ec1, contentType=text/plain;charset=UTF-8, timestamp=1540457614968}]
   //GenericMessage [payload=byte[65], headers={simpMessageType=MESSAGE, stompCommand=SEND, nativeHeaders={destination=[/topic/app01], content-length=[17]}, simpSessionAttributes={}, simpHeartbeat=[J@d3f55f1, simpSessionId=mmn2kar4, simpDestination=/topic/app01}], strUTF8={"name":"ceshi4"}, strJsonUTF8={"InboundChannelContent2":"add to","name":"Inboundadd to ceshi4"}
    Message sendInitMsg(Message<?> oldMessage, String dest, Map<String, Object> headers, String payload) {
        MessageHeaders messageHeaders = null;
        Object conversionHint = headers != null?headers.get("conversionHint"):null;
        Map<String, Object> headersToUse = new HashMap<>();
        headersToUse.put("simpMessageType", SimpMessageType.MESSAGE);
        headersToUse.put("destination", dest);
        headersToUse.put("contentType", "text/plain;charset=UTF-8");
        headersToUse.put("stompCommand", "SEND");

        Map<String, Object> nativeHeaders = new LinkedHashMap<>();
        nativeHeaders.put("id", "sub-0");
        nativeHeaders.put("destination", dest);
        headersToUse.put("nativeHeaders", nativeHeaders);
        messageHeaders = new MessageHeaders(headersToUse);
        MessageHeaders oldHeaders = oldMessage.getHeaders()

        ;
        MessageConverter converter = new SimpleMessageConverter();
        Message<?> message = converter instanceof SmartMessageConverter ?((SmartMessageConverter)converter).toMessage(payload, messageHeaders, conversionHint):converter.toMessage(payload, messageHeaders);
        return message;

    }
}