package com.yq;

import com.yq.service.DistSequenceGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.shared.SharedCount;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.io.*;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

/**
 * Simple to Introduction
 * className: ZKDemoOneApplication
 *
 * @author EricYang
 * @version 2018/8/24 23:43
 */

@Slf4j
public class ZKCounterApp {
    private static final String COUNTER_ZNODE = "/yqcounter_path1";
    private static final String ZK_SERVERS = "127.0.0.1:2181";
    static final int SESSION_OUTTIME = 15000;

    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);
        CuratorFramework cf = CuratorFrameworkFactory.builder()
                .connectString(ZK_SERVERS)
                .sessionTimeoutMs(SESSION_OUTTIME)
                .retryPolicy(retryPolicy)
                .build();
        cf.start();

        final CountDownLatch countdown = new CountDownLatch(1);
        final CyclicBarrier cyclicBarrier = new CyclicBarrier(10);
        SharedCount counter = new SharedCount(cf, COUNTER_ZNODE, 0);
        counter.start();

        counter.setCount(counter.getCount() + 1);
        for(int i = 0; i < 10; i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean isAcquired = false;
                    try {
                        countdown.await();
                        int sequence = counter.getCount();
                         //xuyao jixu
                        counter.setCount(counter.getCount() + 1);
                        log.info("threadId={}, counter={}", Thread.currentThread().getId(), sequence);
                    } catch (Exception e) {
                        log.error("acquire section exception.", e);
                    }
                }
            },"t" + i).start();
        }
        Thread.sleep(100);
        //10个线程开始执行
        countdown.countDown();
        log.info("countDown");

        log.info("End");
    }
}