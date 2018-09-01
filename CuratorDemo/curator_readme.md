
访问http://127.0.0.1:8080

https://github.com/sgroschupf/zkclient

http://127.0.0.1:8081/swagger-ui.html
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
