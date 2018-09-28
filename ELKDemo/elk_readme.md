
https://github.com/SpringDataElasticsearchDevs/spring-data-elasticsearch-sample-application

https://github.com/spring-projects/spring-data-elasticsearch
 <elasticsearch:transport-client id="client" cluster-nodes="ip:9300,ip:9300" cluster-name="elasticsearch" />
 
 
 #使用1.5.15 或者1.5.12自带的spring-data-elasticsearch
 访问ES serverVersion: 5.4.3 一直报错
 
org.elasticsearch.transport.NodeDisconnectedException: [][192.168.1.101:9300][cluster:monitor/nodes/liveness] disconnected

升级到 最新的spring-data-search需要springboot升级
http://127.0.0.1:9000/swagger-ui.html

@JsonFormat (shape = JsonFormat.Shape.STRING, pattern ="yyyy-MM-dd'T'HH:mm:ss.SSSZZ"). 

@Field(type = FieldType.Date, index = FieldIndex.not_analyzed, store = true, 
            format = DateFormat.custom, pattern = "yyyy-MM-dd'T'hh:mm:ss.SSS'Z'")
    private Date createDate;