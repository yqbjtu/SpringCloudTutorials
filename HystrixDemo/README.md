
spring.cloud.consul.enabled=false 导致import org.springframework.cloud.client.serviceregistry.Registration;直接无法启动  

因此后面将SvcInfoController注释了  

http://127.0.0.1:7009/swagger-ui.html

http://127.0.0.1:7009/hystrix

http://127.0.0.1:7009/hystrix.stream  在chrome浏览器上不断出现ping  ping的字符串  
http://localhost:7009/turbine.stream 


在程序的入口HystrixDemoApplication类，加上@EnableHystrix注解开启断路器，这个是必须的，并且需要在程序中声明断路点HystrixCommand；
加上@EnableHystrixDashboard注解，开启HystrixDashboard  

http://projects.spring.io/spring-cloud/spring-cloud.html#_turbine 
 
 
 Field registration in org.springframework.cloud.client.serviceregistry.ServiceRegistryAutoConfiguration$ServiceRegistryEndpointConfiguration required a single bean, but 2 were found:
 	- eurekaRegistration: defined by method 'eurekaRegistration' in class path resource [org/springframework/cloud/netflix/eureka/EurekaClientAutoConfiguration.class]
 	- consulRegistration: defined by method 'consulRegistration' in class path resource [org/springframework/cloud/consul/serviceregistry/ConsulAutoServiceRegistrationAutoConfiguration.class]
