package com.yq.config;

/**
 * Simple to Introduction
 * className: MultipartSupportConfig
 *
 * @author EricYang
 * @version 2019/1/22 19:49
 */


import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MultipartSupportConfig {

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    @Bean
    public Encoder feignFormEncoder(){
        return new SpringFormEncoder(new SpringEncoder(messageConverters));
    }

}
