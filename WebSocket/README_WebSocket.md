# websocket
https://spring.io/guides/gs/messaging-stomp-websocket/

https://www.jeejava.com/spring-boot-websocket-angularjs-gradle-example/

https://docs.spring.io/spring-framework/docs/5.0.1.RELEASE/spring-framework-reference/web.html#websocket

https://docs.spring.io/spring-framework/docs/5.0.1.RELEASE/spring-framework-reference/web.html#websocket-stomp-handle-send

http://127.0.0.1:8086/---会有跨域问题‘

http://localhost:8086/---可以正常访问
http://localhost:8086/swagger-ui.html
通过下面的swagger可以在controller中发送消息给socket
http://127.0.0.1:6666/swagger-ui.html


AbstractWebSocketMessageBrokerConfigurer已经过期了。

controller中发送过来的消息页面也可以收到
[INFO ] 22:17:02.811 [http-nio-8080-exec-7] com.yq.config.WebSocketConfig - Inbound preSend: sessionId=whhidk5t, jwtToken=yqbjtu

js代码先connect，然后subscribe
也就是所有的Connect操作都是
Inbound preSend
Inbound postSend
Inbound afterSendCompletion
Outbound preSend
Outbound postSend
Outbound afterSendCompletion

对于subscribe这样的操作不需要outbound的就是只有三个inbound
Inbound preSend
Inbound postSend
Inbound afterSendCompletion

[INFO ] 19:12:32.629 [http-nio-8086-exec-4] com.yq.config.WebSocketConfig - Inbound preSend. message=GenericMessage [payload=byte[0], headers={simpMessageType=CONNECT, stompCommand=CONNECT, nativeHeaders={AuthToken=[yqbjtu], accept-version=[1.1,1.0], heart-beat=[10000,10000]}, simpSessionAttributes={}, simpHeartbeat=[J@71bc82d4, simpSessionId=twhhlp14}]
[INFO ] 19:12:32.631 [http-nio-8086-exec-4] com.yq.config.WebSocketConfig - Inbound postSend. message=GenericMessage [payload=byte[0], headers={simpMessageType=CONNECT, stompCommand=CONNECT, nativeHeaders={AuthToken=[yqbjtu], accept-version=[1.1,1.0], heart-beat=[10000,10000]}, simpSessionAttributes={}, simpHeartbeat=[J@71bc82d4, simpSessionId=twhhlp14}]
[INFO ] 19:12:32.631 [http-nio-8086-exec-4] com.yq.config.WebSocketConfig - Inbound afterSendCompletion. message=GenericMessage [payload=byte[0], headers={simpMessageType=CONNECT, stompCommand=CONNECT, nativeHeaders={AuthToken=[yqbjtu], accept-version=[1.1,1.0], heart-beat=[10000,10000]}, simpSessionAttributes={}, simpHeartbeat=[J@71bc82d4, simpSessionId=twhhlp14}]
[INFO ] 19:12:32.631 [clientInboundChannel-2] com.yq.config.WebSocketConfig - Outbound preSend: message=GenericMessage [payload=byte[0], headers={simpMessageType=CONNECT_ACK, simpConnectMessage=GenericMessage [payload=byte[0], headers={simpMessageType=CONNECT, stompCommand=CONNECT, nativeHeaders={AuthToken=[yqbjtu], accept-version=[1.1,1.0], heart-beat=[10000,10000]}, simpSessionAttributes={}, simpHeartbeat=[J@71bc82d4, simpSessionId=twhhlp14}], simpSessionId=twhhlp14}]
[INFO ] 19:12:32.632 [clientInboundChannel-2] com.yq.config.WebSocketConfig - Outbound postSend. message=GenericMessage [payload=byte[0], headers={simpMessageType=CONNECT_ACK, simpConnectMessage=GenericMessage [payload=byte[0], headers={simpMessageType=CONNECT, stompCommand=CONNECT, nativeHeaders={AuthToken=[yqbjtu], accept-version=[1.1,1.0], heart-beat=[10000,10000]}, simpSessionAttributes={}, simpHeartbeat=[J@71bc82d4, simpSessionId=twhhlp14}], simpSessionId=twhhlp14}]
[INFO ] 19:12:32.633 [clientInboundChannel-2] com.yq.config.WebSocketConfig - Outbound afterSendCompletion. message=GenericMessage [payload=byte[0], headers={simpMessageType=CONNECT_ACK, simpConnectMessage=GenericMessage [payload=byte[0], headers={simpMessageType=CONNECT, stompCommand=CONNECT, nativeHeaders={AuthToken=[yqbjtu], accept-version=[1.1,1.0], heart-beat=[10000,10000]}, simpSessionAttributes={}, simpHeartbeat=[J@71bc82d4, simpSessionId=twhhlp14}], simpSessionId=twhhlp14}]
[INFO ] 19:12:32.644 [http-nio-8086-exec-5] com.yq.config.WebSocketConfig - Inbound preSend. message=GenericMessage [payload=byte[0], headers={simpMessageType=SUBSCRIBE, stompCommand=SUBSCRIBE, nativeHeaders={id=[sub-0], destination=[/topic/app01]}, simpSessionAttributes={}, simpHeartbeat=[J@41c83f57, simpSubscriptionId=sub-0, simpSessionId=twhhlp14, simpDestination=/topic/app01}]
[INFO ] 19:12:32.645 [http-nio-8086-exec-5] com.yq.config.WebSocketConfig - Inbound postSend. message=GenericMessage [payload=byte[0], headers={simpMessageType=SUBSCRIBE, stompCommand=SUBSCRIBE, nativeHeaders={id=[sub-0], destination=[/topic/app01]}, simpSessionAttributes={}, simpHeartbeat=[J@41c83f57, simpSubscriptionId=sub-0, simpSessionId=twhhlp14, simpDestination=/topic/app01}]
[INFO ] 19:12:32.646 [http-nio-8086-exec-5] com.yq.config.WebSocketConfig - Inbound afterSendCompletion. message=GenericMessage [payload=byte[0], headers={simpMessageType=SUBSCRIBE, stompCommand=SUBSCRIBE, nativeHeaders={id=[sub-0], destination=[/topic/app01]}, simpSessionAttributes={}, simpHeartbeat=[J@41c83f57, simpSubscriptionId=sub-0, simpSessionId=twhhlp14, simpDestination=/topic/app01}]
[INFO ] 19:12:54.684 [MessageBroker-4] o.s.w.s.c.WebSocketMessageBrokerStats - WebSocketSession[1 current WS(1)-HttpStream(0)-HttpPoll(0), 1 total, 0 closed abnormally (0 connect failure, 0 send limit, 0 transport error)], stompSubProtocol[processed CONNECT(1)-CONNECTED(1)-DISCONNECT(0)], stompBrokerRelay[null], inboundChannel[pool size = 6, active threads = 0, queued tasks = 0, completed tasks = 6], outboundChannelpool size = 1, active threads = 0, queued tasks = 0, completed tasks = 1], sockJsScheduler[pool size = 8, active threads = 1, queued tasks = 2, completed tasks = 5]  

  对于调用        messagingTemplate.convertAndSend(topic, message);
              log.info("sendMessage to {} with {}", topic, message);
              
可以看到顺序是
   Outbound preSend
   Outbound postSend
   afterSendCompletion
            
  [INFO ] 19:15:25.755 [http-nio-8086-exec-8] com.yq.config.WebSocketConfig - Outbound preSend: message=GenericMessage [payload=byte[12], headers={simpMessageType=MESSAGE, simpSubscriptionId=sub-0, contentType=text/plain;charset=UTF-8, simpSessionId=twhhlp14, simpDestination=/topic/app01}]
  [INFO ] 19:15:25.756 [http-nio-8086-exec-8] com.yq.config.WebSocketConfig - Outbound postSend. message=GenericMessage [payload=byte[12], headers={simpMessageType=MESSAGE, simpSubscriptionId=sub-0, contentType=text/plain;charset=UTF-8, simpSessionId=twhhlp14, simpDestination=/topic/app01}]
  [INFO ] 19:15:25.757 [http-nio-8086-exec-8] com.yq.config.WebSocketConfig - Outbound afterSendCompletion. message=GenericMessage [payload=byte[12], headers={simpMessageType=MESSAGE, simpSubscriptionId=sub-0, contentType=text/plain;charset=UTF-8, simpSessionId=twhhlp14, simpDestination=/topic/app01}]
  [INFO ] 19:15:25.757 [http-nio-8086-exec-8] com.yq.controller.GreetingController - sendMessage to /topic/app01 with {"name":610}  
  
  
  https://docs.spring.io/spring-framework/docs/5.0.0.BUILD-SNAPSHOT/spring-framework-reference/html/websocket.html#websocket-server-allowed-origins  
    
    
    https://www.nexmo.com/blog/2018/10/08/create-websocket-server-spring-boot-dr/
  
  