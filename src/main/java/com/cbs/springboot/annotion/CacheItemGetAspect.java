//package com.cbs.springboot.annotion;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.core.DefaultParameterNameDiscoverer;
//import org.springframework.expression.EvaluationContext;
//import org.springframework.expression.Expression;
//import org.springframework.expression.ExpressionParser;
//import org.springframework.expression.spel.standard.SpelExpressionParser;
//import org.springframework.expression.spel.support.StandardEvaluationContext;
//import org.springframework.stereotype.Component;
//import javax.annotation.Resource;
//import java.lang.reflect.Method;
//
//@Aspect
//@Component
//public class CacheItemGetAspect {
//  @Around("@annotation(com.cbs.springboot.annotion.CouponsTarget)")
//  public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
//    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//    Method method =
//        joinPoint
//            .getTarget()
//            .getClass()
//            .getMethod(signature.getName(), signature.getMethod().getParameterTypes());
//    CouponsTarget cacheItemGet = method.getAnnotation(CouponsTarget.class);
//    String value = cacheItemGet.target(); // 创建解析器
//    ExpressionParser parser = new SpelExpressionParser();
//    Expression hKeyExpression = parser.parseExpression(value);
//    // 设置解析上下文有哪些占位符。
//    EvaluationContext context = new StandardEvaluationContext();
//    // 获取方法参数
//    Object[] args = joinPoint.getArgs();
//    String[] parameterNames = new DefaultParameterNameDiscoverer().getParameterNames(method);
//    for (int i = 0; i < parameterNames.length; i++) {
//      context.setVariable(parameterNames[i], args[i]);
//    } // 解析得到 item的 key
//    if (value != null) {
//      return value.toString();
//    }
//    return joinPoint.proceed();
//  }
//}
