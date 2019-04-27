
spring.cloud.consul.enabled=false 
导致import org.springframework.cloud.client.serviceregistry.Registration;直接无法启动  

因此后面将SvcInfoController注释了  

http://127.0.0.1:7009/swagger-ui.html

http://127.0.0.1:7009/hystrix

http://cloud.spring.io/spring-cloud-openfeign/single/spring-cloud-openfeign.html#spring-cloud-feign-hystrix-fallback  
http://127.0.0.1:7009/hystrix.stream  在chrome浏览器上不断出现ping  ping的字符串  
http://localhost:7009/turbine.stream 


在程序的入口HystrixDemoApplication类，加上@EnableHystrix注解开启断路器，这个是必须的，
并且需要在程序中声明断路点HystrixCommand；
加上@EnableHystrixDashboard注解，开启HystrixDashboard  

http://projects.spring.io/spring-cloud/spring-cloud.html#_turbine 
 
 
 Field registration in org.springframework.cloud.client.serviceregistry.ServiceRegistryAutoConfiguration$ServiceRegistryEndpointConfiguration required a single bean, but 2 were found:
 	- eurekaRegistration: defined by method 'eurekaRegistration' in class path resource [org/springframework/cloud/netflix/eureka/EurekaClientAutoConfiguration.class]
 	- consulRegistration: defined by method 'consulRegistration' in class path resource [org/springframework/cloud/consul/serviceregistry/ConsulAutoServiceRegistrationAutoConfiguration.class]


虽然user-service宕机了，但是consul上还存在，可以通过discoveryClient能找到user-service，因此Feign client报告了如下错误
{
  "timestamp": 1539520622425,
  "status": 500,
  "error": "Internal Server Error",
  "exception": "feign.RetryableException",
  "message": "connect timed out executing GET http://user-service/user/users/6",
  "path": "/user/feignusers/6"
}

直到discoveryClient报告 "can't find this service": "user-service"， Feign client如下新的错误

{
  "timestamp": 1539520767328,
  "status": 500,
  "error": "Internal Server Error",
  "exception": "java.lang.RuntimeException",
  "message": "com.netflix.client.ClientException: Load balancer does not have available server for client: user-service",
  "path": "/user/feignusers/8"
}

没有使用默认的fallback返回值
  
  
  使用UserServiceClientFallbackFactory之后依然是报
  {
    "timestamp": 1539520767328,
    "status": 500,
    "error": "Internal Server Error",
    "exception": "java.lang.RuntimeException",
    "message": "com.netflix.client.ClientException: Load balancer does not have available server for client: user-service",
    "path": "/user/feignusers/8"
  }
  
  
  Caused by: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'com.yq.client.UserServiceClient': 
  FactoryBean threw exception on object creation; 
  nested exception is java.lang.IllegalStateException: Incompatible fallbackFactory instance. Fallback/fallbackFactory of type class com.yq.client.UserServiceClientFallbackFactory is not assignable to interface feign.hystrix.FallbackFactory for feign client user-service

这是因为
应该使用fallback = FallbackSayService.class， 而我写成 @FeignClient(value = "user-service", fallbackFactory = UserClientFallbackFactory.class)

@FeignClient(value = "user-service", fallback = UserServiceClientFallbackFactory.class)

通过网关访问swagger
http://127.0.0.1:7700/huserapi/swagger-ui.html?token=abc

### case 1
ribbon.ConnectTimeout=30000
ribbon.ReadTimeout=7000
只有第一次走了主要逻辑，后面熔断了，我们启用了feign.hystrix.enabled=true，所以都是直接走fallback，熔断后后面的cost特别小


2019-04-27 21:02:34.488  INFO 25224 --- [-user-service-1] c.n.l.DynamicServerListLoadBalancer      : DynamicServerListLoadBalancer for client user-service initialized: DynamicServerListLoadBalancer:{NFLoadBalancer:name=user-service,current list of Servers=[],Load balancer stats=Zone stats: {},Server stats: []}ServerList:ConsulServerList{serviceId='user-service', tag=null}
2019-04-27 21:02:34.559 ERROR 25224 --- [-user-service-1] c.y.c.UserServiceClientFallbackFactory   : Fallback2, userId=2
2019-04-27 21:02:34.567 ERROR 25224 --- [nio-7009-exec-4] com.yq.controller.UserController         : cost=813, normal
2019-04-27 21:03:42.240 ERROR 25224 --- [-user-service-2] c.y.c.UserServiceClientFallbackFactory   : Fallback2, userId=4
2019-04-27 21:03:42.242 ERROR 25224 --- [nio-7009-exec-2] com.yq.controller.UserController         : cost=4, normal
2019-04-27 21:04:00.208 ERROR 25224 --- [-user-service-3] c.y.c.UserServiceClientFallbackFactory   : Fallback2, userId=4
2019-04-27 21:04:00.208 ERROR 25224 --- [nio-7009-exec-7] com.yq.controller.UserController         : cost=3, normal
2019-04-27 21:04:00.535 ERROR 25224 --- [-user-service-4] c.y.c.UserServiceClientFallbackFactory   : Fallback2, userId=4
2019-04-27 21:04:00.536 ERROR 25224 --- [nio-7009-exec-5] com.yq.controller.UserController         : cost=5, normal
2019-04-27 21:04:00.814 ERROR 25224 --- [-user-service-5] c.y.c.UserServiceClientFallbackFactory   : Fallback2, userId=4
2019-04-27 21:04:00.815 ERROR 25224 --- [nio-7009-exec-4] com.yq.controller.UserController         : cost=4, normal
2019-04-27 21:04:01.063 ERROR 25224 --- [-user-service-6] c.y.c.UserServiceClientFallbackFactory   : Fallback2, userId=4
2019-04-27 21:04:01.064 ERROR 25224 --- [nio-7009-exec-3] com.yq.controller.UserController         : cost=4, normal
2019-04-27 21:04:01.303 ERROR 25224 --- [-user-service-7] c.y.c.UserServiceClientFallbackFactory   : Fallback2, userId=4
2019-04-27 21:04:01.304 ERROR 25224 --- [nio-7009-exec-9] com.yq.controller.UserController         : cost=4, normal
2019-04-27 21:04:01.509 ERROR 25224 --- [-user-service-8] c.y.c.UserServiceClientFallbackFactory   : Fallback2, userId=4
2019-04-27 21:04:01.510 ERROR 25224 --- [nio-7009-exec-8] com.yq.controller.UserController         : cost=3, normal
2019-04-27 21:04:01.759 ERROR 25224 --- [-user-service-9] c.y.c.UserServiceClientFallbackFactory   : Fallback2, userId=4
2019-04-27 21:04:01.760 ERROR 25224 --- [nio-7009-exec-1] com.yq.controller.UserController         : cost=4, normal

### case 2
ribbon.ConnectTimeout=30000
ribbon.ReadTimeout=7000
只有第一次走了主要逻辑，后面已经熔断了，因为feign.hystrix.enabled=false， 所以没有启用fallback，熔断后后面的cost特别小

2019-04-27 21:15:00.413  INFO 23264 --- [nio-7009-exec-1] c.n.l.DynamicServerListLoadBalancer      : DynamicServerListLoadBalancer for client user-service initialized: DynamicServerListLoadBalancer:{NFLoadBalancer:name=user-service,current list of Servers=[],Load balancer stats=Zone stats: {},Server stats: []}ServerList:ConsulServerList{serviceId='user-service', tag=null}
2019-04-27 21:15:00.499 ERROR 23264 --- [nio-7009-exec-1] com.yq.controller.UserController         : cost=452, exception

java.lang.RuntimeException: com.netflix.client.ClientException: Load balancer does not have available server for client: user-service
	at org.springframework.cloud.openfeign.ribbon.LoadBalancerFeignClient.execute(LoadBalancerFeignClient.java:71) ~[spring-cloud-openfeign-core-2.0.0.RELEASE.jar:2.0.0.RELEASE]
	at feign.SynchronousMethodHandler.executeAndDecode(SynchronousMethodHandler.java:97) ~[feign-core-9.5.1.jar:na]
	at feign.SynchronousMethodHandler.invoke(SynchronousMethodHandler.java:76) ~[feign-core-9.5.1.jar:na]
	at feign.ReflectiveFeign$FeignInvocationHandler.invoke(ReflectiveFeign.java:103) ~[feign-core-9.5.1.jar:na]
	at com.sun.proxy.$Proxy131.getUserDetail(Unknown Source) ~[na:na]
	at com.yq.controller.UserController.getUserByFeign2(UserController.java:131) ~[classes/:na]
	
	2019-04-27 21:15:03.072 ERROR 23264 --- [nio-7009-exec-2] com.yq.controller.UserController         : cost=0, exception
    
java.lang.RuntimeException: com.netflix.client.ClientException: Load balancer does not have available server for client: user-service
 
 
 总结case 1和case 2， 之所以ribbon.ReadTimeout=7000没有生效，是因为FeignClient快速发现我们需要的服务不存在，所以直接就熔断了，
 如果要测试ribbon.ReadTimeout=7000的效果需要经user-service开启，同时让它很慢（响应时间超过ReadTimeout=7000）
 
 当user-service在consul上注册时候后，如果我们访问http://127.0.0.1:7009/user/feign2users/2?sleepTimeMillis=60000
 就http200，然后
 2019-04-27 22:32:08.637  INFO 23604 --- [erListUpdater-0] c.netflix.config.ChainedDynamicProperty  : Flipping property: user-service.ribbon.ActiveConnectionsLimit to use NEXT property: niws.loadbalancer.availabilityFilteringRule.activeConnectionsLimit = 2147483647
 2019-04-27 22:32:21.968 ERROR 23604 --- [nio-7009-exec-4] com.yq.controller.UserController         : cost=14654, exception
 
 feign.RetryableException: Read timed out executing GET http://user-service/v1/usersWithSleep/2?sleepTimeMillis=60000
 	at feign.FeignException.errorExecuting(FeignException.java:67) ~[feign-core-9.5.1.jar:na]
 	at feign.SynchronousMethodHandler.executeAndDecode(SynchronousMethodHandler.java:104) ~[feign-core-9.5.1.jar:na]

 	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61) [tomcat-embed-core-8.5.29.jar:8.5.29]
 	at java.lang.Thread.run(Thread.java:748) [na:1.8.0_161]
 Caused by: java.net.SocketTimeoutException: Read timed out
 	at java.net.SocketInputStream.socketRead0(Native Method) ~[na:1.8.0_161]
 	at java.net.SocketInputStream.socketRead(SocketInputStream.java:116) ~[na:1.8.0_161]
 	at java.net.SocketInputStream.read(SocketInputStream.java:171) ~[na:1.8.0_161]
 	at java.net.SocketInputStream.read(SocketInputStream.java:141) ~[na:1.8.0_161]
 	
 	此时配置
 	hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds=500
    ribbon.ConnectTimeout=30000
    ribbon.ReadTimeout=7000
    
    
### 如果我们通过网关调用
    http://127.0.0.1:7700/huserapi/user/feign2users/2?sleepTimeMillis=8000
    
    返回http 200
    {
      "result": "Read timed out executing GET http://user-service/v1/usersWithSleep/2?sleepTimeMillis=8000, 14005"
    }
    
    此时网关的配置是
    zuul.host.connect-timeout-millis=90000
    zuul.host.socket-timeout-millis=60000
    zuul.sensitiveHeaders=
    
    #timeout settings
    #当user-service没注册时，The Hystrix timeout of 70000ms for the command user-service is set lower than the combination of the Ribbon read and connect timeout, 240000ms.
    hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds=70000
    ribbon.ConnectTimeout=60000
    ribbon.ReadTimeout=60000
    
    
    当我们再次调用http://127.0.0.1:7700/huserapi/user/feign2users/2?sleepTimeMillis=60000
    网关返回{
          "result": "Read timed out executing GET http://user-service/v1/usersWithSleep/2?sleepTimeMillis=60000, 14003"
        }
        http 200
        
        http://127.0.0.1:7700/huserapi/user/feign2users/2?sleepTimeMillis=70000
        结果
        {
          "result": "Read timed out executing GET http://user-service/v1/usersWithSleep/2?sleepTimeMillis=70000, 14004"
        }
        http 200
        
        {
          "result": "Read timed out executing GET http://user-service/v1/usersWithSleep/2?sleepTimeMillis=80000, 14004"
        }
        
        可以产出超时时间基本在14000
        
### 当我们将hystrix-user-service自身的ribbon.ReadTimeout修改6000，并且user-service可以正常被访问到
        hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds=500
        ribbon.ConnectTimeout=30000
        ribbon.ReadTimeout=6000
        
        访问http://127.0.0.1:7009/user/feign2users/2?sleepTimeMillis=600000
        {
          "result": "Read timed out executing GET http://user-service/v1/usersWithSleep/2?sleepTimeMillis=600000, 12483"
        }
        
        访问 http://127.0.0.1:7009/user/feign2users/2?sleepTimeMillis=8000
        结果
        {
          "result": "Read timed out executing GET http://user-service/v1/usersWithSleep/2?sleepTimeMillis=8000, 12006"
        }
        
        也就是超时基本在12000
### 当我们将hystrix-user-service自身的ribbon.ReadTimeout修改3000，并且user-service可以正常被访问到
hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds=500
ribbon.ConnectTimeout=30000
ribbon.ReadTimeout=3000
      
      访问http://127.0.0.1:7009/user/feign2users/2?sleepTimeMillis=8000
      
      结果
      {
        "result": "Read timed out executing GET http://user-service/v1/usersWithSleep/2?sleepTimeMillis=8000, 6631"
      }
       也就是超时是6000
        
### 当我们将hystrix-user-service自身的ribbon.ReadTimeout修改1000，并且user-service可以正常被访问到
hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds=500
ribbon.ConnectTimeout=30000
ribbon.ReadTimeout=1000
      
      访问http://127.0.0.1:7009/user/feign2users/2?sleepTimeMillis=8000
      
      结果
     {
       "result": "Read timed out executing GET http://user-service/v1/usersWithSleep/2?sleepTimeMillis=8000, 2658"
     }
             
      也就是超时是2000
        
 ### 当我们将hystrix-user-service自身的ribbon.ReadTimeout修改500，并且user-service可以正常被访问到       
 hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds=5000
 ribbon.ConnectTimeout=30000
 ribbon.ReadTimeout=500    
    
    访问http://127.0.0.1:7009/user/feign2users/2?sleepTimeMillis=7000
    结果
    {
      "result": "Read timed out executing GET http://user-service/v1/usersWithSleep/2?sleepTimeMillis=7000, 1477"
    }
    访问http://127.0.0.1:7009/user/feign2users/2?sleepTimeMillis=6000
    {
      "result": "Read timed out executing GET http://user-service/v1/usersWithSleep/2?sleepTimeMillis=6000, 1005"
    }
   
### ribbon.maxAutoRetries=0 加上这个但是感觉没有效果
    
    
#如下配置没有效果，查实依然是ReadTimeout两倍
hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds=5000
ribbon.ConnectTimeout=30000
ribbon.ReadTimeout=500
hystrix.command.user-service.execution.isolation.thread.timeoutInMilliseconds=3500

###当hystrix.command小于ReadTimeout，依然是ribbon的readTimeout生效
hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds=5000
ribbon.ConnectTimeout=30000
ribbon.ReadTimeout=4000
hystrix.command.user-service.execution.isolation.thread.timeoutInMilliseconds=3500


http://127.0.0.1:7009/user/feign2users/2?sleepTimeMillis=9000

{
  "result": "Read timed out executing GET http://user-service/v1/usersWithSleep/2?sleepTimeMillis=9000, 8004"
}



### 当所有服务都在线，zuul的ribbon readtimeout小于hystrix-user-service自身的ribbon.ReadTimeout
同样的参数
http://127.0.0.1:7700/huserapi/user/feign2users/2?sleepTimeMillis=6000

网关日志
2019-04-27 23:23:42,613 ERROR [DESKTOP-8S2E5H7 http-nio-7700-exec-4] Caller+0	 at com.yq.filter.MyFallBackProvider.fallbackResponse(MyFallBackProvider.java:28)
zuul exception, msg=null
com.netflix.client.ClientException: null
	at com.netflix.client.AbstractLoadBalancerAwareClient.executeWithLoadBalancer(AbstractLoadBalancerAwareClient.java:118)
Caused by: java.lang.RuntimeException: java.net.SocketTimeoutException: Read timed out
http返回值
Status Code: 503 
fallback. service:hystrix-user-service, status:Service Unavailable

如果直接调用hystrix-user-service
http://127.0.0.1:7009/user/feign2users/2?sleepTimeMillis=6000

返回http 200，
{
  "result": "Read timed out executing GET http://user-service/v1/usersWithSleep/2?sleepTimeMillis=6000, 8679"
}

 服务日志
 2019-04-27 23:23:22.348 ERROR 23584 --- [nio-7009-exec-7] com.yq.controller.UserController         : cost=8679, exception
 
 feign.RetryableException: Read timed out executing GET http://user-service/v1/usersWithSleep/2?sleepTimeMillis=6000
 	at feign.FeignException.errorExecuting(FeignException.java:67) ~[feign-core-9.5.1.jar:na]