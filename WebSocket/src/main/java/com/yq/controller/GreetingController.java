package com.yq.controller;

import com.yq.WebSocketApplication;
import com.yq.domain.Greeting;
import com.yq.domain.HelloMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class GreetingController {

    private static final Logger log = LoggerFactory.getLogger(GreetingController.class);

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message) throws Exception {
        log.info("send to /topic/greetings with ", message.getName());
        Thread.sleep(1000); // simulated delay
        return new Greeting("Hello1, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }

    @MessageMapping("/hello2")
    @SendTo("/topic/myTopic02")
    public Greeting greeting(String msg) throws Exception {
        Thread.sleep(1000); // simulated delay
        log.info("send to /topic/myTopic02 with ", msg);
        return new Greeting("Hello2, " + HtmlUtils.htmlEscape(msg) + "!");
    }
}
