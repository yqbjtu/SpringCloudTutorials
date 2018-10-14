
spring.cloud.consul.enabled=false 导致import org.springframework.cloud.client.serviceregistry.Registration;直接无法启动  

因此后面将SvcInfoController注释了  

http://127.0.0.1:7009/swagger-ui.html

http://127.0.0.1:7009/hystrix

http://127.0.0.1:7009/hystrix.stream  在chrome浏览器上不断出现ping  ping的字符串