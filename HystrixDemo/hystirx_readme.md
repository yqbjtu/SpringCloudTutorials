
spring.cloud.consul.enabled=false 导致import org.springframework.cloud.client.serviceregistry.Registration;直接无法启动  

因此后面将SvcInfoController注释了  

http://127.0.0.1:7009/swagger-ui.html