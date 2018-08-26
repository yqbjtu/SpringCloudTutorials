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
    private static final String ZNODE_NAME = "/test001";

    public static void main(String[] args) {

        MyZooKeeper zk = new MyZooKeeper();
        zk.connect(ZK_SERVERS);
        zk.createZNode(ZNODE_NAME, "hello world!");
        String value = zk.readData(ZNODE_NAME);
        log.info("znode={}, data={}",ZNODE_NAME, value);
        zk.deteleZNode(ZNODE_NAME);
        try {
            Thread.sleep(1000 * 120);
        }
        catch (Exception ex){
            log.info("start ex={}.", ex);
        }

        log.info("start done.");
    }
}