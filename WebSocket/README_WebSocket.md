# websocket
https://spring.io/guides/gs/messaging-stomp-websocket/

https://www.jeejava.com/spring-boot-websocket-angularjs-gradle-example/

https://docs.spring.io/spring-framework/docs/5.0.1.RELEASE/spring-framework-reference/web.html#websocket

https://docs.spring.io/spring-framework/docs/5.0.1.RELEASE/spring-framework-reference/web.html#websocket-stomp-handle-send

http://127.0.0.1:8080/
通过下面的swagger可以在controller中发送消息给socket
http://127.0.0.1:8080/swagger-ui.html


AbstractWebSocketMessageBrokerConfigurer已经过期了。

controller中发送过来的消息页面也可以收到
[INFO ] 22:17:02.811 [http-nio-8080-exec-7] com.yq.config.WebSocketConfig - Inbound preSend: sessionId=whhidk5t, jwtToken=yqbjtu