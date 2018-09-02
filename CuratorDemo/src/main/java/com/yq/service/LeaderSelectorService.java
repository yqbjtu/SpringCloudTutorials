package com.yq.service;

import com.yq.Constant.PathConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.consul.serviceregistry.ConsulRegistration;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Simple to Introduction
 * className: LeaderSelectorService
 *
 * @author EricYang
 * @version 2018/9/2 12:01
 */
@Service
@Slf4j
public class LeaderSelectorService {
    @Autowired
    private ConsulRegistration registration;

    private static final int CLIENT_QTY = 10;
    CuratorFramework client = null;

    //存放自己的任务列表，Map<String, String>  <uuid, content>
    private List<Map<String, String>> mySubList = new ArrayList<>();

    public void init() throws Exception
    {
        String currentId = registration.getInstanceId();

        MyLeaderSelectorListener selector = null;

        try {
            client =
                    CuratorFrameworkFactory.newClient("127.0.0.1:2181", new ExponentialBackoffRetry(1000, 3));
            client.start();
            //先注册自己 人，然后才watch其他人
            registerMySelf();
            watchMySubList();

            selector = new MyLeaderSelectorListener(client, PathConstants.LEADER_PATH, currentId);
            selector.start();

            //leader选举完毕再观察总的任务列表
            watchAllSubList();


            System.out.println("Press enter/return to quit\n");
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        } finally {
            System.out.println("Shutting down...");
            CloseableUtils.closeQuietly(selector);
            CloseableUtils.closeQuietly(client);
        }
    }

    public void registerMySelf() throws Exception
    {
        String currentId = registration.getInstanceId();
        String workerPath = PathConstants.WORKER_PATH + "/" + currentId;
        try {
            Stat stat = client.checkExists().forPath(workerPath);
            if(stat == null) {
                long currentTime = System.currentTimeMillis();
                client.create().withMode(CreateMode.EPHEMERAL).forPath(workerPath,String.valueOf(currentTime).getBytes("UTF-8"));
            }
            else {
                log.warn("{} has already been registered.", workerPath);
            }
        }
        catch (Exception ex) {
            log.error("{} registered. exception", workerPath, ex);
        }
    }

    public void watchAllSubList() {
        try
        {
            PathChildrenCache watcher = new PathChildrenCache(
                    client,
                    PathConstants.ALL_SUB_PATH,
                    true    // if cache data
            );
            watcher.getListenable().addListener((client1, event) -> {
                ChildData data = event.getData();
                if (data == null) {
                    System.out.println("No data in event[" + event + "]");
                } else {
                    if (event.getType() == PathChildrenCacheEvent.Type.CHILD_ADDED) {
                        /* 新增加了哪个任务，很多就知道了
                        type=[CHILD_ADDED], path=[/myWorkerList/sub-service-8082-2103334695],
                        data=[1535881598520], stat=[1463,1463,1535881598527,1535881598527,0,0,0,100654189908262946,13,0,1463
                        */
                        log.info("ALL_SUB_PATH 新增task，需要当前leader分配task给worker");
                    }
                    else if (event.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED) {
                        /* 哪个task 取消了，非常清楚就知道了
                        type=[CHILD_REMOVED], path=[/myWorkerList/sub-service-8082-1774221102],
                        data=[1535881276839], stat=[1449,1449,1535881276849,1535881276849,0,0,0,100654189908262942,13,0,1449
                        */
                        log.info("ALL_SUB_PATH 有task取消了，需要执行该task的worker进行更新");
                    }else if (event.getType() == PathChildrenCacheEvent.Type.CHILD_UPDATED) {
                        log.info("ALL_SUB_PATH task更新，不用管");
                    } else {
                        log.info("Receive event: "
                                + "type=[" + event.getType() + "]"
                                + ", path=[" + data.getPath() + "]"
                                + ", data=[" + new String(data.getData()) + "]"
                                + ", stat=[" + data.getStat() + "]");
                    }
                }
            });
            watcher.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
            log.info("Register zk watcher 观察总的任务列表successfully!");
        }
        catch (Exception ex)
        {
            log.info("观察总的任务列表 was interrupted.", ex);
        }
    }

    /*
     自己的任务列表，一致需要关注，然后去执行，并更新对应的状态
     */
    public void watchMySubList() {
        String instanceId = registration.getInstanceId();
        String myPath = PathConstants.MY_SUB_Path + "/" + instanceId;
        try
        {
            PathChildrenCache watcher = new PathChildrenCache(
                    client,
                    myPath,
                    true
            );
            watcher.getListenable().addListener((client1, event) -> {
                ChildData data = event.getData();
                if (data == null) {
                    System.out.println("No data in event[" + event + "]");
                } else {
                    if (event.getType() == PathChildrenCacheEvent.Type.CHILD_ADDED) {
                        /* 新增加了哪个任务，很多就知道了
                        type=[CHILD_ADDED], path=[/myWorkerList/sub-service-8082-2103334695],
                        data=[1535881598520], stat=[1463,1463,1535881598527,1535881598527,0,0,0,100654189908262946,13,0,1463
                        */
                        log.info("我的任务新增一个task，需要执行该task 执行更新");
                    }
                    else if (event.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED) {
                        /* 哪个task 取消了，非常清楚就知道了
                        type=[CHILD_REMOVED], path=[/myWorkerList/sub-service-8082-1774221102],
                        data=[1535881276839], stat=[1449,1449,1535881276849,1535881276849,0,0,0,100654189908262942,13,0,1449
                        */
                        log.info("我的任务取消一个task，需要执行该task 取消更新");
                    }else if (event.getType() == PathChildrenCacheEvent.Type.CHILD_UPDATED) {
                        log.info("ALL_SUB_PATH task更新，不用管");
                    } else {
                        log.info("Receive event: "
                                + "type=[" + event.getType() + "]"
                                + ", path=[" + data.getPath() + "]"
                                + ", data=[" + new String(data.getData()) + "]"
                                + ", stat=[" + data.getStat() + "]");
                    }
                }
            });
            watcher.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
            log.info("Register zk watcher 观察自己的任务列表successfully!");
        }
        catch (Exception ex)
        {
            log.info("观察自己的任务列表 was interrupted.", ex);
        }
    }
}
