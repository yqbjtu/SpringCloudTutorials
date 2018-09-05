package com.yq.Constant;

/**
 * Simple to Introduction
 * className: PathConstants
 *
 * @author EricYang
 * @version 2018/9/2 12:49
 */
public class PathConstants {
    /*     所有的任务都是放入这个path下面
       路径， /subList/uuidA. 内容为 axouosfs/zton/aoe
       路径， /subList/uuidB. 内容为 axzruuy/noys/iue
       路径， /subList/uuidC. 内容为 aytimxf/pnkyt/lyx
     */
    public static final String ALL_SUB_PATH= "/allSubList";


    /*临时节点，所有的work都去该路径下面创建childPath，使用自己的serviceId， 然后写上自己的当前时间为数据内容。
           路径， /myWorkerList/SVCA. 内容为 long的时间戳， 也及时服务注册时间
           路径， /myWorkerList/SVCB. 内容为 ong的时间戳， 也及时服务注册时间
           路径， /myWorkerList/SVCC. 内容为 ong的时间戳， 也及时服务注册时间
     */
    public static final String WORKER_PATH = "/myWorkerList";

    /*临时节点，所有的work都去该路径下面创建childPath，使用任务的uuid， 然后任务的内容。
       路径， /mySubList/SVCA/uuidA. 内容为 axouosfs/zton/aoe
       路径， /mySubList/SVCA/uuidB. 内容为 axzruuy/noys/iue
       路径， /mySubList/SVCC/uuidC. 内容为 aytimxf/pnkyt/lyx
    */
    public static final String MY_SUB_PATH = "/mySubList";


    public static final String LEADER_PATH = "/worker/leader";
}
