package com.das.kettle.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.Semaphore;

@Aspect
@Component
public class KettleRunAop {

    private static final Logger LOGGER = LoggerFactory.getLogger(KettleRunAop.class);
    private final Semaphore semp = new Semaphore(15);

    @Pointcut("execution(public * com.das.kettle.web.KettleController.runJob(..))")
    public void pointcut(){
    }

    @Around("pointcut()")
    public void aroundRunJob(ProceedingJoinPoint jp) throws Throwable {
        semp.acquire();
        LOGGER.info("开始尝试获取信号锁，当前锁数量还剩余：" + semp.availablePermits());
        jp.proceed();
        semp.release();
        LOGGER.info("信号锁已释放，当前锁数量：" + semp.availablePermits());
    }

}
