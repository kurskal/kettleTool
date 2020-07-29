package com.das.kettle.aop;

import com.das.kettle.lock.Lock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

@Aspect
@Component
public class KettleRunAop {

    private static final Logger LOGGER = LoggerFactory.getLogger(KettleRunAop.class);

    @Autowired
    Lock lockUtil;

    @Pointcut("execution(public * com.das.kettle.web.KettleController.runJob(..))")
    public void pointcut(){
    }

    @Around("pointcut()")
    public void aroundRunJob(ProceedingJoinPoint jp) throws Throwable {
        String kettleName = (String)getParameter(jp).get("kettleName");
        Semaphore semaphore = lockUtil.getSemaphore(kettleName);
        semaphore.acquire();
        LOGGER.info("开始尝试获取信号锁，当前锁数量还剩余：" + semaphore.availablePermits());
        jp.proceed();
        semaphore.release();
        LOGGER.info("信号锁已释放，当前锁数量：" + semaphore.availablePermits());
    }

    private static Map<String, Object> getParameter(ProceedingJoinPoint jp) {
        Object[] args = jp.getArgs();
        ParameterNameDiscoverer pnd = new DefaultParameterNameDiscoverer();
        MethodSignature signature = (MethodSignature) jp.getSignature();
        Method method = signature.getMethod();
        String[] parameterNames = pnd.getParameterNames(method);
        Map<String, Object> map = new HashMap<>();
        for(int i = 0; i < parameterNames.length; i++) {
            map.put(parameterNames[i], args[i]);
        }
        return map;
    }

}
