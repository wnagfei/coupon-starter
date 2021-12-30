package com.cbs.springboot.tempAspectJ;

import com.cbs.springboot.annotion.CouponsTarget;
import com.cbs.springboot.bean.ThreadLocalPool;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class CacheMapPutAspect {
  @Around("@annotation(com.cbs.springboot.annotion.CouponsTarget)")
  public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method =
        joinPoint
            .getTarget()
            .getClass()
            .getMethod(signature.getName(), signature.getMethod().getParameterTypes());
    CouponsTarget cacheMap = method.getAnnotation(CouponsTarget.class);

    // 强制刷缓存
    Object val;
    // 加锁
    synchronized (this) {
      //       执行目标方法
      val = joinPoint.proceed();
      ThreadLocalPool.targetThreadLocal.set((String) val);
      return val;
    }
  }
}
