package com.yq.controller;

import com.yq.domain.User;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Simple to Introduction
 * className: ClientController
 *
 * @author EricYang
 * @version 2019/2/14 9:30
 */


@RestController
@RequestMapping("/client")
@Slf4j
public class ClientController {
    private WebClient webClient;

    private final String BASE_URL = "http://localhost:9902";

    public ClientController() {
        this.webClient = WebClient.builder().baseUrl(BASE_URL).build();
    }

    @ApiOperation(value = "通过WebClient访问user api", notes="这是演示代码，实际中不建议在controller中写这么多业务代码或者工具代码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "userID", defaultValue = "2",required = true, dataType = "string", paramType = "path"),
    })
    @PostMapping(value = "/requestByWebClient/{userId}", produces = "application/json;charset=UTF-8")
    public Mono<User> getUserByWebClient(@PathVariable String userId) {

        Mono<User> mono = webClient
                .get().uri("/user/users/" + userId)
                .headers(httpHeaders -> {
                    httpHeaders.add("h1", "v1");
                    httpHeaders.add("h2","v2");
                })
                .exchange()
                .flatMap(clientResponse -> clientResponse.bodyToMono(User.class));
        return mono;
    }

}
