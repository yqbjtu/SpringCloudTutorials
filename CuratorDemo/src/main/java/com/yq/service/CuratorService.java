package com.yq.service;

import com.yq.Constant.PathConstants;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
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
public class CuratorService {
    private static final Logger log = LoggerFactory.getLogger(CuratorService.class);

    private String ZKServers = "127.0.0.1:2181";
    private CuratorFramework client  = null;
    private static final String PARENT_NODE = "/parentNode";
    private static final String PARENT_NODE_TEMP = "/parentNodeTemp";
    private List<String> myTopicsList = new ArrayList<>();

    public CuratorService() throws Exception {
        client = CuratorFrameworkFactory.newClient(
                ZKServers,
                new RetryNTimes(10, 5000)
        );
        client.start();
        log.info("zk client start successfully!");

        Stat stat = client.checkExists().forPath(PathConstants.ALL_SUB_PATH);
        if(stat == null){
            log.info("parent node does not exist, create it");
            client.create().forPath(PathConstants.ALL_SUB_PATH);
        }

        log.info("connected ok!");
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

    public String createNode(String uuid, String content)   {
        String topicPath = PathConstants.ALL_SUB_PATH + "/" + uuid;
        String result = null;
        try {
            Stat stat = client.checkExists().forPath(PathConstants.ALL_SUB_PATH);
            if(stat != null){

                stat = client.checkExists().forPath(topicPath);

                if(stat != null){
                    byte[] existingValue = client.getData().forPath(topicPath);
                    log.info("topicPath={}, existingValue is '{}'.", topicPath,  new String(existingValue,"UTF-8"));
                }
                else {
                    result = client.create().forPath(topicPath, content.getBytes("utf-8"));
                }
            }
            else {
                log.info("parent node does not exist, create it");
                client.create().forPath(PathConstants.ALL_SUB_PATH);

            }
        }
        catch (Exception ex ) {
            log.info("create node exception", ex);
        }

        log.info("created topicPath {}", topicPath);
        return result;
    }


    public boolean deleteNode(String uuid){
        Boolean isDelOK = true;
        String topicPath = PathConstants.ALL_SUB_PATH + "/" + uuid;
        try {
            Stat stat = client.checkExists().forPath(topicPath);
            if(stat != null){
                client.delete().forPath(topicPath);
            }
        }
        catch (Exception ex ) {
            log.info("create node exception", ex);
        }

        return isDelOK;
    }

    /*
     应该先检查是否存在
      */
    public String getData(String path){
        byte[] existingValue = null;
        String result = null;
        try {
            existingValue = client.getData().forPath(path);
            result = new String(existingValue,"UTF-8");
        }
        catch (Exception ex ) {
            log.info("create node exception", ex);
        }

        return  result;
    }

    public List<String> getCurrentList() {
        return Collections.unmodifiableList(myTopicsList);
    }
}

