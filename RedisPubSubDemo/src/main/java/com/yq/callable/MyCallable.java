package com.yq.callable;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

/**
 * Simple to Introduction
 * className: MyCallable
 *  if we use callable, program will report "java.lang.IllegalArgumentException: Task can't be created using anonymous class"
 *
 *  Caused by: java.lang.IllegalArgumentException: com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Cannot construct instance of `com.yq.callable.MyCallable` (no Creators, like default construct, exist): cannot deserialize from Object value (no delegate- or property-based Creator)
 * @author EricYang
 * @version 2019/5/16 14:25
 */
@Slf4j
public class MyCallable implements Callable<String> {
    private int num = 30;

    public MyCallable(int num) {
        this.num = num;
    }

    @Override
    public String call() throws Exception {
        long threadId = Thread.currentThread().getId();
        String jobId = "jobId_" + threadId;
        log.info("call jobId={}. threadId={}", jobId, threadId);
        try {
            Thread.sleep(num * 1000);
        } catch (Exception ex) {
            log.info("{} sleep exception", threadId, ex);
        }
        return jobId;
    }
}
