
访问http://127.0.0.1:8080

https://github.com/sgroschupf/zkclient

http://127.0.0.1:8081/swagger-ui.html

http://localhost:8082/swagger-ui.html
```
<dependency>
     <groupId>org.apache.zookeeper</groupId>
     <artifactId>zookeeper</artifactId>
     <version>3.4.9</version>
 </dependency>
 <dependency>
     <groupId>com.github.sgroschupf</groupId>
     <artifactId>zkclient</artifactId>
     <version>0.1</version>
 </dependency>
```

所有新加入的instance， 按照加入时间排序，这样确保分桶排序的不变性




第一个错误
Detected both log4j-over-slf4j.jar AND slf4j-log4j12.jar on the class path, preempting StackOverflowError.

在zookeeper的api中引入exclude后解决

第二个问题
在idea启动加入程序参数--spring.profiles.active=dev1就可以变化profile

测试数据
A001，  cass001a
A002    cass002a
B002    cbss002b
B001    cbss001b

create /allSubList/D001 "cbss001d"
set /allSubList/D001 "cbss001d"
set /allSubList/D002 "cbss002d"
set /allSubList/D003 "cbss003d"

出现新问题，当新的sub加入时没有一个worker是leader

如下这种配置，FILE值记录 org.apache.zookeeper的信息
    <!-- 基础日志输出级别 -->
    <root level="DEBUG"  additivity="false">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="ERROR_FILE"/>
    </root>
    <!-- FILE只记录 org.apache.zookeeper的信息-->
    <logger name="org.apache.zookeeper"  additivity="false">
        <appender-ref ref="FILE" />
    </logger>
    
    
如下这种配置， file中也会记录除com.yq外的， org.springframework等信息
    <logger name="org.springframework" level="INFO" />
    <logger name="org.apache.zookeeper" level="DEBUG" />
    <logger name="org.apache.curator" level="INFO" />

    <!-- 基础日志输出级别 -->
    <root level="DEBUG"  additivity="false">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="ERROR_FILE"/>
    </root>
    <!-- FILE只记录 com.yq的信息-->
    <logger name="com.yq"  additivity="false">
        <appender-ref ref="FILE" />
    </logger>