package com.yq;

import com.yq.service.MyZooKeeper;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
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
import java.io.RandomAccessFile;
import java.io.Writer;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
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
    private static final String LOCK_ZNODE = "/MsgId_OtherId";
    private static final String ZK_SERVERS = "127.0.0.1:2181";


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
                String lineTxt = bufferedReader.readLine();
                //如果文件的第一行是空，就清空整改文件
                if ( StringUtils.isBlank(lineTxt)) {
                    //FileWriter writer = new FileWriter(fileName, true);
                    RandomAccessFile raf = new RandomAccessFile(fileName,"rw");
                    String initContent = "0" + "\r\n";
                    raf.seek(0);
                    raf.write(initContent.getBytes("UTF-8"));
                    log.info("Write zero and read it again. lineTxt={}", lineTxt);
                }
                else {
                    lastLine = lineTxt;
                    while((lineTxt = bufferedReader.readLine())!=null && !"".equals(lineTxt)){
                        lastLine = lineTxt;
                    }
                    Integer oldValue = Integer.valueOf(lastLine);
                    Integer newValue = oldValue + 1;
                    FileWriter writer = new FileWriter(fileName, true);
                    String content = newValue.toString() + "\r\n";
                    log.info("oldValue={}, newValue={}", oldValue, newValue);
                    writer.write(content);
                    writer.flush();
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
        //final InterProcessReadWriteLock rwlock = new InterProcessReadWriteLock(cf, "/LOCK_ZNODE");
        final CountDownLatch countdown = new CountDownLatch(1);
        final CyclicBarrier cyclicBarrier = new CyclicBarrier(10);

        for(int i = 0; i < 10; i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Random random = new Random();
                    boolean isAcquired = false;
                    try {
                        countdown.await();
                        isAcquired = lock.acquire(60, TimeUnit.SECONDS);
                        log.info("isAcquired={}", isAcquired);
                        if (isAcquired) {
                            readAndWrite("");
                            int sleepRandom = random.nextInt(1000);
                            Thread.sleep(sleepRandom);
                        }
                        else {
                            log.info("isAcquired={}, do nothing", isAcquired);
                        }
                    } catch (Exception e) {
                        log.error("acquire section exception.", e);
                    } finally {
                        try {
                            if (isAcquired) {
                                lock.release();
                                log.info("release");
                            }
                        } catch (Exception ex) {
                            log.error("release exception.", ex);
                        }
                    }
                }
            },"t" + i).start();
        }
        Thread.sleep(100);
        //10个线程开始执行
        countdown.countDown();
        log.info("countDown");

        //继续运行1.5分钟让其他正在运行的示例获取lock
        Thread.sleep(90*1000);
        log.info("End");
    }
}