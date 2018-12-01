package com.yq;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * Simple to Introduction
 * className: ZKDistributedAtomicLongSequenceApp
 *
 * @author EricYang
 * @version 2018/12/01 23:43
 */

@Slf4j
public class ZKDistributedAtomicLongSequenceApp {
    private static final String COUNTER_ZNODE = "/yqlock_pathDistAtomicLong";
    //like this "127.0.0.1:2181,"192.168.1.132:2181";
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
        final CyclicBarrier cyclicBarrier = new CyclicBarrier(11);
        DistributedAtomicLong distAtomicLong = new DistributedAtomicLong(cf, COUNTER_ZNODE, retryPolicy);
        for(int i = 0; i < 10; i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //这里使用CountDownLatch，是为了保证10个线程同时启动，每个县被创建的线程都在await，等10个创建完成后，在主线程调用了countDown
                        countdown.await();
                        AtomicValue<Long> sequence = distAtomicLong.increment();
                        if (sequence.succeeded()) {
                            Long seq = sequence.postValue();
                            log.info("threadId={}, sequence={}", Thread.currentThread().getId(), seq);
                        } else {
                            log.warn("threadId={}, no sequence", Thread.currentThread().getId());
                        }
                        cyclicBarrier.await();
                    } catch (Exception e) {
                        log.error("acquire section exception.", e);
                    }
                }

            },"t" + i).start();
        }
        Thread.sleep(300);
        //10个线程开始执行
        countdown.countDown();
        log.info("countDown");
        cyclicBarrier.await();
        log.info("End");
    }
}