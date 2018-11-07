
package com.yq.service;

import com.yq.Constant.PathConstants;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.zookeeper.data.Stat;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Data
public class MyLeaderSelectorListener extends LeaderSelectorListenerAdapter implements Closeable
{
    private final String instanceId;
    private final LeaderSelector leaderSelector;
    private final AtomicInteger leaderCount = new AtomicInteger();
    private CuratorFramework client = null;
    private volatile boolean isLeader = false;


    public MyLeaderSelectorListener(CuratorFramework client, String path, String instanceId) {
        this.instanceId = instanceId;

        this.client = client;

        // create a leader selector using the given path for management
        // all participants in a given leader selection must use the same path
        // MyLeaderSelectorListener here is also a LeaderSelectorListener but this isn't required
        leaderSelector = new LeaderSelector(client, path, this);

        // for most cases you will want your instance to requeue when it relinquishes leadership
        leaderSelector.autoRequeue();

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
      workerC    21
      workerNew  0，  m=37， n=4， 每个worker上平均Math.ceil是（37/4=9.25， 取上限）10， 也就是小于9的不需要平衡了， 取Math.ceil的目的是最大限度减少抖动
      那么，workerB移除（9小于10）0个任务，  workerC移除（21-10） 11个任务， 最后的结果是， 基本平衡（如果某个节点上特别多，就严重影响平衡了）
      workerA    7
      workerB    9
      workerC    10
      workerNew  11

      workerA Down 了，  m=37， n=2， 每个worker上平均Math.ceil是（37/2=18.5， 取上限）19， 也就是大于17需要移除一部分任务给其他人，
       那么，workerB 当前任务只有8 ，不到17， 因此先从workerA上接管7个任务，共计16个，  workerC移除（21-19） 3个任务， 再讲这三个分配给workB
       最后的结果是

      workerB    8 + 7 + 3=18
      workerC    19



     只在新任务到达，新节点加入，节点消失时进行任务重新平衡，取消任务时不会进行rebalance工作


     当所有worker被强制关闭了，新启动的第一个worker发现自己是leader（所有人发现自己是leader都应该做一次这样的工作），
     后对比当前的/mySubList下面的自己节点是否和/myWorkerList中自己点匹配
     如果存在于/mySubList， 但是没有存在于/myWorkerList， 就表明他是被强制关闭的worker，并且当时其他worker都down了。
     如果/mySubList中不存在， 但是存在于/myWorkerList， 就表明新加入worker，不用处理。

     如何判断自己的是刚启动的leader，还是别人down机后的leader？
     */
    @Override
    public void takeLeadership(CuratorFramework client) throws Exception {
        // we are now the leader. This method should not return until we want to relinquish leadership
        final int waitSeconds = (int)(1000 * 30 * Math.random()) + 1;

        Long threadId = Thread.currentThread().getId();
        log.info("{} is now the leader. Waiting {} seconds..., threadId={}", instanceId, waitSeconds, threadId);
        log.info("当前leader是{}. {} time(s) before. threadId={}", instanceId, leaderCount.getAndIncrement(), threadId);
        isLeader = true;

        /*
        刚接管了leader后需要，检查一下当前/allSubList和所有有效live worker下面的subList之和是否相等，这是所有leader需要完成的任务
        如果不相等，就需要将差异找出来，凡是存在于/allSubList，但是不存在于mySubList中的，需要分配到worker上
        凡是存在于/mySubList，但是不存在于allSubList中的，需要取消订阅，因为该任务要么是down机期间，别人取消的，要么是不应该有的任务
         */

        leaderCheckIntegrity();

        try
        {
//            //观察当前的worker， 有几个实例在工作
//            PathChildrenCache watcher = new PathChildrenCache(
//                    client,
//                    PathConstants.WORKER_PATH,
//                    true    // if cache data
//            );
//            watcher.getListenable().addListener((client1, event) -> {
//
//                ChildData data = event.getData();
//                if (data == null) {
//                    System.out.println("No data in event[" + event + "]");
//                } else {
//                if (event.getType() == PathChildrenCacheEvent.Type.CHILD_ADDED) {
//                    /*
//                        type=[CHILD_ADDED], path=[/myWorkerList/sub-service-8082-2103334695],
//                        data=[1535881598520], stat=[1463,1463,1535881598527,1535881598527,0,0,0,100654189908262946,13,0,1463
//                    */
//                    log.info("workerList 新增child，需要把现有的任务分配给新child. threadId={}", Thread.currentThread().getId());
//                    processNewWorker(data.getPath());
//
//                }
//                else if (event.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED) {
//                    /* 哪个worker down了，非常清楚就知道了
//                        type=[CHILD_REMOVED], path=[/myWorkerList/sub-service-8082-1774221102],
//                        data=[1535881276839], stat=[1449,1449,1535881276849,1535881276849,0,0,0,100654189908262942,13,0,1449
//                    */
//                    log.info("workerList 有child down了，需要接管该child上的任务. threadId={}", Thread.currentThread().getId());
//                    processDownWorker(data.getPath());
//
//                }else if (event.getType() == PathChildrenCacheEvent.Type.CHILD_UPDATED) {
//                    log.info("workerList child更新，不用管. threadId={}", threadId);
//                } else {
//                    log.info("Receive event: "
//                            + "type=[" + event.getType() + "]"
//                            + ", path=[" + data.getPath() + "]"
//                            + ", data=[" + new String(data.getData()) + "]"
//                            + ", stat=[" + data.getStat() + "]");
//                }
//                }
//            });
//            watcher.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
//            log.info("Register zk watcher 工作实例 successfully!");
            Thread.sleep(TimeUnit.SECONDS.toMillis(waitSeconds));
        }
        catch ( InterruptedException e )
        {
            log.info(instanceId + " was interrupted.");
            Thread.currentThread().interrupt();
        }
        finally
        {
            log.info(instanceId + " relinquishing leadership.");
            isLeader = false;
        }
    }
    /*
        刚接管了leader后需要，检查一下当前/allSubList和所有有效live worker下面的subList之和是否相等，这是所有leader需要完成的任务
        如果不相等，就需要将差异找出来，凡是存在于/allSubList，但是不存在于mySubList中的，需要分配到worker上
        凡是存在于/mySubList，但是不存在于allSubList中的，需要取消订阅，因为该任务要么是down机期间，别人取消的，要么是不应该有的任务
   */

    private void leaderCheckIntegrity() {
        //获取所有或者的worker，String格式为sub-service-8082-1135504170
        Long threadId = Thread.currentThread().getId();
        List<String> liveWorkerList = getAllLiveWorkerList();
        List<String> allSubList = getAllSubList();
        if (allSubList == null) {
            allSubList = new ArrayList<>();
        }
        //liveWorkerList与allWorkerListFromMySubList肯定不会为null， 因为新worker需要立刻注册自己并watch自己的任务列表
        List<String> allWorkerListFromMySubList = getAllWorkerListFromMySubList();

        log.info("检查完整性 liveWorkerList={}, allSubList={}, allWorkerListFromMySubList={}. threadId={}",
                liveWorkerList, allSubList, allWorkerListFromMySubList, threadId);

        //活着的worker们拥有的所有任务
        List<String> allLiveWorkerHoldSubList = new ArrayList<>();
        //down的worker们用户的所有任务
        List<String> allDownWorkerHoldSubList = new ArrayList<>();
        //从/mySubList下面删除所有已经down的worker
        for(String workerId : allWorkerListFromMySubList) {
            if (liveWorkerList.contains(workerId)) {
                List<String> subList = getMySubListByWorkerId(workerId);
                allLiveWorkerHoldSubList.addAll(subList);
            }
            else {
                log.info("workerId={} is not live now, delete it. threadId={}", workerId,threadId);
                List<String> subList = getMySubListByWorkerId(workerId);
                allDownWorkerHoldSubList.addAll(subList);
                deleteWorkerFromMySubList(workerId);
            }
        }
        log.info("检查完整性 allLiveWorkerHoldSubList={}, allDownWorkerHoldSubList={}, threadId={}",
                allLiveWorkerHoldSubList, allDownWorkerHoldSubList, threadId);

        //遍历/mySubList，如果该worker下面的任务在/allSubList中，从/allSubList中移除该任务，
        //                如果该worker下面的任务不在allSubList中，取消该task
        // 应该是因为所有worker down了之后系统取消该taskA，当时因为所有worker都down了导致对应的worker没有取消任务taskA，而/allSubList中移除了该taskA任务
        // 这样taskA存在于/mySubList/instanceX/taskA, 但是不存在与/allSubList中

        //存在于allSubList，但是不能存在于allLiveWorkerHoldSubList中的额，也表示任务应该是被执行的
        Iterator<String> itr = allSubList.iterator();
        List<String> allNewWorkerHoldSubList = new ArrayList<>();
        while(itr.hasNext()){
            //使用index效率更高，因为后面使用remove
            String uuid = itr.next();
            int index = allLiveWorkerHoldSubList.indexOf(uuid);
            if (index == -1) {
                //取消该任务，根据uuid即可, 但是标记为一斤更新了对应的记录
                allNewWorkerHoldSubList.add(uuid);
                log.info("切换leader期间增加的新任务uuid={} will be executed. 正被分配（留在allOldWorkerHoldSubList变量即可）. threadId={}", uuid, threadId);
            }
            else {
                itr.remove();
                log.info("uuid={} has already been executed. 从allSubList变量移除.  threadId={}", uuid, threadId);
            }
        }

        log.info("新任务列表allNewWorkerHoldSubList={}。 threadId={}", allNewWorkerHoldSubList, threadId);
        //存在于allDownWorkerHoldSubList， 但是不能存在于allSubList中的额，就是被取消的
        List<String> allOldSubList = new ArrayList<>();
        List<String> allCancelledSubList = new ArrayList<>();
        for(String uuid : allDownWorkerHoldSubList) {
            //使用index效率更高，因为后面使用remove
            int index = allSubList.indexOf(uuid);
            if (index == -1) {
                //取消该任务，根据uuid即可
                allCancelledSubList.add(uuid);
                log.info("切换leader期间取消的任务uuid={} will be cancelled. 正被取消. threadId={}", uuid, threadId);
            }
            else {
                log.info("uuid={} 留在allSubList变量， 将被分配执行. threadId={}", uuid, threadId);
                allOldSubList.add(uuid);
                allSubList.remove(index);
            }
        }
        log.info("老任务列表allOldSubList={}（已经去除被取消的）. 被取消的任务列表allCancelledSubList={}, threadId={}",
                allOldSubList, allCancelledSubList, threadId);

        //处理还剩余在allSubList的任务，这些任务就是所有的worker都down机期间，其他系统全新添加任务。
        if(allSubList.size() != 0) {
            for (String uuid : allSubList) {
                String content = getContent(uuid);
                if (StringUtils.isNotBlank(content)) {
                    log.info("切换leader期间分配的新任务uuid={} has content={}. 分配给活着的worker并更新记录. threadId={}",
                            uuid, content, threadId);
                    distributeNewTask2Worker(uuid, content);
                }
                else {
                    log.error("uuid={} has no valid content. content={}. threadId={}", uuid, content,threadId);
                }
            }
        }

        if(allOldSubList.size() != 0) {
            for (String uuid : allOldSubList) {
                String content = getContent(uuid);
                if (StringUtils.isNotBlank(content)) {
                    log.info("切换leader期间原来的老任务uuid={} has content={}. 分配给活着的worker. threadId={}", uuid, content, threadId);
                    distributeNewTask2Worker(uuid, content);
                }
                else {
                    log.error("uuid={} has no valid content. content={}. threadId={}", uuid, content,threadId);
                }
            }
        }
     }

    private String getContent(String uuid) {
        String content = null;
        String taskFullPath = PathConstants.ALL_SUB_PATH + "/" + uuid;

        try {
            Stat stat = client.checkExists().forPath(taskFullPath);
            if (stat != null) {
                byte[] existingValue = client.getData().forPath(taskFullPath);
                content = new String(existingValue, "UTF-8");
                log.info("taskFullPath={}, uuid={}, content={}", taskFullPath, uuid, content);
            } else {
                log.error("taskFullPath={} does not exist. but it should exist.", taskFullPath);
            }
        } catch (Exception ex) {
            log.info("get taskFullPath={} children exception", taskFullPath, ex);
        }

        return content;
    }

    public List<String> getMySubListByWorkerId(String workerId){
        List<String> subList = null;
        String workerSubListFullPath = PathConstants.MY_SUB_PATH + "/" + workerId;
        try {
            Stat stat = client.checkExists().forPath(workerSubListFullPath);
            if(stat != null){
                subList = client.getChildren().forPath(workerSubListFullPath);
            }
            else {
                log.info("workerSubListFullPath={} does not exist.", workerSubListFullPath);
            }
        }
        catch (Exception ex ) {
            log.info("get workerSubListFullPath={} children exception", workerSubListFullPath, ex);
        }

        return subList;
    }


    public boolean deleteWorkerFromMySubList(String workerId){
        Boolean isDelOK = true;
        String workerSubListFullPath = PathConstants.MY_SUB_PATH + "/" + workerId;
        try {
            Stat stat = client.checkExists().forPath(workerSubListFullPath);
            if(stat != null){
                client.delete().deletingChildrenIfNeeded().forPath(workerSubListFullPath);
            }
        }
        catch (Exception ex ) {
            log.info("del workerSubListFullPath={} exception", workerSubListFullPath, ex);
        }

        return isDelOK;
    }
    /*
      path是新worker整路径，例如 /myWorkerList/sub-service-8082-1774221102
     */
    public void processNewWorker(String path) {
        Long threadId = Thread.currentThread().getId();
        String instanceId = path.substring(PathConstants.WORKER_PATH.length() + 1);
        log.info("newInstance id={}. 开始处理新加入的worker. threadId={}", instanceId, threadId);

        String workerSubListPath = PathConstants.MY_SUB_PATH + "/" + instanceId;
        try {
            //检查该worker的任务路径（/mySubList/instanceId）路径是否创建了
            Stat stat = client.checkExists().forPath(workerSubListPath);
            if(stat == null) {
                log.info("create path={} for new instanceId={}, threadId={}", workerSubListPath, instanceId, threadId);
                String tmpPath = client.create().forPath(workerSubListPath);
            }

            //开始平均分配这些任务
            int workerCount = getAllLiveWorkerCount();
            int taskCount = getAllSubListCount();

            double tmpAvgCount = (double)taskCount / (double)workerCount;
            int avgTaskCount = (int)Math.ceil(tmpAvgCount);
            log.info("newInstance taskCount={}, workerCount={}, tmpAvgCount={}, avgCount={}, threadId={}",
                    taskCount, workerCount, tmpAvgCount, avgTaskCount, threadId);


            //先实现最简单的当前这些任务直接分配给所有活着的list
            transferTask2NewWorker(workerSubListPath, avgTaskCount);
        }
        catch (Exception ex) {
            log.error("processNewWorker newInstance={}. threadId={}. exception", instanceId, threadId, ex);
        }
    }

    /*
      path 是, 新加入的任务
 */
    public void distributeNewTask2Worker(String uuid, String content) {
        Map<String, String> taskList = new HashMap<>();
        taskList.put(uuid, content);

        List<String> liveWorkerList = getAllLiveWorkerList();

        //开始平均分配这些任务
        int workerCount = getAllLiveWorkerCount();
        int taskCount = getAllSubListCount();

        double tmpAvgCount = (double)taskCount / (double)workerCount;
        int avgTaskCount = (int)Math.ceil(tmpAvgCount);
        log.info("newTask taskCount={}, workerCount={}, tmpAvgCount={}, avgCount={}", taskCount, workerCount, tmpAvgCount, avgTaskCount);

        //是否所有活着的worker都到达或者接近平衡了
        List<String> notReachedAvgWorkerList = new ArrayList<>();

        for (String workerPath : liveWorkerList) {
            //workerPath is like this。 sub-service-8082-1135504170
            //workerId is sub-service-8082-1135504170,  its task list is under /mySubList/sub-service-8082-1135504170
            String workerId = workerPath;
            String mySubListPathForWorker = PathConstants.MY_SUB_PATH + "/" + workerId;
            Map<String, String> workerSubList = getWorkerSubList(mySubListPathForWorker);
            log.info("从path={}获取workerId={}已有任务列表={}", mySubListPathForWorker, workerId, workerSubList);

            int currentCount = workerSubList.size();
            if (workerSubList.size() > avgTaskCount) {
                //从该worker中要转移一部分task出来到taskList
                log.info("workerPath={}, currentCount={} is GT avgCount={}", workerPath, currentCount, avgTaskCount);
                int surplusCount = currentCount - avgTaskCount;
                Map<String, String> tmpTaskList = new HashMap<>();

                Iterator<Map.Entry<String, String>> iterator = workerSubList.entrySet().iterator();
                while (iterator.hasNext() && (surplusCount != 0)) {
                    Map.Entry<String, String> entry = iterator.next();
                    taskList.put(entry.getKey(), entry.getValue());
                    tmpTaskList.put(entry.getKey(), entry.getValue());
                    surplusCount--;
                    iterator.remove();
                }
                //
                reclaimTaskListFromWorker(workerId, tmpTaskList);
            }
            else if (workerSubList.size() < avgTaskCount) {
                //从tasklist分配任务到该worker中，如果现有的taskList与该worker的list合起来还无法到达avgTaskCount
                //那就说明后面的worker还会转移一部分任务出来的，因此将worker记录号到notReachedAvgWorkerList
                log.info("workerPath={}, currentCount={} is LT avgCount={}", workerPath, currentCount, avgTaskCount);
                if ((currentCount + taskList.size()) >= avgTaskCount) {
                    int needCount = avgTaskCount - currentCount;
                    Map<String, String> tmpTaskList = new HashMap<>();

                    Iterator<Map.Entry<String, String>> iterator = taskList.entrySet().iterator();
                    while (iterator.hasNext() && (needCount != 0)) {
                        Map.Entry<String, String> entry = iterator.next();
                        tmpTaskList.put(entry.getKey(), entry.getValue());
                        needCount--;
                        iterator.remove();
                    }
                    //将tmpTasklist转移给workerId
                    log.info("新任务到达。 将任务list={}分配到workerId={}上", tmpTaskList, workerId);
                    distributeNewTaskList2Worker(workerId, tmpTaskList);
                }
                else {
                    log.info("workerId={}任务没有到达平均值。 将任务list={}添加到notReachedAvgWorkerList", workerId, workerPath);
                    notReachedAvgWorkerList.add(workerPath);
                }
            }
            else {
                log.info("workerPath={}, currentCount={} is equal to avgCount={}", workerPath, currentCount,avgTaskCount);
            }
        }

        if(notReachedAvgWorkerList.size() != 0 ) {
            for (String workerPath : notReachedAvgWorkerList) {
                if (taskList.size() == 0) {
                    //任务分配完毕，跳出循环
                    break;
                }
                //workerPath is like this。 sub-service-8082-1135504170
                //workerId is sub-service-8082-1135504170,  it task list is under /mySubList/sub-service-8082-1135504170
                String workerId = workerPath;
                String mySubListPathForWorker = PathConstants.MY_SUB_PATH + "/" + workerId;
                Map<String, String> workerSubList = getWorkerSubList(mySubListPathForWorker);

                int currentCount = workerSubList.size();
                int needCount = avgTaskCount - currentCount;
                Map<String, String> tmpTasklist = new HashMap<>();

                Iterator<Map.Entry<String, String>> iterator = taskList.entrySet().iterator();
                while (iterator.hasNext() && (needCount != 0)) {
                    Map.Entry<String, String> entry = iterator.next();
                    tmpTasklist.put(entry.getKey(), entry.getValue());
                    needCount--;
                    iterator.remove();
                }
                //将tmpTasklist转移给workerId
                log.info("没到达平均任务。 将任务tmpTasklist={}分配给workerId={}", tmpTasklist, workerId);
                distributeNewTaskList2Worker(workerId, tmpTasklist);
            }
        }
    }


    /*
    path 是new worker的全路径， 例如path=[/myWorkerList/sub-service-8082-1774221102],
     */
    private void transferTask2NewWorker(String newPath, int avgTaskCount) {
        Map<String, String> taskList = new HashMap<>();
        List<String> liveWorkerList = getAllLiveWorkerList();

        //是否所有活着的worker都到达或者接近平衡了
        List<String> notReachedAvgWorkerList = new ArrayList<>();

        for (String workerPath : liveWorkerList) {
            //workerPath is like this。 sub-service-8082-1135504170
            //workerId is sub-service-8082-1135504170,  it task list is under /mySubList/sub-service-8082-1135504170
            String workerId = workerPath;
            String mySubListPathForWorker = PathConstants.MY_SUB_PATH + "/" + workerId;
            Map<String, String> workerSubList = getWorkerSubList(mySubListPathForWorker);
            log.info("从path={}获取workerId={}已有任务列表={}", mySubListPathForWorker, workerId, workerSubList);

            int currentCount = workerSubList.size();
            if (workerSubList.size() > avgTaskCount) {
                //从该worker中要转移一部分task出来到taskList
                log.info("workerPath={}, currentCount={} is GT avgCount={}", workerPath, currentCount, avgTaskCount);
                int surplusCount = currentCount - avgTaskCount;
                Map<String, String> tmpTaskList = new HashMap<>();

                Iterator<Map.Entry<String, String>> iterator = workerSubList.entrySet().iterator();
                while (iterator.hasNext() && (surplusCount != 0)) {
                    Map.Entry<String, String> entry = iterator.next();
                    taskList.put(entry.getKey(), entry.getValue());
                    tmpTaskList.put(entry.getKey(), entry.getValue());
                    surplusCount--;
                    iterator.remove();
                }
                //
                reclaimTaskListFromWorker(workerId, tmpTaskList);
            }
            else if (workerSubList.size() < avgTaskCount) {
                //从tasklist分配任务到该worker中，如果现有的taskList与该worker的list合起来还无法到达avgTaskCount
                //那就说明后面的worker还会转移一部分任务出来的，因此将worker记录号到notReachedAvgWorkerList
                log.info("workerPath={}, currentCount={} is LT avgCount={}", workerPath, currentCount, avgTaskCount);
                if ((currentCount + taskList.size()) >= avgTaskCount) {
                    int needCount = avgTaskCount - currentCount;
                    Map<String, String> tmpTaskList = new HashMap<>();

                    Iterator<Map.Entry<String, String>> iterator = taskList.entrySet().iterator();
                    while (iterator.hasNext() && (needCount != 0)) {
                        Map.Entry<String, String> entry = iterator.next();
                        tmpTaskList.put(entry.getKey(), entry.getValue());
                        needCount--;
                        iterator.remove();
                    }
                    //将tmpTasklist转移给workerId
                    distributeNewTaskList2Worker(workerId, tmpTaskList);
                }
                else {
                    notReachedAvgWorkerList.add(workerPath);
                }
            }
            else {
                log.info("workerPath={}, currentCount={} is equal to avgCount={}", workerPath, currentCount, avgTaskCount);
            }
        }

        if(notReachedAvgWorkerList.size() != 0 ) {
            for (String workerPath : notReachedAvgWorkerList) {
                if (taskList.size() == 0) {
                    //任务分配完毕，跳出循环
                    break;
                }
                //workerPath is like this。sub-service-8082-1135504170
                // workerId is sub-service-8082-1135504170,  it task list is under /mySubList/sub-service-8082-1135504170
                String workerId = workerPath;
                String mySubListPathForWorker = PathConstants.MY_SUB_PATH + "/" + workerId;
                Map<String, String> workerSubList = getWorkerSubList(mySubListPathForWorker);

                int currentCount = workerSubList.size();
                int needCount = avgTaskCount - currentCount;
                Map<String, String> tmpTasklist = new HashMap<>();

                Iterator<Map.Entry<String, String>> iterator = taskList.entrySet().iterator();
                while (iterator.hasNext() && (needCount != 0)) {
                    Map.Entry<String, String> entry = iterator.next();
                    tmpTasklist.put(entry.getKey(), entry.getValue());
                    needCount--;
                    iterator.remove();
                }
                //将tmpTasklist转移给workerId
                distributeNewTaskList2Worker(workerId, tmpTasklist);
            }
        }
    }

    /*
     path 是down的worker的全路径， 例如path=[/myWorkerList/sub-service-8082-1774221102],
     */
    public void processDownWorker(String path) {
        String instanceId = path.substring(PathConstants.WORKER_PATH.length() + 1);
        log.info("DownInstance id is {}", instanceId);

        String workerSubListPath = PathConstants.MY_SUB_PATH + "/" + instanceId;
        try {
            //检查该worker有哪些任务需要接管
            Stat stat = client.checkExists().forPath(workerSubListPath);
            if(stat == null) {
                log.info("DownInstance={} has no tasks.", instanceId);
            }
            else {
                List<String> subList = client.getChildren().forPath(workerSubListPath);
                if (subList != null && subList.size() != 0) {
                    log.info("DownInstance={} has {} tasks.", instanceId, subList);
                    //开始接管这些任务
                    int workerCount = getAllLiveWorkerCount();
                    int taskCount = getAllSubListCount();

                    double tmpAvgCount = (double)taskCount / (double)workerCount;
                    int avgTaskCount = (int)Math.ceil(tmpAvgCount);
                    log.info("DownInstance taskCount={}, workerCount={}, tmpAvgCount={}, avgCount={}", taskCount, workerCount, tmpAvgCount, avgTaskCount);

                    //先实现最简单的, 将这些任务直接分配给所有活着的list
                    distributeDownSubList2AllListWorker(workerSubListPath, avgTaskCount);
                    //清理旧的workerSubListPath 路径
                    client.delete().forPath(workerSubListPath);
                    log.info("delete workerSubListPath={}", workerSubListPath);
                }
                else {
                    log.info("DownInstance={} has no tasks or 0 task.", instanceId);
                }
            }
        }
        catch (Exception ex) {
            log.error("processDownWorker path={}. exception", workerSubListPath, ex);
        }
    }



    private void distributeDownSubList2AllListWorker(String downPath, int avgTaskCount) {
        Map<String, String> taskList = getDownWorkerSubList(downPath);
        List<String> liveWorkerList = getAllLiveWorkerList();

        //是否所有活着的worker都到达或者接近平衡了
        boolean isAllWorkerReachedAvg = true;
        List<String> notReachedAvgWorkerList = new ArrayList<>();

        for (String workerPath : liveWorkerList) {
            //workerPath is like this。 sub-service-8082-1135504170
            //workerId is sub-service-8082-1135504170,  it task list is under /mySubList/sub-service-8082-1135504170
            String workerId = workerPath;
            String mySubListPathForWorker = PathConstants.MY_SUB_PATH + "/" + workerId;
            Map<String, String> workerSubList = getWorkerSubList(mySubListPathForWorker);
            log.info("从path={}获取workerId={}已有任务列表={}", mySubListPathForWorker, workerId, workerSubList);

            int currentCount = workerSubList.size();
            if (workerSubList.size() > avgTaskCount) {
                //从该worker中要转移一部分出来到taskList
                log.info("workerPath={} is GT avgCount={}", workerPath, avgTaskCount);
                int surplusCount = currentCount - avgTaskCount;
                Map<String, String> tmpTaskList = new HashMap<>();

                Iterator<Map.Entry<String, String>> iterator = workerSubList.entrySet().iterator();
                while (iterator.hasNext() && (surplusCount != 0)) {
                    Map.Entry<String, String> entry = iterator.next();
                    taskList.put(entry.getKey(), entry.getValue());
                    tmpTaskList.put(entry.getKey(), entry.getValue());
                    surplusCount--;
                    iterator.remove();
                }
                //从该worker上将task移除
                reclaimTaskListFromWorker(workerId, tmpTaskList);
            }
            else if (workerSubList.size() < avgTaskCount) {
                //从tasklist分配任务到该worker中，如果现有的taskList与该worker的list合起来还无法到达avgTaskCount
                //那就说明后面的worker还会转移一部分任务出来的，因此将worker记录号到notReachedAvgWorkerList
                log.info("workerPath={} is LT avgCount={}", workerPath, avgTaskCount);
                if ( (currentCount + taskList.size()) >= avgTaskCount) {
                    int needCount = avgTaskCount - currentCount;
                    Map<String, String> tmpTasklist = new HashMap<>();

                    Iterator<Map.Entry<String, String>> iterator = taskList.entrySet().iterator();
                    while (iterator.hasNext() && (needCount != 0)) {
                        Map.Entry<String, String> entry = iterator.next();
                        tmpTasklist.put(entry.getKey(), entry.getValue());
                        needCount--;
                        iterator.remove();
                    }
                    //将tmpTasklist转移给workerId
                    distributeNewTaskList2Worker(workerId, tmpTasklist);
                }
                else {
                    notReachedAvgWorkerList.add(workerPath);
                }
            }
            else {
                log.info("workerPath={} is equal to avgCount={}", workerPath, avgTaskCount);
            }
        }

        if(!notReachedAvgWorkerList.isEmpty()) {
            for (String workerPath : notReachedAvgWorkerList) {
                if (taskList.size() == 0) {
                    //任务分配完毕，跳出循环
                    break;
                }
                //workerPath is like this。 sub-service-8082-1135504170
                //workerId is sub-service-8082-1135504170,  it task list is under /mySubList/sub-service-8082-1135504170
                String workerId = workerPath;
                String mySubListPathForWorker = PathConstants.MY_SUB_PATH + "/" + workerId;
                Map<String, String> workerSubList = getWorkerSubList(mySubListPathForWorker);

                int currentCount = workerSubList.size();
                int needCount = avgTaskCount - currentCount;
                Map<String, String> tmpTaskList = new HashMap<>();

                Iterator<Map.Entry<String, String>> iterator = taskList.entrySet().iterator();
                while (iterator.hasNext() && (needCount != 0)) {
                    Map.Entry<String, String> entry = iterator.next();
                    tmpTaskList.put(entry.getKey(), entry.getValue());
                    needCount--;
                    iterator.remove();
                }
                //将tmpTasklist转移给workerId
                distributeNewTaskList2Worker(workerId, tmpTaskList);
            }
        }
    }

    private void distributeNewTaskList2Worker(String workerId,  Map<String, String> newTaskMap) {
        try {
            Iterator<Map.Entry<String, String>> iterator = newTaskMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                String uuid = entry.getKey();
                String content = entry.getValue();

                String pathForWorkerAndUuid = PathConstants.MY_SUB_PATH + "/" + workerId + "/"+ uuid;
                log.info("将任务uuid={}分配到workerId={}上, pathForWorkerAndUuid={}", uuid, workerId, pathForWorkerAndUuid);
                Stat stat = client.checkExists().forPath(pathForWorkerAndUuid);
                //理论上如果该uuid 代表的path存在， 那么data应该和content是一致的
                if (stat != null) {
                    stat = client.setData().forPath(pathForWorkerAndUuid, content.getBytes("utf-8"));

                } else {
                    String result = client.create().forPath(pathForWorkerAndUuid, content.getBytes("utf-8"));
                }
            }
        }
        catch (Exception ex) {
            log.info("distributeNewTaskList2Worker, workerId={}", workerId, ex);
        }
    }

    /*
      从workerId上将这些任务移除，应为该worker上任务过重，需要平衡到其他worker上
     */
    private void reclaimTaskListFromWorker(String workerId, Map<String, String> oldTaskMap) {
        try {
            Iterator<Map.Entry<String, String>> iterator = oldTaskMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                String uuid = entry.getKey();
                String content = entry.getValue();

                String pathForWorkerAndUuid = PathConstants.MY_SUB_PATH + "/" + workerId+ "/" + uuid;

                Stat stat = client.checkExists().forPath(pathForWorkerAndUuid);
                //理论上如果该uuid 应该是存在的
                if (stat == null) {
                    log.info("task={} does not exist on pathForWorkerAndUuid={} for workerId={}", uuid,  pathForWorkerAndUuid, workerId);

                } else {
                    client.delete().deletingChildrenIfNeeded().forPath(pathForWorkerAndUuid);
                    log.info("delete task={} from workerId={} by deleting pathForWorkerAndUuid={}", uuid, workerId, pathForWorkerAndUuid);
                }
            }
        }
        catch (Exception ex) {
            log.info("distributeNewTaskList2Worker, workerId={}", workerId, ex);
        }
    }

    private List<String> getAllLiveWorkerList() {
        List<String> liveWorkList = getChildrenList(PathConstants.WORKER_PATH);
        return liveWorkList;
    }

    private List<String> getAllSubList() {
        List<String> allSubList = getChildrenList(PathConstants.ALL_SUB_PATH);
        return allSubList;
    }

    private List<String> getAllWorkerListFromMySubList() {
        List<String> allWorkerListFromMySubList = getChildrenList(PathConstants.MY_SUB_PATH);
        return allWorkerListFromMySubList;
    }

    private List<String> getChildrenList(String parentPath) {
        List<String> allSubList = null;
        try {
            Stat stat = client.checkExists().forPath(parentPath);
            if(stat == null) {
                log.info("parentPath={} is null. can't check its children path.", parentPath);
            }
            else {
                List<String> workerList = client.getChildren().forPath(parentPath);
                if (workerList != null && workerList.size() != 0) {
                    log.info("parentPath={} has {} children.", parentPath, workerList);
                    allSubList = workerList;
                }
                else {
                    log.info("parentPath={} has no child.", parentPath);
                }
            }
        }
        catch (Exception ex) {
            log.error("check parentPath={}. exception", parentPath, ex);
        }

        return allSubList;
    }
    /*
     path 是全路径 例如/mySubList/sub-service-8082-1135504170
    */
    private Map<String, String> getDownWorkerSubList(String path) {
        return getWorkerSubList(path);
    }

    /*
      path 是全路径 例如/mySubList/sub-service-8082-1135504170
     */
    private Map<String, String> getWorkerSubList(String path) {
       Map<String, String> map = new HashMap<>();
        try {
            Stat stat = client.checkExists().forPath(path);
            if(stat == null) {
                log.info("path={} is null. can't check its children path.", path);
            }
            else {
                List<String> subList = client.getChildren().forPath(path);
                log.info("path={} children path list={}", path, subList);
                if (subList != null && subList.size() != 0) {
                    log.info("path={} has {} children.", path, subList);
                    //childrenPath is like, A001, A002 ,uuid
                    for(String childrenPath : subList) {
                        String uuid = childrenPath;
                        String fullPath =  path + "/" +  uuid;
                        byte[] existingValue = client.getData().forPath(fullPath);
                        String content = new String(existingValue,"UTF-8");
                        log.info("fullChildrenPath={}, uuid={}, content={}", fullPath, uuid, content);

                        map.put(uuid, content);
                    }
                }
                else {
                    log.info("path={} has no child.", path);
                }
            }
        }
        catch (Exception ex) {
            log.error("check path={}. exception", path, ex);
        }

        return map;
    }

    private int getAllLiveWorkerCount() {
        return getCount(PathConstants.WORKER_PATH);
    }

    private int getAllSubListCount() {
        return getCount(PathConstants.ALL_SUB_PATH);
    }

    private int getCount(String path) {
        int count = 1 ;
        try {
            Stat stat = client.checkExists().forPath(path);
            if(stat == null) {
                log.info("path={} is null. can't check its children path.", path);
            }
            else {
                List<String> subList = client.getChildren().forPath(path);
                if (subList != null && subList.size() != 0) {
                    log.info("path={} has {} children.", path, subList);
                    count = subList.size();
                }
                else {
                    log.info("path={} has no child.", path);
                }
            }
        }
        catch (Exception ex) {
            log.error("check path={}. exception", path, ex);
        }

        return count;
    }

    public boolean isLeader() {
        return this.isLeader;
    }
}
