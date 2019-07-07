# ConsulCfgCenter 7001

http://127.0.0.1:7001/
http://localhost:7001/swagger-ui.html#/


https  
https://www.tutorialspoint.com/spring_boot/spring_boot_enabling_https.htm
https://127.0.0.1:7001/swagger-ui.html

###配置如下时
spring:
  application:
    name: user-service
  cloud:
    consul:
      port: 8500
      host: 127.0.0.1
      config:
        enabled: true
        format: properties
        defaultContext: application
        profileSeparator: ','
        watch:
          enabled: true
          delay: 10000
          
日志为          
2019-07-07 10:24:15.127  INFO 18912 --- [           main] b.c.PropertySourceBootstrapConfiguration : Located property source: CompositePropertySource {name='consul', propertySources=  
[ConsulPropertySource {name='config/user-service,dev/'},   
ConsulPropertySource {name='config/user-service/'},   
ConsulPropertySource {name='config/application,dev/'},   
ConsulPropertySource {name='config/application/'}]}