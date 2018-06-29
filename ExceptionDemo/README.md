# SpringCloudTutorials

http://127.0.0.1:8085/swagger-ui.html

spring.mvc.throw-exception-if-no-handler-found=true

@ExceptionHandler表示该方法可以处理的异常，可以多个，比如
@ExceptionHandler({ NullPointerException.class, DataAccessException.class})
也可以针对不同的异常写不同的方法。@ExceptionHandler(Exception.class)可以处理所有的异常类型。



AfterReturningAdvice 
ThrowsAdvice
MethodInterceptor

 事务 与 Afterthrowing 冲突， 通过order解决