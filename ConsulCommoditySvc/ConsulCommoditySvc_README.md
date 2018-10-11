# ConsulCommoditySvc 7002

http://127.0.0.1:7002/
http://localhost:7002/swagger-ui.html#/


 也可以直接使用ConsulRegistration, 使用可以获得instanceId   String currentInstId = registration.getInstanceId();
 

rest API
1, http://localhost:7002/commodity/commodities

2, http://localhost:7002/commodity/commodities/2  
  
通过API网关
http://127.0.0.1:7700/comapi/swagger-ui.html?token=aaa
1, http://localhost:7700/comapi/commodity/commodities?token=aaa   
2, http://localhost:7700/comapi/commodity/commodities/2?token=aaa    