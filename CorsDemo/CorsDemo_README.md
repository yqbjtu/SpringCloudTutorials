# CorsDemo

http://127.0.0.1:6606/
http://localhost:6606/swagger-ui.html#/


https://spring.io/guides/gs/rest-service-cors/

mvn spring-boot:run -Dserver.port=9000

CorsDemo的端口是6606， 我们在hello.js中使用"http://localhost:6606/user/users/2访问rest服务
如果我们打开应用时使用http://127.0.0.1:6606/， 系统会报跨域问题，
而如果我们打开应用使用http://localhost:6606， 就没有跨越问题
