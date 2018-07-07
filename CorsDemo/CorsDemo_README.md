# CorsDemo

http://127.0.0.1:6606/
http://localhost:6606/swagger-ui.html#/


https://spring.io/guides/gs/rest-service-cors/

mvn spring-boot:run -Dserver.port=6677



CorsDemo的端口是6606， 我们在hello.js中使用"http://localhost:6606/user/users/2访问rest服务
如果我们打开应用时使用http://127.0.0.1:6606/， 系统会报跨域问题，(Invalid CORS request)
而如果我们打开应用使用http://localhost:6606， 就没有跨越问题

如果我们在rest方法上添加了@CrossOrigin(origins = "http://localhost:6677")

然后重新再启动一个java -jar target\CorsDemo-1.0-SNAPSHOT.jar   --server.port=6677，
这时候通过http://localhost:6677，可以看到rest能正常工作，但是如果我们使用http://127.0.01:6677/， 也会出现Invalid CORS request错误