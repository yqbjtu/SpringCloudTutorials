# APIZUUL

http://127.0.0.1:7700/
http://localhost:6601/swagger-ui.html#/

gateway启动

http://127.0.0.1:6604/userapi/swagger-ui.html  ---no token
http://127.0.0.1:6604/userapi/swagger-ui.html?token=aaa#

http://127.0.0.1:6601/user/users/2   ---直接访问user服务
http://127.0.0.1:6604/userapi/user/users/2?token=aaa，  通过API网关访问user服务， 用户服务的所有path前面都有user
