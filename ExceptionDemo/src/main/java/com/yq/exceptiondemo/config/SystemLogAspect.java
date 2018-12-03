
package com.yq.exceptiondemo.config;

import javassist.bytecode.SignatureAttribute;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.lang.reflect.Method;



/**
 * Simple to Introduction
 * className: SystemLogAspect
 *
 * @author EricYang
 * @version 2018/6/9 19:43
 */

@Aspect
@Component
public class SystemLogAspect {
    //本地异常日志记录对象
    private static final Logger logger = LoggerFactory.getLogger(SystemLogAspect. class);

    @Pointcut("@annotation(com.yq.exceptiondemo.config.SystemLog)")
    public void serviceAspect() {
        System.out.println("我是一个切入点");
    }

    /**
     * 前置通知 用于拦截记录用户的操作
     *
     * @param joinPoint 切点
     */
    @Before("serviceAspect()")
    public void before(JoinPoint joinPoint) {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        //读取session中的用户 等其他和业务相关的信息，比如当前用户所在应用，以及其他信息, 例如ip
        String ip = request.getRemoteAddr();
        try {
            System.out.println("doBefore enter。 任何时候进入连接点都调用");
            System.out.println("method requested:" + (joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()"));
            System.out.println("method description:" + getServiceMethodDescription(joinPoint));
            System.out.println("remote ip:" + ip);
            //日志存入数据库

            System.out.println("doBefore end");
        }  catch (Exception e) {
            logger.error("doBefore exception");
            logger.error("exceptionMsg={}", e.getMessage());
        }
    }

    /**
     * 后通知（After advice） ：当某连接点退出的时候执行的通知（不论是正常返回还是异常退出）。
     * @param joinPoint
     */
    @After("serviceAspect()")
    public void after(JoinPoint joinPoint) {
        System.out.println("after  executed. 无论连接点正常退出还是异常退出都调用");
    }

    /**
     * 后通知（After advice） ：当某连接点退出的时候执行的通知（只有正常返回时调用）。
     * @param joinPoint
     */
    @AfterReturning(pointcut = "serviceAspect()")
    public void AfterReturnning(JoinPoint joinPoint)
    {
        System.out.println("AfterReturning executed。只有当连接点正常退出时才调用");
        Object[] objs = joinPoint.getArgs();
    }

    /**
     * 异常通知 用于拦截层记录异常日志
     *
     * @param joinPoint
     * @param e
     */
    @AfterThrowing(pointcut = "serviceAspect()", throwing = "e")
    public void afterThrowing(JoinPoint joinPoint, Throwable e) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();

        String ip = request.getRemoteAddr();
        String params = "";
        if (joinPoint.getArgs() !=  null && joinPoint.getArgs().length > 0) {
            for ( int i = 0; i < joinPoint.getArgs().length; i++) {
                params += (joinPoint.getArgs()[i]) + "; ";
            }
        }
        try {
            System.out.println("doAfterThrowing enter。 只有当连接点异常退出时才调用");
            System.out.println("exception class:" + e.getClass().getName());
            System.out.println("exception msg:" + e.getMessage());
            System.out.println("exception method:" + (joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()"));
            System.out.println("method description:" + getServiceMethodDescription(joinPoint));
            System.out.println("remote ip:" + ip);
            System.out.println("method parameters:" + params);
            //日志存入数据库
            System.out.println("doAfterThrowing end");
        }  catch (Exception ex) {
            logger.error("doAfterThrowing exception");
            logger.error("exception msg={}", ex.getMessage());
        }
        logger.error("method={}, code={}, msg={}, params={}",
                joinPoint.getTarget().getClass().getName() + joinPoint.getSignature().getName(), e.getClass().getName(), e.getMessage(), params);
    }


    /**
     * 获取注解中对方法的描述信息
     * @param joinPoint 切点
     * @return 方法描述
     * @throws Exception
     */
    public static String getServiceMethodDescription(JoinPoint joinPoint)
            throws Exception {
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        Class targetClass = Class.forName(targetName);

        Class<?> classTarget = joinPoint.getTarget().getClass();
        Signature sig = joinPoint.getSignature();
        MethodSignature msig = null;
        if (!(sig instanceof SignatureAttribute.MethodSignature)) {
            throw new IllegalArgumentException("该注解只能用于方法");
        }
        Class<?>[] par = msig.getParameterTypes();
        Method currentMethod = classTarget.getClass().getMethod(sig.getName(),  par);
        String description1 = currentMethod.getAnnotation(SystemLog.class).description();

        Method[] methods = targetClass.getMethods();
        String description = "";
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class[] clazzs = method.getParameterTypes();
                if (clazzs.length == arguments.length) {
                    description = method.getAnnotation(SystemLog.class).description();
                    break;
                }
            }
        }
        return description;
    }

}
