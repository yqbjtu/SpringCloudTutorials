package com.yq;


import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
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
    private static final int SESSION_OUTTIME = 5000;

    static int count = 10;
    public static void readAndWrite(String threadNo){
        String fileName = "zkLock.txt";
        InputStreamReader read = null;
        BufferedReader bufferedReader = null;
        RandomAccessFile raf = null;
        FileWriter writer = null;
        try {
            File file = new File(fileName);
            String lastLine = null;

            if (file.isFile() && file.exists()) {
                read = new InputStreamReader(new FileInputStream(file), "UTF-8");
                bufferedReader = new BufferedReader(read);
                String lineTxt = bufferedReader.readLine();
                //如果文件的第一行是空，就清空整改文件
                if ( StringUtils.isBlank(lineTxt)) {
                    raf = new RandomAccessFile(fileName,"rw");
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
                    writer = new FileWriter(fileName, true);
                    String content = newValue.toString() + "\r\n";
                    log.info("oldValue={}, newValue={}", oldValue, newValue);
                    writer.write(content);
                    writer.flush();
                }

            } else {
                file.createNewFile();
                log.error("file Path={}", file.getAbsolutePath());
            }
        } catch (Exception e) {
            log.error("读取文件内容出错", e);
        }
        finally {
            try {
                if (read != null) {
                    read.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (raf != null) {
                    raf.close();
                }
                if (writer != null) {
                    writer.close();
                }
            }
            catch (Exception ex) {
                log.error("关闭文件读写时出错", ex);
            }
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

        //final InterProcessMutex lock = new InterProcessMutex(cf, LOCK_ZNODE);
        //final InterProcessReadWriteLock rwlock = new InterProcessReadWriteLock(cf, LOCK_ZNODE);
        final CountDownLatch countdown = new CountDownLatch(10);
        final CyclicBarrier cyclicBarrier = new CyclicBarrier(10);

        for(int i = 0; i < 10; i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    InterProcessSemaphoreMutex lock = new InterProcessSemaphoreMutex(cf, LOCK_ZNODE);
                    Random random = new Random();
                    boolean isAcquired = false;
                    try {

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
                        countdown.countDown();
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
        log.info("countDown await");
        countdown.await();
        log.info("done");

        //log.info("isAcquiredInThisProcess​={}", lock.isAcquiredInThisProcess());
        //继续运行1分钟以便观察其他正在在运行的示例获取lock的时间
        //Thread.sleep(20*1000);
        //log.info("done. isAcquiredInThisProcess​={}", lock.isAcquiredInThisProcess());
    }
}