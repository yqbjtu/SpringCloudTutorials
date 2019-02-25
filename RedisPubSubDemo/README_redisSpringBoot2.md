##spring-data-redis 升级到springboot2


> [官方文档](#official_docs)
>
> [注意问题](#notes_faq)
>

### 官方文档<a name="official_docs"></a>
https://spring.io/guides/gs/messaging-redis/

http://127.0.0.1:8080/swagger-ui.html

redis (Windows上修改redis.windows-service.conf，然后重启)配置修改为
 #notify-keyspace-events Esx  ---does not work for set,m only works for expired
 notify-keyspace-events E$x  works well for set and expired
### 注意问题<a name="notes_faq"></a>

```


```




 

 