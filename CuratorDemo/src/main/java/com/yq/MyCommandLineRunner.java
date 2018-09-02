package com.yq;

import com.yq.Constant.PathConstants;
import com.yq.service.CuratorService;
import com.yq.service.LeaderSelectorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.consul.serviceregistry.ConsulRegistration;
import org.springframework.stereotype.Component;

/**
 * Simple to Introduction
 * className: MyCommandLineRunner
 *
 * @author EricYang
 * @version 2018/9/2 14:03
 */
@Component
@Slf4j
public class MyCommandLineRunner implements CommandLineRunner {
    @Autowired
    private CuratorService zkClient;


    @Autowired
    private ConsulRegistration registration;


    @Autowired
    LeaderSelectorService  svc;

    @Override
    public void run(String... strings) throws Exception {
        log.info("MyCommandLineRunner初始化 zkClient={}, registration={}, svc={}", zkClient, registration, svc);
        zkClient.getData(PathConstants.ALL_SUB_PATH);
        svc.init();
    }
}
