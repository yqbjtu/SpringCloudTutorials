
访问http://127.0.0.1:6605/bullet  会出现Welcome to SockJS!

6604是网关端口
http://127.0.0.1:6604/bullet/info,  显示正常， 结果{"entropy":351872525,"origins":["*:*"],"cookie_needed":true,"websocket":true}

但是http://127.0.0.1:6604/bullet无法正常显示
Whitelabel Error Page

This application has no configured error view, so you are seeing this as a fallback.
Fri Jul 06 17:35:02 CST 2018
There was an unexpected error (type=Bad Request, status=400).
Invalid 'Upgrade' header: {Host=[127.0.0.1:6604], User-Agent=[Mozilla/5.0 (Windows NT 10.0; WOW64; rv:60.0) Gecko/20100101 Firefox/60.0], Accept=[text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8], Accept-Language=[en-US,zh-CN;q=0.8,zh;q=0.7,zh-TW;q=0.5,zh-HK;q=0.3,en;q=0.2], Accept-Encoding=[gzip, deflate], Connection=[keep-alive], Upgrade-Insecure-Requests=[1]}


http://localhost:8087
一定要先启动websocket服务，然后启动gateway，websocket就能通过6604端口正常工作

通过日志发现，如果websocket服务被解析成ws://localhost:6605， 就能进行下去，如果被解析成真实ip地址，就无法进行下去了，这是因为localhost:6605是websocket的真实ip和端口，但是192xxxx反倒无法使用。
因此需要注释掉#eureka.instance.preferIpAddress=true这一条
2018-07-07 00:20:01.849 TRACE 15896 --- [ctor-http-nio-4] o.s.c.g.filter.RouteToRequestUrlFilter   : RouteToRequestUrlFilter start
2018-07-07 00:20:01.849 TRACE 15896 --- [ctor-http-nio-4] o.s.c.g.filter.LoadBalancerClientFilter  : LoadBalancerClientFilter url before: ws://bullet:6604/bullet/141/uqkgxpid/websocket
2018-07-07 00:20:01.849 TRACE 15896 --- [ctor-http-nio-4] o.s.c.g.filter.LoadBalancerClientFilter  : LoadBalancerClientFilter url chosen: ws://localhost:6605/bullet/141/uqkgxpid/websocket
2018-07-07 00:20:01.851 DEBUG 15896 --- [ctor-http-nio-4] o.s.w.r.s.s.s.HandshakeWebSocketService  : Handling http://127.0.0.1:6604/bullet/141/uqkgxpid/websocket with headers: {Host=[127.0.0.1:6604], User-Agent=[Mozilla/5.0 (Windows NT 10.0; WOW64; rv:60.0) Gecko/20100101 Firefox/60.0], Accept=[text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8], Accept-Language=[en-US,en;q=0.5], Accept-Encoding=[gzip, deflate], Sec-WebSocket-Version=[13], Origin=[http://127.0.0.1:8086], Sec-WebSocket-Extensions=[permessage-deflate], Sec-WebSocket-Key=[xaZ4r9o4+U4ciCrKnxmydA==], Connection=[keep-alive, Upgrade], Pragma=[no-cache], Cache-Control=[no-cache], Upgrade=[websocket]}