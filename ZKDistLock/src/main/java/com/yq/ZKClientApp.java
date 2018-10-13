package com.yq;

import com.yq.service.MyZooKeeper;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Simple to Introduction
 * className: ZKDemoOneApplication
 *
 * @author EricYang
 * @version 2018/8/24 23:43
 */

public class ZKClientApp {
    private static final Logger log = LoggerFactory.getLogger(ZKClientApp.class);
    public static final String LOCK_ZNODE = "/deviceId_pointId";
    static final String ZK_SERVERS = "127.0.0.1:2181";

    static final int SESSION_OUTTIME = 5000;

    static int count = 10;
    public static void readAndWrite(String threadNo){
        String fileName = "zkLock.txt";
        try {
            File file = new File(fileName);
            String lastLine = null;
            if (file.isFile() && file.exists()) {
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                if ((lineTxt = bufferedReader.readLine())== null) {
                    FileWriter writer = new FileWriter(fileName, true);
                    String content = "0" + "\r\n";
                    writer.write(content);
                    writer.close();
                }
                else {
                    lastLine = lineTxt;
                    log.info("lastLine={}", lastLine);
                    while((lineTxt = bufferedReader.readLine())!=null && !"".equals(lineTxt)){
                        lastLine = lineTxt;
                    }
                    Integer oldValue = Integer.valueOf(lastLine);
                    Integer newValue = oldValue + 1;
                    FileWriter writer = new FileWriter(fileName, true);
                    String content = newValue.toString() + "\r\n";
                    log.info("oldValue={}, newValue={}, threadNo={}", oldValue, newValue.toString());
                    writer.write(content);
                    writer.close();
                }

                read.close();
            } else {
                file.createNewFile();
                log.error("file Path={}", file.getAbsolutePath());
            }
        } catch (Exception e) {
            log.error("读取文件内容出错", e);
        }
    }

    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);
        CuratorFramework cf = CuratorFrameworkFactory.builder()
                .connectString(ZK_SERVERS)
                .sessionTimeoutMs(SESSION_OUTTIME)
                .retryPolicy(retryPolicy)
                .build();
        cf.start();

        final InterProcessMutex lock = new InterProcessMutex(cf, "/LOCK_ZNODE");
        final CountDownLatch countdown = new CountDownLatch(1);

        for(int i = 0; i < 10; i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        countdown.await();
                        //加锁
                        boolean isAcquired = lock.acquire(10, TimeUnit.SECONDS);
                        log.info("isAcquired={}", isAcquired);
                        readAndWrite("");
                        Thread.sleep(10000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            //释放
                            lock.release();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            },"t" + i).start();
        }
        Thread.sleep(100);
        countdown.countDown();
    }
}