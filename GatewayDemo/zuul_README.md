# APIZUUL

http://127.0.0.1:7700/
http://localhost:6601/swagger-ui.html#/

gateway启动

http://127.0.0.1:6604/userapi/swagger-ui.html  ---no token
http://127.0.0.1:6604/userapi/swagger-ui.html?token=aaa#

http://127.0.0.1:6601/user/users/2   ---直接访问user服务
http://127.0.0.1:6604/userapi/user/users/2?token=aaa，  通过API网关访问user服务， 用户服务的所有path前面都有user


启用了
          #而不是我们期望的去掉userapi，只保留**部分
          filters:
          - StripPrefix=1
          - MyPre=foo, bar
          - AddRequestParameter=foo1, bar1
          - UrlFilter
          - AddResponseHeader=X-Response-Foo, Bar2
          之后urlFilter会对url进行过滤
          http://127.0.0.1:6604/userapi2/user/users/2， 可以工作，但是http://127.0.0.1:6604/userapi/user/users/2不行
