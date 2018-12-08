
spring.cloud.consul.enabled=false 
导致import org.springframework.cloud.client.serviceregistry.Registration;直接无法启动  

因此后面将SvcInfoController注释了  

http://127.0.0.1:7009/swagger-ui.html

http://127.0.0.1:7009/hystrix

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