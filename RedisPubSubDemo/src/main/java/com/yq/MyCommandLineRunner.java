package com.yq;


import com.yq.dist.DistLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
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
@Order(value = 4)
public class MyCommandLineRunner implements CommandLineRunner {

    @Autowired
    private DistLock distLock;


    @Override
    public void run(String... strings) throws Exception {
        log.info("MyCommandLineRunner初始化 distLock={}", distLock);

        distLock.init();
    }
}
