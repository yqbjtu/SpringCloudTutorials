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
    private static ZooKeeper zooKeeper = null;

    private static final String PATH_NAME = "/yqPath";

    @Override
    public void process(WatchedEvent event) {
        log.info("event notification={}", event.getState() );
        String path = event.getPath();
        log.info("type={}. path={}", event.getType(), path);
        if(event.getState()==KeeperState.SyncConnected){
            countDownLatch.countDown();

        }
        else if (event.getType() == Event.EventType.NodeCreated) {
            log.info("node create={}", event.getPath() );
        }
        else if (event.getType() == Event.EventType.NodeDeleted) {
            log.info("node delete={}", event.getPath() );
        }if (event.getType() == Event.EventType.NodeDataChanged) {
            String data = readData(event.getPath());
            log.info("data change. path={}, data={}", event.getPath(), data);
        }

        //必须重新注册，要不然就无法获得变化通知
        try {
            zooKeeper.exists(PATH_NAME, this);
        }catch (Exception ex) {
            log.error( "Failed to connect, Exception , ex={}", ex.getMessage(), ex );
        }

        //zooKeeper.register(this);
    }

    public void connect(String hosts){
        try {
            if(zooKeeper == null){
                zooKeeper = new ZooKeeper(hosts,SESSION_TIME,this);
                zooKeeper.exists(PATH_NAME, this);
                countDownLatch.await();
            }
        } catch (IOException ex) {
            log.error("Failed to connect, IOException , ex={} ", ex.getMessage(), ex);
        } catch (InterruptedException ex) {
            log.error( "Failed to connect, InterruptedException , ex={}", ex.getMessage(), ex );
        } catch (Exception ex) {
            log.error( "Failed to connect, Exception , ex={}", ex.getMessage(), ex );
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

    public boolean createPath(String path, String data){
        try {
            String zkPath = zooKeeper.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            log.info("ZooKeeper. create path={}", zkPath);
            return true;
        } catch (KeeperException e) {
            log.error("Failed to create path. errMsg：" + e.getMessage() + "，path:" + path  ,e);
        } catch (InterruptedException e) {
            log.error("Failed to create path. errMsg:" + e.getMessage() + "，path:" + path  ,e);
        }
        return false;
    }

    public boolean detelePath(String path){
        try {
            MyZooKeeper.zooKeeper.delete(path, -1);
            log.info("ZooKeeper delete path，path={}", path);
            return true;
        } catch (InterruptedException e) {
            log.error("Failed to delete path, errMsg：" + e.getMessage() + "，path:" + path ,e);
        } catch (KeeperException e) {
            log.error("Failed to delete path, errMsg：" + e.getMessage() + "，path:" + path  ,e);
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