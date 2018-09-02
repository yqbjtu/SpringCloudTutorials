
package com.yq.service;

import com.yq.Constant.PathConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class MyLeaderSelectorListener extends LeaderSelectorListenerAdapter implements Closeable
{
    private final String name;
    private final LeaderSelector leaderSelector;
    private final AtomicInteger leaderCount = new AtomicInteger();


    public MyLeaderSelectorListener(CuratorFramework client, String path, String name) {
        this.name = name;

        // create a leader selector using the given path for management
        // all participants in a given leader selection must use the same path
        // MyLeaderSelectorListener here is also a LeaderSelectorListener but this isn't required
        leaderSelector = new LeaderSelector(client, path, this);

        // for most cases you will want your instance to requeue when it relinquishes leadership
        //leaderSelector.autoRequeue();
    }

    public void start() throws IOException
    {
        // the selection for this instance doesn't start until the leader selector is started
        // leader selection is done in the background so this call to leaderSelector.start() returns immediately
        leaderSelector.start();
    }

    @Override
    public void close() throws IOException
    {
        leaderSelector.close();
    }

    /*
      接管了leader任务，就要给各个worker分配任务(包括自己).  首先是获取myWorkerList这个路径下面的有多个子路径
      子路径代表的是当前有多少个woker， mySubList下面的子路径， 每个子路径下面代表该worker的subList
      myWorkerList下面的都是临时path，一旦有节点离开了，该几点会立刻得到通知有worker离开或者加入了
      mySubList是持久的path，并且/mySubList/workerA/task1, /mySubList/workerA/task2表示workerA有两个任务，当workerA离开的时候，
      leader发现workerA从myWorkerList消失了，就去查看/sublist/workerA的任务，
      然后分配给其他人（分配过程可以是，直接将taskA，taskB添加的/sublist/workerA的下面，workerX发现自己的任务中多了一个就立刻接管该任务，
      同时调用restTempalte，完成异步通知通能）  分配的公平性如何保证？（因为不断有task被取消，导致一些worker上可能就没有多少任务了）

      当有新的worker加入时，leader首先发现了新节点（leader自己要存储一份当前节点list）， 然后当前所有的worker任务进行排序，并计算总数，
      例如共计m个任务，现在加上新节点有n个节点，一个节点的任务是m/n, 那就将多余的任务分配给新worker
      workerA    7
      workerB    9
      workerC    13
       workerNew  0，  m=29， n=4， 每个worker上平均Math.ceil是（29/4=7.25， 去上限）8， 也就是小于8的不需要平衡了， 取Math.ceil的目的是最大限度减少抖动
       那么，workerB移除1个任务，  workerC移除5个任务， 最后的结果是
      workerA    7
      workerB    8
      workerC    8
      workerNew  6

     只在新任务到达，新节点加入，节点消失时进行任务重新平衡，取消任务时不会进行rebalance工作

     如何判断自己的是刚启动的leader，还是别人down机后的leader？
     */
    @Override
    public void takeLeadership(CuratorFramework client) throws Exception
    {
        // we are now the leader. This method should not return until we want to relinquish leadership
        final int waitSeconds = (int)(5 * Math.random()) + 1;

        System.out.println(name + " is now the leader. Waiting " + waitSeconds + " seconds...");
        log.info("{} 现在是leader. " + leaderCount.getAndIncrement() + " time(s) before.", name);
        try
        {
            //观察当前的worker， 有几个实例在工作
            PathChildrenCache watcher = new PathChildrenCache(
                    client,
                    PathConstants.WORKER_PATH,
                    true    // if cache data
            );
            watcher.getListenable().addListener((client1, event) -> {
                ChildData data = event.getData();
                if (data == null) {
                    System.out.println("No data in event[" + event + "]");
                } else {

                if (event.getType() == PathChildrenCacheEvent.Type.CHILD_ADDED) {
                    /*
                        type=[CHILD_ADDED], path=[/myWorkerList/sub-service-8082-2103334695],
                        data=[1535881598520], stat=[1463,1463,1535881598527,1535881598527,0,0,0,100654189908262946,13,0,1463
                    */
                    log.info("workerList 新增child，需要经现有的任务分配给新child");
                }
                else if (event.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED) {
                    /* 哪个worker down了，非常清楚就知道了
                        type=[CHILD_REMOVED], path=[/myWorkerList/sub-service-8082-1774221102],
                        data=[1535881276839], stat=[1449,1449,1535881276849,1535881276849,0,0,0,100654189908262942,13,0,1449
                    */
                    log.info("workerList 有child down了，需要接管该child上的额任务");
                }else if (event.getType() == PathChildrenCacheEvent.Type.CHILD_UPDATED) {
                    log.info("workerList child更新，不用管");
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
            log.info("Register zk watcher successfully!");
            Thread.sleep(TimeUnit.SECONDS.toMillis(waitSeconds));
        }
        catch ( InterruptedException e )
        {
            log.info(name + " was interrupted.");
            Thread.currentThread().interrupt();
        }
        finally
        {
            log.info(name + " relinquishing leadership.");
        }
    }
}
