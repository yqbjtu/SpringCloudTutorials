package com.yq;


import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Simple to Introduction
 * className: ZKDemoOneApplication
 *
 * @author EricYang
 * @version 2018/8/24 23:43
 */

public class ZKClientSequenceApp {
    private static ZooKeeper zk = null;
    private static final Logger log = LoggerFactory.getLogger(ZKClientSequenceApp.class);
    private static final String ZK_SERVERS = "127.0.0.1:2181";
    private static final int SESSION_TIME = 2000;
    private static final String PARENT_PATH_NAME_TEST = "/yqseq_test001";
    private static final String CHILD_PATH_NAME_TEST = "/yqseq_test001/";
    public static void main(String[] args) throws Exception {
        zk = new ZooKeeper(ZK_SERVERS,SESSION_TIME, null);
        Stat stat = zk.exists(PARENT_PATH_NAME_TEST, null);
        if (stat == null) {
            String zkParentPath = zk.create(PARENT_PATH_NAME_TEST, "helloWorld".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            log.info("zkParentPath={}", zkParentPath);
        }

        String zkPath = zk.create(CHILD_PATH_NAME_TEST, "helloWorld".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
        log.info("zkPath={}", zkPath);

        List<String> children = zk.getChildren(PARENT_PATH_NAME_TEST, null);

        for(String seq : children) {
            log.info("seq={}", seq);
        }

        //生成序列后即可删除，默认刚创建的version为0.，
        //当然如果我我们使用的 CreateMode.EPHEMERAL_SEQUENTIAL， 不用主动清理，在zk断开连接时就自动清理了，但是如果程序一致运行还是会有很多遗留节点的。
        zk.delete(zkPath, 0);
        log.info("start done.");
    }
}