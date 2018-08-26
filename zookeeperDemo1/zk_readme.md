
访问http://127.0.0.1:8080

https://github.com/sgroschupf/zkclient

http://127.0.0.1:8081/swagger-ui.html

目前不支持topic中有/,  如果有/需要递归创建
```
<dependency>
     <groupId>org.apache.zookeeper</groupId>
     <artifactId>zookeeper</artifactId>
     <version>3.4.9</version>
 </dependency>
 <dependency>
     <groupId>com.github.sgroschupf</groupId>
     <artifactId>zkclient</artifactId>
     <version>0.1</version>
 </dependency>
```

```
zkClient.subscribeDataChanges("/node", new IZkDataListener(){
            public void handleDataChange(String arg0, Object arg1)
                    throws Exception {
                System.out.println("节点名称："+arg0+"-->修改后的值："+arg1);
            }
 
            public void handleDataDeleted(String arg0) throws Exception {
                System.out.println("删除节点"+arg0+"成功");
            }
        });
```