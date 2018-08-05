##spring-data-redis cache


> [官方文档](#official_docs)
>
> [注意问题](#notes_faq)
>

https://www.journaldev.com/18141/spring-boot-redis-cache
post man , get
http://127.0.0.1:8080/1
http://127.0.0.1:8080/1
http://127.0.0.1:8080/2
http://127.0.0.1:8080/2


put  http://127.0.0.1:8080/update

{"id":4,"name":"ZhangSan","followers":500}

Redis Cache Limits

Although Redis is very fast, it still has no limits on storing any amount of data on a 64-bit system. It can only store 3GB of data on a 32-bit system. More available memory can result into a more hit ratio but this will tend to cease once too much memory is occupied by Redis.
When cache size reaches the memory limit, old data is removed to make place for new one.
 

 