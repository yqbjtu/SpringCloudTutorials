# ConsulUserSvc 7001

http://127.0.0.1:7001/
http://localhost:7001/swagger-ui.html#/


 也可以直接使用ConsulRegistration, 使用可以获得instanceId   String currentInstId = registration.getInstanceId();
 
 
 deregisterCriticalServiceAfter

spring.cloud.consul.discovery.check.Check.deregisterCriticalServiceAfter=20s

设置了spring.cloud.consul.discovery.healthCheckCriticalTimeout=20s后， 自动就有了deregisterCriticalServiceAfter='20s',

2018-09-28 18:39:07.683  INFO 19724 --- [           main] o.s.c.c.s.ConsulServiceRegistry          : Registering service with consul: NewService{id='user-service-7001-1724970637', name='user-service', tags=[trial, secure=false], address='DESKTOP-8S2E5H7', port=7001, enableTagOverride=null, check=Check{script='null', interval='120s', ttl='null', http='http://DESKTOP-8S2E5H7:7001/actuator/health', tcp='null', timeout='null', deregisterCriticalServiceAfter='20s', tlsSkipVerify=null, status='null'}, checks=null}


To disable the Consul Discovery Client you can set spring.cloud.consul.discovery.enabled to false.
To disable the service registration you can set spring.cloud.consul.discovery.register to false.

You can disable the health check by setting management.health.consul.enabled=false.

https  
https://www.tutorialspoint.com/spring_boot/spring_boot_enabling_https.htm
https://127.0.0.1:7001/swagger-ui.html