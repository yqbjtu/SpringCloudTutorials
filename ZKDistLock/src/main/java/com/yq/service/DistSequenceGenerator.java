package com.yq.service;


import java.nio.charset.Charset;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * Created by yangqian on 2018/11/21.
 */

@Slf4j
public class DistSequenceGenerator {
    private CuratorFramework curator;
    private InterProcessMutex mutex ;
    private String lockPath;

    public DistSequenceGenerator(String zkServers, String lockPath) {
        curator = CuratorFrameworkFactory.builder()
                .connectString(zkServers)
                .retryPolicy(new ExponentialBackoffRetry(2000,3))
                .build();

        curator.start();
        this.lockPath = lockPath;
        mutex = new InterProcessMutex(curator, lockPath);
    }
    /**
     * get next sequential number
     * @return
     * @throws Exception
     */
    public int next() throws Exception{
        int seq =0;
        try {
            mutex.acquire();
            byte[] seqInByte = curator.getData().forPath(this.lockPath);
            //第一次获取的是空
            if (seqInByte.length != 0) {
                seq= Integer.parseInt(new String(seqInByte));
                seq++;
            }
            else {
                log.warn( "seq=0 for seqInByte={}", seqInByte);
            }

            curator.setData().forPath(lockPath,new Integer(seq).toString().getBytes());
        }catch(Exception ex) {
            log.error( "Exception", ex );
        }finally {
            mutex.release();
        }

        return seq;
    }
}