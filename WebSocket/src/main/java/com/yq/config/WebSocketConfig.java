package com.yq.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private static final Logger log = LoggerFactory.getLogger(WebSocketConfig.class);

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        //也就是所有的topic必须以/topic开始，否则无法正常工作
        config.enableSimpleBroker("/topic");
        //config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket").withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        ChannelInterceptor interceptor = new ChannelInterceptorAdapter() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                MessageHeaders header = message.getHeaders();
                String sessionId = (String)header.get("simpSessionId");
                if (accessor != null && accessor.getCommand() !=null && accessor.getCommand().getMessageType() != null) {
                    SimpMessageType type = accessor.getCommand().getMessageType();
                    if (accessor!= null && SimpMessageType.CONNECT.equals(type)) {
                        //Authentication user = "aaa" ; // access authentication header(s)
                        // accessor.setUser(user);
                        String jwtToken = accessor.getFirstNativeHeader("AuthToken");
                        log.info("Inbound preSend: sessionId={}, jwtToken={}", sessionId, jwtToken);
                    }else if (type == SimpMessageType.DISCONNECT) {
                        log.info("Inbound sessionId={} is disconnected", sessionId);
                    }else if (type == SimpMessageType.SUBSCRIBE) {
                        log.info("Inbound sessionId={} SUBSCRIBE", sessionId);
                    } else if (type == SimpMessageType.MESSAGE) {
                        message = UpdateMessage(message, "Inbound");
                    }
                }

                return message;
            }

            @Override
            public Message<?> postReceive(Message<?> message, MessageChannel channel) {
                MessageHeaders header = message.getHeaders();
                Object obj = message.getPayload();
                log.info("Inbound postReceive: message={}", message);
                log.info("Inbound postReceive, class={}", obj.getClass().getCanonicalName());
                return message;
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
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                if (accessor != null && accessor.getCommand() !=null && accessor.getCommand().getMessageType() != null) {
                    SimpMessageType type = accessor.getCommand().getMessageType();
                    if (accessor!= null && SimpMessageType.CONNECT.equals(type)) {
                        //Authentication user = "aaa" ; // access authentication header(s)
                        // accessor.setUser(user);
                    } else if (type == SimpMessageType.MESSAGE) {
                        message = UpdateMessage(message, "Outbound");
                    }
                }

                return message;
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

}