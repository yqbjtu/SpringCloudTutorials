package com.yq.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;


@Slf4j
public class MyZooKeeper implements Watcher {


    protected CountDownLatch countDownLatch = new CountDownLatch(1);

    private static final int SESSION_TIME = 2000;
    public static ZooKeeper zooKeeper = null;


    @Override
    public void process(WatchedEvent event) {
        log.info("event notification={}" + event.getState() );
        if(event.getState()==KeeperState.SyncConnected){
            countDownLatch.countDown();
        }
        else if (event.getType() == Event.EventType.NodeCreated) {
            log.info("node create={}" + event.getPath() );
        }
        else if (event.getType() == Event.EventType.NodeDeleted) {
            log.info("node delete={}" + event.getPath() );
        }
    }

    public void connect(String hosts){
        try {
            if(zooKeeper == null){
                // ZK客户端允许我们将ZK服务器的所有地址都配置在这里
                zooKeeper = new ZooKeeper(hosts,SESSION_TIME,this);
                // 使用CountDownLatch.await()的线程（当前线程）阻塞直到所有其它拥有
                //CountDownLatch的线程执行完毕（countDown()结果为0）
                countDownLatch.await();
            }
        } catch (IOException ex) {
            log.error("连接创建失败，发生 InterruptedException , ex={} ", ex.getMessage(), ex);
        } catch (InterruptedException ex) {
            log.error( "连接创建失败，发生 IOException , ex={}", ex.getMessage(), ex );
        }
    }


    public void close(){
        try {
            if(zooKeeper != null){
                zooKeeper.close();
            }
        } catch (InterruptedException ex) {
            log.error("release connection error={}", ex.getMessage() ,ex);
        }
    }
}