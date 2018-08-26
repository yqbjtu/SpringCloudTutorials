package com.yq;

import com.yq.service.MyZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple to Introduction
 * className: ZKDemoOneApplication
 *
 * @author EricYang
 * @version 2018/8/24 23:43
 */

public class ZKClientApp {
    private static final Logger log = LoggerFactory.getLogger(ZKClientApp.class);
    private static final String ZK_SERVERS = "127.0.0.1:2181";
    private static final String PATH_NAME_TEST = "/test001";

    public static void main(String[] args) {

        MyZooKeeper zk = new MyZooKeeper();
        zk.connect(ZK_SERVERS);
        zk.createPath(PATH_NAME_TEST, "hello world!");
        String value = zk.readData(PATH_NAME_TEST);
        log.info("znode={}, data={}", PATH_NAME_TEST, value);
        zk.detelePath(PATH_NAME_TEST);
        try {
            Thread.sleep(1000 * 120);
        }
        catch (Exception ex){
            log.info("start ex={}.", ex);
        }

        log.info("start done.");
    }
}