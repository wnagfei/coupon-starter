package com.cbs.springboot.annotion;

import static java.lang.Enum.valueOf;
import static org.apache.logging.log4j.message.MapMessage.MapFormat.JSON;

import com.cbs.springboot.bean.StatusEnum;
import com.cbs.springboot.bean.StreamsVo;
import com.cbs.springboot.bean.ThreadLocalPool;
import com.cbs.springboot.kafka.CouponStream;

import com.cbs.springboot.bean.CoupousBean;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Aspect
@Slf4j
@EnableBinding(CouponStream.class)
public class CouponsAspect {
  @Autowired private CouponStream couponStream;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @AfterReturning(value = "@annotation(com.cbs.springboot.annotion.Coupons)", returning = "result")
  public Object doAfterReturning(JoinPoint point, Object result) {
    try {
      boolean canSend = false;
      // ???????????????????????????
      List list = new ArrayList();
      list.add("200");
      list.add("SUCCESS");
      list.add("0");
      list.add(200);
      list.add(0);
      MethodSignature signature = (MethodSignature) point.getSignature();
      Method method = signature.getMethod();
      Coupons coupons = method.getAnnotation(Coupons.class);

      if (StringUtils.isEmpty(coupons.name()) && StringUtils.isEmpty(coupons.name())) {
        log.info("?????????????????????????????????");
        return null;
      }
      List<String> targetCodes = null;
      if (coupons.targetCode() != null) {

        targetCodes = getTargetCodes(coupons.targetCode(), method, point.getArgs());
        if (targetCodes==null){
          targetCodes = new ArrayList<>();
        }
      }

      // ????????????????????????
      if (!ObjectUtils.isEmpty(result)) {
        HashMap jsonObject =
            objectMapper.readValue(objectMapper.writeValueAsString(result), HashMap.class);
        if (jsonObject.get("code") != null && list.contains(jsonObject.get("code"))
            || jsonObject.get("status") != null && list.contains(jsonObject.get("status"))) {
          canSend = true;
        }
        // ?????????????????????????????????
        if (canSend) {
          String s = ThreadLocalPool.targetThreadLocal.get();
          CoupousBean coupousBean = ThreadLocalPool.threadLocal.get();
          if (null==coupousBean){
            coupousBean = new CoupousBean();
          }
          if (targetCodes!=null){
            targetCodes.add(s);
            coupousBean.setTargetCodes(targetCodes);
          }
          coupousBean.setPoint(coupons.name());
          log.info("CouponsAspect??????????????????????????????{}", objectMapper.writeValueAsString(coupousBean));
          sendMsg(StatusEnum.STREAM_DATA, coupousBean);
          log.info("CouponsAspect?????????????????????????????????");
        }
      }
    } catch (Exception e) {
      log.warn("????????????????????????");
      return null;
    } finally {
      return null;
    }
  }
  /**
   * ?????? ???????????????
   *
   * @param type
   * @param data
   */
  public void sendMsg(StatusEnum type, CoupousBean data) throws JsonProcessingException {
    String json = objectMapper.writeValueAsString(data); // ??????????????????
    StreamsVo svo = new StreamsVo();
    svo.setOperation(type.getCode());
    svo.setPayload(json);
    String sv = objectMapper.writeValueAsString(svo);
    boolean send =
        couponStream
            .outboundCouponsSendMessage()
            .send(
                MessageBuilder.withPayload(sv)
                    .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                    .build());
    if (send) {
      log.info("coupons->sendMsg,type={} ??????,msg={}", type, svo.toString());
    } else {
      log.error("coupons->sendMsg,id={} ??????,msg={}", type, svo.toString());
    }
  }
  public List<String> getTargetCodes(String targetCodeSpe,Method method,Object[] args){
    LocalVariableTableParameterNameDiscoverer localVariableTable = new LocalVariableTableParameterNameDiscoverer();
    String[] paraNameArr = localVariableTable.getParameterNames(method);
    //??????SPEL??????key?????????
    ExpressionParser parser = new SpelExpressionParser();
    //SPEL?????????
    StandardEvaluationContext context = new StandardEvaluationContext();
    //?????????????????????SPEL????????????
    for(int i=0;i<paraNameArr.length;i++) {
      context.setVariable(paraNameArr[i], args[i]);
    }
    String target = null;
    // ??????????????????????????????????????????
    if(targetCodeSpe.matches("^#.*.$")) {
      target = parser.parseExpression(targetCodeSpe).getValue(context, String.class);
      if (!StringUtils.isEmpty(target)){
        String[] split = target.split(",");
        return  Arrays.asList(split);
      }
    }
    return null;
  }
//  @Around(value = "@annotation(com.cbs.springboot.annotion.CouponsTarget)")
//  public Object doAroud(ProceedingJoinPoint point) {
//    try {
//      ThreadLocalPool.targetThreadLocal.set("46541521");
//      return point.proceed();
//    } catch (Exception e) {
//      log.warn("????????????????????????");
//      return null;
//    } finally {
//      return null;
//    }
//  }
}
