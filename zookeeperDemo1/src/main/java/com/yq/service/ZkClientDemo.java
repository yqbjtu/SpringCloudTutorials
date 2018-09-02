package com.yq.service;

import com.yq.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private static final String PARENT_NODE_TEMP = "/parentNodeTemp";
    private List<String> myTopicsList = new ArrayList<>();

    public String init() {
        zkClient = new ZkClient(ZKServers,10000,10000,new SerializableSerializer());

        if(!zkClient.exists(PARENT_NODE)){
            log.info("parent node does not exist, create it");
            zkClient.create(PARENT_NODE, "parent", CreateMode.PERSISTENT);
        }

        if(!zkClient.exists(PARENT_NODE_TEMP)){
            log.info("parent node temp does not exist, create it");
            zkClient.create(PARENT_NODE_TEMP, "temp", CreateMode.PERSISTENT);
        }

        zkClient.subscribeChildChanges(PARENT_NODE, new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChildren) throws Exception {
                log.info("ChildNodes under path '" + parentPath +"' has been changed. childNodes={}", currentChildren);
                List<String> newTopicsList = getNewTopics(currentChildren, myTopicsList);
                List<String> delTopicsList = getDeletedTopics(myTopicsList, currentChildren);
                //针对newTopicsList， 我们试图修改数据，并将其添加到自己的myTopicsList中。 修改时，如果别人已经修改，那我们就放弃修改，也不会添加到自己的myTopicsList
                processNewTopics(newTopicsList);
                //针对delTopicsList， 我们发出通知，并将其从自己的myTopicsList中一处
                processDelTopics(delTopicsList);
            }
        });

        //watch其他worker是否订阅或者取消订阅，以及自己消亡了
        zkClient.subscribeChildChanges(PARENT_NODE_TEMP, new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChildrenTemp) throws Exception {
                log.info("ChildNodes under temp path '{}' has been changed. currentChildrenTemp={}", parentPath, currentChildrenTemp );
                List<String> currentChildren = zkClient.getChildren(PARENT_NODE);
                List<String> newTopicsList = getNewTopics(currentChildren, currentChildrenTemp);

                //发生变化后，对比当前PARENT_NODE下面的topics，只要存在于PARENT_NODE，而没有存在于PARENT_NODE_TEMP，
                //就认为要么没有人订阅，要么订阅的人down了
                processNewTopics(newTopicsList);
            }
        });

        zkClient.subscribeStateChanges(
                new  IZkStateListener() {
                    @Override
                    public void handleStateChanged(Watcher.Event.KeeperState state) {
                        log.info("handleStateChanged state={}", state.name());
                    }

                    @Override
                    public void handleNewSession() {
                        log.info("handleNewSession");
                    }
                }
        );

        log.info("connected ok!");
        return zkClient.toString();
    }
    private void processNewTopics(List<String> newTopicsList) {
        log.info("There are newTopics={}", newTopicsList );
        for(String topic : newTopicsList) {
            String path = PARENT_NODE_TEMP + "/" + topic;
            if(zkClient.exists(path)){
                log.info("path={} has already existed. it means that this topic={} is subscribed by other worker.", path, topic);
            }
            else {
                log.info("parent node temp does not exist, try create it={}", path);
                try {
                    path = zkClient.create(path, "temp", CreateMode.EPHEMERAL);

                    myTopicsList.add(topic);
                    log.info("add topic={} to myTopicsList, now myTopicsList={}", topic, myTopicsList);
                    //change record state
                }
                catch (Exception ex) {
                    if (ex instanceof  ZkNodeExistsException ) {
                        log.info("parent node temp is created by other worker when I try to create it={}, ex={}", path, ex);
                    }
                    else {
                        log.info("parent node temp creation ex={}", ex);
                    }
                }
            }
        }
    }

    private void processDelTopics(List<String> delTopicsList) {
         //unsubscribe and change record state
         // remove from myTopicsList
        log.info("Topics={} is deleted from zk, and these topics are on my own, so delete it from my myTopicsList={}",
                delTopicsList, myTopicsList);
        myTopicsList.removeAll(delTopicsList);
    }

    /*
    list1 ,是当前zk上的topic， list2是当前自己的使用的topic, 先省事
     */
    private static List<String> getNewTopics(List<String> list1, List<String> list2) {

        List<String> diff = new ArrayList<String>();
        for(String str:list1)
        {
            if(!list2.contains(str))
            {
                diff.add(str);
            }
        }
        return diff;
    }

    /*
     list1 ,是当前自己的topic， list2是当前zk使用的topic
    */
    private static List<String> getDeletedTopics(List<String> list1, List<String> list2) {
        List<String> diff = new ArrayList<String>();
        for(String str:list1)
        {
            if(!list2.contains(str))
            {
                diff.add(str);
            }
        }
        return diff;
    }

    public String createNode(String nodeName) {

        String path = null;
        if(zkClient.exists(PARENT_NODE)){
            Object obj = zkClient.readData(PARENT_NODE);
            log.info("existingVal={}", obj);

            zkClient.writeData(PARENT_NODE, "parent");
            path = "writeData";
        }
        else {
            log.info("parent node does not exist, create it");
            path = zkClient.create(PARENT_NODE, "data", CreateMode.PERSISTENT);

        }
        //父节点创建完毕，开始创建子节点, 默认自己点数据都是0，只有当其他人订阅了该nodeName，才修改data
        //path = zkClient.create(PARENT_NODE + "/" + nodeName, "0", CreateMode.PERSISTENT);
        zkClient.createPersistent(PARENT_NODE + "/" + nodeName, true);
        log.info("created path:"+path);
        return path;
    }


    public boolean deleteNode(String nodeName) {
        Boolean isDelOK = true;
        String childNode = PARENT_NODE + "/" + nodeName;
        if(zkClient.exists(childNode)){
            //Object obj = zkClient.readData(childNode);
            //log.info("childNode={}, existingVal={}", childNode, obj);
            isDelOK = zkClient.delete(childNode);

            //delete the temp node either
            deleteNodeTemp(nodeName);
        }
        else {
            log.info("child node does not exist, no need to delete it");
        }

        return isDelOK;
    }
    private boolean deleteNodeTemp(String nodeName) {
        Boolean isDelOK = true;
        String childNodeTemp = PARENT_NODE_TEMP + "/" + nodeName;
        if(zkClient.exists(childNodeTemp)){
            isDelOK = zkClient.delete(childNodeTemp);
        }
        else {
            log.info("child node temp does not exist, no need to delete it");
        }

        return isDelOK;
    }

    public String getData(String nodeName) {
        Stat stat = new Stat();
        String str = zkClient.readData(PARENT_NODE + "/" + nodeName, stat);
        return str;
    }

    public List<String> getCurrentList() {
        return Collections.unmodifiableList(myTopicsList);
    }
}

