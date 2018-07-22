# API gateway

http://127.0.0.1:7443/
http://localhost:7443/swagger-ui.html#/

gateway启动

http://127.0.0.1:7443/userapi/swagger-ui.html  ---no token
http://127.0.0.1:7443/userapi/swagger-ui.html?token=aaa#

http://127.0.0.1:7001/user/users/2   ---直接访问user服务
http://127.0.0.1:7443/userapi/user/users/2?token=aaa，  通过API网关访问user服务， 用户服务的所有path前面都有user


consul agent -data-dir dir1 -node=192.168.1.102  -bind=192.168.1.102  -datacenter=dc1 -ui  -server -bootstrap-expect 1  只能通过http://127.0.0.1:8500/ui/dc1/services 
以该命令启动consul， 无法无法注册因为
2018-07-22 17:53:12.682 ERROR 17424 --- [           main] o.s.c.c.s.ConsulServiceRegistry          : 
Error registering service with consul: NewService{id='saas-gateway-service', name='saas-gateway-service', 
tags=[trial, secure=false], address='localhost', port=7443, enableTagOverride=null, check=Check{script='null', interval='10s', ttl='null', http='http://localhost:7443/actuator/health', tcp='null', timeout='null', deregisterCriticalServiceAfter='null', tlsSkipVerify=null, status='null'}, checks=null}
com.ecwid.consul.transport.TransportException: org.apache.http.conn.HttpHostConnectException: Connect to 192.168.1.102:8500 [/192.168.1.102] failed: Connection refused: connect


必须修改application.properties, 将spring.cloud.consul.host=192.168.1.102 修改为spring.cloud.consul.host=127.0.0.1