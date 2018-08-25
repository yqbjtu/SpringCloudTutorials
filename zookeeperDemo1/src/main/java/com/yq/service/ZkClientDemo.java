package com.yq.service;

import com.yq.ZKDemoOneApplication;
import com.yq.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Simple to Introduction
 * className: ZkClientDemo
 *
 * @author EricYang
 * @version 2018/8/24 23:51
 */
@Service
public class ZkClientDemo {
    private static final Logger log = LoggerFactory.getLogger(ZkClientDemo.class);

    private String ZKServers = "127.0.0.1:2181";
    private ZkClient zkClient = null;
    private static final String PARENT_NODE = "/parentNode";

    public String init() {
        zkClient = new ZkClient(ZKServers,10000,10000,new SerializableSerializer());

        log.info("connected ok!");
        return zkClient.toString();
    }

    public String createNode(String nodeName) {
        User user = new User();
        user.setId(2);
        user.setName("testUser2");

        String path = null;
        if(zkClient.exists(PARENT_NODE)){
            Object obj = zkClient.readData(PARENT_NODE);
            log.info("existingVal={}", obj);

            zkClient.writeData(PARENT_NODE, "parent");
            path = "writeData";
        }
        else {
            log.info("parent node does not exist, create it");
            path = zkClient.create(PARENT_NODE, user, CreateMode.PERSISTENT);
        }
        //父节点创建完毕，开始创建子节点, 默认自己点数据都是0，只有当其他人订阅了该nodeName，才修改data
        path = zkClient.create(PARENT_NODE + "/" + nodeName, "0", CreateMode.PERSISTENT);
        log.info("created path:"+path);
        return path;
    }

    public boolean deleteNode(String nodeName) {
        Boolean isDelOK = true;
        String childNode = PARENT_NODE + "/" + nodeName;
        if(zkClient.exists(childNode)){
            Object obj = zkClient.readData(childNode);
            log.info("childNode={}, existingVal={}", childNode, obj);
            isDelOK = zkClient.delete(childNode);
        }
        else {
            log.info("child node does not exist, no need to delete it");
        }

        return isDelOK;
    }

    public String getData(String nodeName) {
        Stat stat = new Stat();
        String str = zkClient.readData(PARENT_NODE + "/" + nodeName, stat);
        return str;
    }
}

