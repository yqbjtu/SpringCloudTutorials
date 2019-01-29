package com.yq;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;

@SpringBootApplication
@EnableDiscoveryClient
@Slf4j
public class ConsulUserApplication {

//    @Value("${https.port}")
//    private Integer port;
//
//    @Value("${https.ssl.key-store-password}")
//    private String key_store_password;
//
//    @Value("${https.ssl.key-password}")
//    private String key_password;
    
    public static void main(String[] args) {
        SpringApplication.run(ConsulUserApplication.class, args);
        log.info("ConsulUserApplication Start done.");
    }

    // 这是spring boot 1.5.X以下版本的 添加了这个，下一个就不用添加了
//    @Bean
//    public EmbeddedServletContainerFactory servletContainer() {
//        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
//        tomcat.addAdditionalTomcatConnectors(createSslConnector()); // 添加http
//        return tomcat;
//    }

    // 这是spring boot 2.0.X版本的 添加这个，上一个就不用添加了
//    @Bean
//    public ServletWebServerFactory servletContainer() {
//        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
//        tomcat.addAdditionalTomcatConnectors(createSslConnector()); // 添加http
//        return tomcat;
//    }
//
//    // 配置https
//    private Connector createSslConnector() {
//        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
//        Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
//        try {
//            File keystore = new ClassPathResource("sample.jks").getFile();
//            /*File truststore = new ClassPathResource("sample.jks").getFile();*/
//            connector.setScheme("https");
//            connector.setSecure(true);
//            connector.setPort(port);
//            protocol.setSSLEnabled(true);
//            protocol.setKeystoreFile(keystore.getAbsolutePath());
//            protocol.setKeystorePass(key_store_password);
//            protocol.setKeyPass(key_password);
//            return connector;
//        }
//        catch (IOException ex) {
//            throw new IllegalStateException("can't access keystore: [" + "keystore"
//                    + "] or truststore: [" + "keystore" + "]", ex);
//        }
//    }

}
