##spring-data-redis 升级到springboot2


> [官方文档](#official_docs)
>
> [注意问题](#notes_faq)
>

### 官方文档<a name="official_docs"></a>
Spring Boot 2的release notes  
https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.0-Release-Notes  


Spring Boot 2迁移指南  
https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.0-Migration-Guide

Spring 5迁移指南  
https://github.com/spring-projects/spring-framework/wiki/Upgrading-to-Spring-Framework-5.x#upgrading-to-version-50


https://projects.spring.io/spring-data-redis/  
https://docs.spring.io/spring-data/data-redis/docs/current/reference/html/ 

Spring boot 1.5.12中使用的是spring-data-redis 1.8.11
而Spring Boot 2.0.2中使用的是spring-data-redis 2.0.7

因此需要
https://docs.spring.io/spring-data/data-redis/docs/current/reference/html/#new-in-2.0.0
spring-data-redis从1.8版本就是开始使用Lettuce 

https://docs.spring.io/spring-data/data-redis/docs/current/reference/html/#redis:template



 
### 注意问题<a name="notes_faq"></a>

现在的controller提供了2个rest api，一个设置key value一个根据key获取
http://127.0.0.1:8080/cache/keys/aaa?value=bbbb1  post
http://127.0.0.1:8080/cache/keys/aaa  get  

```


```




 

 