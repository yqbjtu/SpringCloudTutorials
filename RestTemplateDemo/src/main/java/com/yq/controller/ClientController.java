package com.yq.controller;

import com.yq.domain.User;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.ssl.SslContextBuilder;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
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

    //private final String BASE_URL = "http://localhost:9901";
    private final String BASE_URL = "https://localhost:9901";

    public ClientController() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setDefaultMaxPerRoute(1000);
        connectionManager.setMaxTotal(1000);
        this.restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory( HttpClientBuilder.create().setConnectionManager(connectionManager).build() ));

        CloseableHttpClient httpClient = null;
        HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = null;
        try {
            HttpClientBuilder builder = HttpClientBuilder.create();
            httpClient = acceptsUntrustedCertsHttpClient(builder);
        } catch (Exception e) {
            log.error("exception",e);
        }
        if(httpClient != null){
            httpComponentsClientHttpRequestFactory =new HttpComponentsClientHttpRequestFactory(httpClient);
        }
        if(httpComponentsClientHttpRequestFactory != null){
            this.restTemplate = new RestTemplate(httpComponentsClientHttpRequestFactory);
        }

        fixedPool = Schedulers.newParallel("poolWithMaxSize", 400);
    }

    private CloseableHttpClient acceptsUntrustedCertsHttpClient(HttpClientBuilder builder)
            throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                return true;
            }
        }).build();
        builder.setSSLContext(sslContext);

        HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;

        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslSocketFactory)
                .build();

        PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager( socketFactoryRegistry);
        connMgr.setMaxTotal(200);
        connMgr.setDefaultMaxPerRoute(100);
        builder.setConnectionManager( connMgr);

        CloseableHttpClient client = builder.build();

        return client;
    }
    @ApiOperation(value = "通过AsyncRestTemplate访问user api", notes="这是演示代码，实际中不建议在controller中写这么多业务代码或者工具代码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "userID", defaultValue = "1",required = true, dataType = "string", paramType = "path"),
    })
    @GetMapping(value = "/requestByAsyncRestTemplate/{userId}", produces = "application/json;charset=UTF-8")
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
    @GetMapping(value = "/requestByRestTemplate/{userId}", produces = "application/json;charset=UTF-8")
    public String getUserByRestTemplate(@PathVariable String userId) {
        String endpointUrl = BASE_URL + "/user/users/" + userId;

        String resBody = restTemplate.getForObject(endpointUrl, String.class);
        return  resBody;
    }

    @ApiOperation(value = "通过restTemplate访问user api, 修改用户", notes="这是演示代码，实际中不建议在controller中写这么多业务代码或者工具代码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "userID", defaultValue = "2",required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "username", value = "username", defaultValue = "u2-0", required = true, dataType = "string", paramType = "body")
    })
    @PostMapping(value = "/requestByRestTemplate/{userId}", produces = "application/json;charset=UTF-8")
    public String updateUserByRestTemplate(@PathVariable String userId, @RequestBody String username) {
        String endpointUrl = BASE_URL + "/user/users/" + userId;

        HttpHeaders headers = new HttpHeaders();
        headers.add("k1", "v1");

        HttpEntity<String> entity = new HttpEntity<>(username, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(endpointUrl, HttpMethod.POST, entity, String.class);
        String resBody = responseEntity.getBody();
        return  resBody;
    }

    @ApiOperation(value = "通过Mono restTemplate访问user api", notes="这是演示代码，实际中不建议在controller中写这么多业务代码或者工具代码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "userID", defaultValue = "3",required = true, dataType = "string", paramType = "path"),
    })
    @GetMapping(value = "/requestByRestTemplateWithMono/{userId}", produces = "application/json;charset=UTF-8")
    public Mono<User> getUserByWebClientWithMono(@PathVariable String userId) {
        String endpointUrl = BASE_URL + "/user/users/" + userId;
        return Mono.fromCallable(() -> restTemplate.getForObject(endpointUrl, User.class))
                .subscribeOn(fixedPool);
    }

}
