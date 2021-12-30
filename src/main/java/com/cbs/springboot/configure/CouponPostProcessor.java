package com.cbs.springboot.configure;

import com.cbs.springboot.annotion.Coupons;
import com.cbs.springboot.annotion.CouponsTarget;
import com.cbs.springboot.annotion.EnableCoupons;

import com.cbs.springboot.bean.ThreadLocalPool;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
 
import java.lang.reflect.Method;
 
public class CouponPostProcessor implements ApplicationContextAware, BeanPostProcessor {
 
    private ApplicationContext applicationContext;
 
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
 
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // 获取 添加了 "EnableMyCache" 注解的 bean,然后 返回该 bean 的 cglib 代理对象
        EnableCoupons declaredAnnotation = bean.getClass().getDeclaredAnnotation(EnableCoupons.class);
        if( declaredAnnotation == null ){
            return bean;
        }
        MethodInterceptor methodInterceptor = new MethodInterceptor() {
            @Override
            public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                // 首先查询缓存中有没有，有直接返回，没有再调用目标方法获取结果返回
                CouponsTarget annotation = method.getDeclaredAnnotation(CouponsTarget.class);
                if( annotation == null ){
                    return methodProxy.invokeSuper(object, args);
                }
                Object result = ThreadLocalPool.targetThreadLocal.get();
                if( result != null ){
                    return result;
                }
                result = methodProxy.invokeSuper(object, args);
                ThreadLocalPool.targetThreadLocal.set(String.valueOf(result));
                return result;
            }
        };
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass( bean.getClass());
        enhancer.setCallback( methodInterceptor );
        Object beanPoxy = enhancer.create();
        BeanUtils.copyProperties( bean,beanPoxy );
        return beanPoxy;
    }
}