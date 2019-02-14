package com.yq.controller;

import com.yq.domain.User;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.ssl.SslContextBuilder;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import javax.net.ssl.SSLException;
import java.util.Map;

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

    private EventLoopGroup eventLoopGroup;
    private AsyncRestTemplate httpClient;

    private RestTemplate restTemplate;
    private Scheduler fixedPool;

    private final String BASE_URL = "http://localhost:9901";

    public ClientController() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setDefaultMaxPerRoute(1000);
        connectionManager.setMaxTotal(1000);
        this.restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory( HttpClientBuilder.create().setConnectionManager(connectionManager).build() ));

        fixedPool = Schedulers.newParallel("poolWithMaxSize", 400);
    }

    @ApiOperation(value = "通过AsyncRestTemplate访问user api", notes="这是演示代码，实际中不建议在controller中写这么多业务代码或者工具代码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "userID", defaultValue = "1",required = true, dataType = "string", paramType = "path"),
    })
    @PostMapping(value = "/request/{userId}", produces = "application/json;charset=UTF-8")
    public String getUser(@PathVariable String userId) {
        try {
            this.eventLoopGroup = new NioEventLoopGroup();
            Netty4ClientHttpRequestFactory nettyFactory = new Netty4ClientHttpRequestFactory(this.eventLoopGroup);
            nettyFactory.setSslContext(SslContextBuilder.forClient().build());
            httpClient = new AsyncRestTemplate(nettyFactory);
        } catch (SSLException e) {
            log.error("sslException", e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("k1", "v1");
        HttpMethod method = HttpMethod.valueOf("GET");
        //get方法的body默认回被ignore的，这里这是演示而已
        String reqBody = "testBodyData";
        HttpEntity<String> entity = new HttpEntity<>(reqBody, headers);
        String endpointUrl = BASE_URL + "/user/users/" + userId;

        ListenableFuture<ResponseEntity<String>> future = httpClient.exchange(
                endpointUrl, method, entity, String.class);

        String resBody = "";
        future.addCallback(new ListenableFutureCallback<ResponseEntity<String>>() {
            @Override
            public void onFailure(Throwable throwable) {
                log.error("http exception.", throwable);
            }

            @Override
            public void onSuccess(ResponseEntity<String> responseEntity) {
                String status =  responseEntity.getStatusCode().name();
                String statusCode = responseEntity.getStatusCode().value()+"";
                String statusReason = responseEntity.getStatusCode().getReasonPhrase();
                Map<String, String> map = responseEntity.getHeaders().toSingleValueMap();
                String resBody = responseEntity.getBody();

                if (responseEntity.getStatusCode().is2xxSuccessful()) {
                    log.info("status={}, statusCode={}, statusReason={}, map={}, body={}",
                            status, statusCode, statusReason, map, resBody);
                } else {
                    log.warn("status={}, statusCode={}, statusReason={}, map={}, body={}",
                            status, statusCode, statusReason, map, resBody);
                }
            }
        });

        return  resBody;
    }

    @ApiOperation(value = "通过restTemplate访问user api", notes="这是演示代码，实际中不建议在controller中写这么多业务代码或者工具代码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "userID", defaultValue = "2",required = true, dataType = "string", paramType = "path"),
    })
    @PostMapping(value = "/requestByRestTemplate/{userId}", produces = "application/json;charset=UTF-8")
    public String getUserByWebClient(@PathVariable String userId) {
        String endpointUrl = BASE_URL + "/user/users/" + userId;

        String resBody = restTemplate.getForObject(endpointUrl, String.class);
        return  resBody;
    }

    @ApiOperation(value = "通过Mono restTemplate访问user api", notes="这是演示代码，实际中不建议在controller中写这么多业务代码或者工具代码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "userID", defaultValue = "3",required = true, dataType = "string", paramType = "path"),
    })
    @PostMapping(value = "/requestByRestTemplateWithMono/{userId}", produces = "application/json;charset=UTF-8")
    public Mono<User> getUserByWebClientWithMono(@PathVariable String userId) {
        String endpointUrl = BASE_URL + "/user/users/" + userId;
        return Mono.fromCallable(() -> restTemplate.getForObject(endpointUrl, User.class))
                .subscribeOn(fixedPool);
    }

}
