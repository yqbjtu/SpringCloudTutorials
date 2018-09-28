# APIZUUL

fallback provider  https://stackoverflow.com/questions/42216079/zuul-implementing-multiple-zuulfallbackprovider-for-multiple-zuul-routes  

https://stackoverflow.com/questions/48046927/how-to-config-default-zuulfallbackprovider-for-zuul  
https://zhuanlan.zhihu.com/p/31793768  

http://127.0.0.1:7700/
http://localhost:6601/swagger-ui.html#/

zuul启动

http://127.0.0.1:7700/userapi/swagger-ui.html  ---no token
http://127.0.0.1:7700/userapi/swagger-ui.html?token=aaa

http://localhost:7001/user/users/2   ---直接访问user服务
http://localhost:7700/userapi/user/users/2?token=aaa，  通过API网关访问user服务， 用户服务的所有path前面都有user
http://localhost:7700/userapi/user/users/2?token=aaa


consul  http://127.0.0.1:8500

当consul上没有对应可用的服务时吗，访问http://localhost:7700/userapi/user/users/2?token=aaa， 直接报错误

018-09-26 13:34:56.746  INFO 1904 --- [nio-7700-exec-2] c.n.l.DynamicServerListLoadBalancer      : DynamicServerListLoadBalancer for client user-service initialized: DynamicServerListLoadBalancer:{NFLoadBalancer:name=user-service,current list of Servers=[],Load balancer stats=Zone stats: {},Server stats: []}ServerList:ConsulServerList{serviceId='user-service', tag=null}
2018-09-26 13:35:45.616 ERROR 1904 --- [nio-7700-exec-2] com.yq.filter.MyFallBackProvider         : zuul exception, msg=Load balancer does not have available server for client: user-service

com.netflix.client.ClientException: Load balancer does not have available server for client: user-service
	at com.netflix.loadbalancer.LoadBalancerContext.getServerFromLoadBalancer(LoadBalancerContext.java:483) ~[ribbon-loadbalancer-2.2.4.jar:2.2.4]
	at com.netflix.loadbalancer.reactive.LoadBalancerCommand$1.call(LoadBalancerCommand.java:184) ~[ribbon-loadbalancer-2.2.4.jar:2.2.4]
	at com.netflix.loadbalancer.reactive.LoadBalancerCommand$1.call(LoadBalancerCommand.java:180) ~[ribbon-loadbalancer-2.2.4.jar:2.2.4]
