
访问http://127.0.0.1:6605/bullet  会出现Welcome to SockJS!

6604是网关端口
http://127.0.0.1:6604/bullet/info,  显示正常， 结果{"entropy":351872525,"origins":["*:*"],"cookie_needed":true,"websocket":true}

但是http://127.0.0.1:6604/bullet无法正常显示
Whitelabel Error Page

This application has no configured error view, so you are seeing this as a fallback.
Fri Jul 06 17:35:02 CST 2018
There was an unexpected error (type=Bad Request, status=400).
Invalid 'Upgrade' header: {Host=[127.0.0.1:6604], User-Agent=[Mozilla/5.0 (Windows NT 10.0; WOW64; rv:60.0) Gecko/20100101 Firefox/60.0], Accept=[text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8], Accept-Language=[en-US,zh-CN;q=0.8,zh;q=0.7,zh-TW;q=0.5,zh-HK;q=0.3,en;q=0.2], Accept-Encoding=[gzip, deflate], Connection=[keep-alive], Upgrade-Insecure-Requests=[1]}


一定要先启动websocket服务，然后启动gateway，websocket就能通过6604端口正常工作