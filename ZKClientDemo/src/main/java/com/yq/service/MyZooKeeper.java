package com.yq.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs;
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
        log.info("event notification={}", event.getState() );
        if(event.getState()==KeeperState.SyncConnected){
            countDownLatch.countDown();

        }
        else if (event.getType() == Event.EventType.NodeCreated) {
            log.info("node create={}", event.getPath() );
        }
        else if (event.getType() == Event.EventType.NodeDeleted) {
            log.info("node delete={}", event.getPath() );
        }

        log.info("type={}", event.getType() );
        String path = event.getPath();
        if (event.getType() == Event.EventType.None) {
            // We are are being told that the state of the
            // connection has changed
            switch (event.getState()) {
                case SyncConnected:
                    // In this particular example we don't need to do anything
                    // here - watches are automatically re-registered with
                    // server and any watches triggered while the client was
                    // disconnected will be delivered (in order of course)
                    log.info("SyncConnected node path={}", path);
                    break;
                case Expired:
                    // It's all over
                    break;
            }
        } else {
            if (path != null ) {
                log.info("node path={}", path );
            }
        }

        zooKeeper.register(this);

    }

    public void connect(String hosts){
        try {
            if(zooKeeper == null){
                zooKeeper = new ZooKeeper(hosts,SESSION_TIME,this);
                countDownLatch.await();
            }
        } catch (IOException ex) {
            log.error("Failed to connect, InterruptedException , ex={} ", ex.getMessage(), ex);
        } catch (InterruptedException ex) {
            log.error( "Failed to connect, OException , ex={}", ex.getMessage(), ex );
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

    public boolean createZNode(String path,String data){
        try {
            String zkPath = MyZooKeeper.zooKeeper.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            log.info("ZooKeeper. create znode path={}", zkPath);
            return true;
        } catch (KeeperException e) {
            log.error("Failed to create znode：" + e.getMessage() + "，path:" + path  ,e);
        } catch (InterruptedException e) {
            log.error("Failed to create znode：" + e.getMessage() + "，path:" + path  ,e);
        }
        return false;
    }
    public boolean deteleZNode(String path){
        try {
            MyZooKeeper.zooKeeper.delete(path, -1);
            log.info("ZooKeeper delete znode，path={}", path);
            return true;
        } catch (InterruptedException e) {
            log.error("Failed to delete znode：" + e.getMessage() + "，path:" + path ,e);
        } catch (KeeperException e) {
            log.error("Failed to delete znode：" + e.getMessage() + "，path:" + path  ,e);
        }
        return false;
    }

    public String readData(String path ){
        String data = null;
        try {
            data = new String( MyZooKeeper.zooKeeper.getData( path, false, null ) );
            log.info( "read data. path={}, content={}", path, data);
        } catch (KeeperException e) {
            log.error("Failed to read data. zKException!.", path, e.getMessage());
        } catch (InterruptedException e) {
            log.error("Failed to read data. 发生InterruptedException!. path={}, exMsg={}", path, e.getMessage());
        }
        return  data;
    }
}