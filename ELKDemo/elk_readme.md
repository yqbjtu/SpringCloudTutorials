
https://github.com/SpringDataElasticsearchDevs/spring-data-elasticsearch-sample-application

https://github.com/spring-projects/spring-data-elasticsearch
 <elasticsearch:transport-client id="client" cluster-nodes="ip:9300,ip:9300" cluster-name="elasticsearch" />
 
 
 #使用1.5.15 后者1.。5.2自带的spring-data-elasticsearch
 访问ES serverVersion: 5.4.3 一直报错
 
org.elasticsearch.transport.NodeDisconnectedException: [][192.168.1.101:9300][cluster:monitor/nodes/liveness] disconnected

升级到
http://127.0.0.1:9000/swagger-ui.html