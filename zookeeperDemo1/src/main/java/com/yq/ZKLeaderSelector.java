package com.yq;


import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import java.net.InetAddress;
import java.net.UnknownHostException;



public class ZKLeaderSelector {

    public boolean isLeader = false;
    private ZkClient zkClient = null;
    private String path = "XXXXX";
    private String hostInfo = "initString";

    public void initZKLeaderSelector(String zkServers, String path, String s) {
        zkClient = new ZkClient(zkServers, 10000, 10000);
        this.path = path;
        try {
            hostInfo = InetAddress.getLocalHost().toString();
        } catch (UnknownHostException e) {
        }
        hostInfo = s;
    }

    /**
     * 选举leader
     *
     */
    public void tryLeader() {
        if (!zkClient.exists(path)) {
            try {
                zkClient.createEphemeral(path, hostInfo);
                isLeader = true;
                System.out.println(hostInfo + "  成为leader了");
            } catch (Exception e) {
                System.out.println(hostInfo+"成为leader失败");
                e.printStackTrace();
                isLeader = false;
            }
        }
    }

    /**
     * 监听指定节点的数据变化
     *
     */
    public void testListener() throws InterruptedException {
        // 监听指定节点的数据变化
        zkClient.subscribeDataChanges(path, new IZkDataListener() {
            public void handleDataChange(String s, Object o) throws Exception {
//                System.out.println(hostInfo+"说：");
//                System.out.println("node data changed!");
//                System.out.println("node=>" + s);
//                System.out.println("data=>" + o);
//                System.out.println("--------------");
//                tryLeader();
            }

            public void handleDataDeleted(String s) throws Exception {
                System.out.println(hostInfo+"说：");
                System.out.println("node data deleted!");
                System.out.println("s=>" + s);
                System.out.println("--------------");
                tryLeader();
            }
        });

        System.out.println(hostInfo + " ----- ready!");

    }

    public static void main(String[] args) throws InterruptedException {
        ZKLeaderSelector zk1 = new ZKLeaderSelector();
        zk1.initZKLeaderSelector("127.0.0.1:2181", "/leaderPath","1");

        ZKLeaderSelector zk2 = new ZKLeaderSelector();
        zk2.initZKLeaderSelector("127.0.0.1:2181", "/leaderPath","2");

        ZKLeaderSelector zk3 = new ZKLeaderSelector();
        zk3.initZKLeaderSelector("127.0.0.1:2181", "/leaderPath","3");

        zk1.tryLeader();
        zk2.tryLeader();
        zk3.tryLeader();
        zk1.testListener();
        zk2.testListener();
        zk3.testListener();
        int i = 0;
        // junit测试时，防止线程退出
        while (true) {
            Thread.sleep(1000);
            i++;
            if(i % 5 == 0){
                if(zk1.isLeader){
                    zk1.zkClient.close();
                    System.out.println("1 关闭了");
                    zk1.isLeader = false;
                }
                if(zk2.isLeader){
                    zk2.zkClient.close();
                    System.out.println("2 关闭了");
                    zk2.isLeader = false;
                }
                if(zk3.isLeader){
                    zk3.zkClient.close();
                    System.out.println("3 关闭了");
                    zk3.isLeader = false;
                }
            }
            System.out.println("1"+zk1.isLeader+"       2:"+zk2.isLeader+"   3:"+zk3.isLeader);

        }
    }
}