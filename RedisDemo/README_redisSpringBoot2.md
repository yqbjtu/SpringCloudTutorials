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

Redis

Lettuce is now used instead of Jedis as the Redis driver when you use spring-boot-starter-data-redis. If you are using higher level Spring Data constructs you should find that the change is transparent.

We still support Jedis. Switch dependencies if you prefer Jedis by excluding io.lettuce:lettuce-core and adding redis.clients:jedis instead.

Connection pooling is optional and, if you are using it, you now need to add commons-pool2 yourself as Lettuce, contrary to Jedis, does not bring it transitively.


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


required a bean of type 'org.springframework.data.redis.support.atomic.RedisAtomicLong'  

在application.properties文件中配置如下内容，由于Spring Boot2.x的改动，连接池相关配置需要通过spring.redis.lettuce.pool或者spring.redis.jedis.pool进行配置了

spring.redis.host=localhost
spring.redis.port=6379
#spring.redis.password=root #根据需要
# 毫秒
spring.redis.timeout=10000
# Redis默认情况下有16个分片，这里配置具体使用的分片，默认是0
spring.redis.database=0
# 默认 8
spring.redis.lettuce.pool.max-active=8
# 连接池最大阻塞等待时间（使用负值表示没有限制） 默认 -1
spring.redis.lettuce.pool.max-wait=-1
#  默认 8
spring.redis.lettuce.pool.max-idle=8
# 默认 0
spring.redis.lettuce.pool.min-idle=0
```


```




 

 