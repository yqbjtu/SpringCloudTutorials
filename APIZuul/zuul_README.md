# APIZUUL

http://127.0.0.1:7700/
http://localhost:6601/swagger-ui.html#/

zuul启动

http://127.0.0.1:6603/userapi/swagger-ui.html  ---no token
http://127.0.0.1:6603/userapi/swagger-ui.html?token=aaa#

http://localhost:6601/user/users/2   ---直接访问user服务
http://localhost:6603/userapi/user/users/2?token=aaa，  通过API网关访问user服务， 用户服务的所有path前面都有user
