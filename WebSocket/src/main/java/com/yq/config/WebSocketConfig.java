package com.yq.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yq.WebSocketApplication;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
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
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/gs-guide-websocket").withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        ChannelInterceptor interceptor = new ChannelInterceptorAdapter() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {

                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (accessor!= null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    //Authentication user = "aaa" ; // access authentication header(s)
                   // accessor.setUser(user);
                }
                MessageHeaders header = message.getHeaders();
                Object obj = message.getPayload();
                log.info("Inbound T, class={}", obj.getClass().getCanonicalName());
//                JSONObject jsonObj = JSON.parseObject(obj);
//                jsonObj.put("InboundChannelContent2", "add to");
//                Message<String> msg = new GenericMessage<String>(jsonObj.toJSONString(), header);
//                log.info("Inbound preSend: message={}", message);
//                return msg;
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

                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (accessor!= null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    //Authentication user = "aaa" ; // access authentication header(s)
                    // accessor.setUser(user);
                }
                log.info("Outbound preSend: message={}", message);
                MessageHeaders header = message.getHeaders();
                Object obj = message.getPayload();
                log.info("Outbound T, class={}", obj.getClass().getCanonicalName());
                //一般都是byte[]
                JSONObject jsonObj = null;
                String strUTF8 = null;
                Message<?> msg = null;
                try {
                    strUTF8 = new String((byte[])obj,"UTF-8");
                    jsonObj = JSON.parseObject(strUTF8);
                    jsonObj.put("InboundChannelContent2", "add to");
                    byte[] msgToByte = jsonObj.toJSONString().getBytes("UTF-8");
                    msg = new GenericMessage<>(msgToByte, header);
                }
                catch (Exception ex) {
                    log.info("(byte[] to string exception. ex={}", ex.getLocalizedMessage());
                }

                if (msg != null) {
                    log.info("Inbound preSend Modified: message={}, strUTF8={}", msg, strUTF8);
                    return msg;
                }
                else {
                    log.info("Inbound preSend Original: message={}, strUTF8={}", message, strUTF8);
                    return message;
                }
            }
        };

        registration.interceptors(interceptor);
    }
}