#redis 多线程读写

http://127.0.0.1:8081/swagger-ui.htmlhttps://docs.spring.io/spring-data/redis/docs/1.8.15.RELEASE/reference/html/  

spring boot 1.5.12默认使用的是spring-data-reds 1.8.11  

2018-10-12 17:43:38.795  INFO 18136 --- [       thread-0] com.yq.controller.RedisController        : threadId=38, oldValue=0, againValue=1
2018-10-12 17:43:38.926  INFO 18136 --- [       thread-1] com.yq.controller.RedisController        : threadId=39, oldValue=1, againValue=2
2018-10-12 17:43:39.032  INFO 18136 --- [       thread-2] com.yq.controller.RedisController        : threadId=40, oldValue=2, againValue=3
2018-10-12 17:43:39.115  INFO 18136 --- [       thread-3] com.yq.controller.RedisController        : threadId=41, oldValue=3, againValue=4
2018-10-12 17:43:39.196  INFO 18136 --- [       thread-4] com.yq.controller.RedisController        : threadId=42, oldValue=4, againValue=5
2018-10-12 17:43:39.533  INFO 18136 --- [       thread-5] com.yq.controller.RedisController        : threadId=43, oldValue=5, againValue=6
2018-10-12 17:43:39.595  INFO 18136 --- [       thread-6] com.yq.controller.RedisController        : threadId=44, oldValue=6, againValue=7
2018-10-12 17:43:39.632  INFO 18136 --- [       thread-7] com.yq.controller.RedisController        : threadId=45, oldValue=6, againValue=7
2018-10-12 17:43:39.641  INFO 18136 --- [       thread-8] com.yq.controller.RedisController        : threadId=46, oldValue=7, againValue=8
2018-10-12 17:43:39.740  INFO 18136 --- [       thread-9] com.yq.controller.RedisController        : threadId=47, oldValue=8, againValue=9