package com.cbs.springboot.annotion;

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
      // 定义返回成功的标识
      List list = new ArrayList();
      list.add("200");
      list.add("SUCCESS");
      list.add("0");
      list.add(200);
      list.add(0);
      MethodSignature signature = (MethodSignature) point.getSignature();
      Method method = signature.getMethod();
      Coupons coupons = method.getAnnotation(Coupons.class);

      if (StringUtils.isEmpty(coupons.name()) && StringUtils.isEmpty(coupons.value())) {
        log.info("优惠券发放业务不能为空");
        return null;
      }
      // 判断如果返回成功
      if (!ObjectUtils.isEmpty(result)) {
        HashMap jsonObject =
            objectMapper.readValue(objectMapper.writeValueAsString(result), HashMap.class);
        if (jsonObject.get("code") != null && list.contains(jsonObject.get("code"))
            || jsonObject.get("status") != null && list.contains(jsonObject.get("status"))) {
          canSend = true;
        }
        // 判断是否可以发放优惠券
        if (canSend) {
          CoupousBean coupousBean = ThreadLocalPool.threadLocal.get();
          coupousBean.setPoint(coupons.name());
          log.info("CouponsAspect处理发放优惠券入参：{}", objectMapper.writeValueAsString(coupousBean));
          sendMsg(StatusEnum.STREAM_DATA, coupousBean);
          log.info("CouponsAspect处理发放优惠券已经发送");
        }
      }
    } catch (Exception e) {
      log.warn("发送切面处理异常");
      return null;
    } finally {
      return null;
    }
  }
  /**
   * 发送 优惠券消息
   *
   * @param type
   * @param data
   */
  public void sendMsg(StatusEnum type, CoupousBean data) throws JsonProcessingException {
    String json = objectMapper.writeValueAsString(data); // 要发送的消息
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
      log.info("coupons->sendMsg,type={} 成功,msg={}", type, svo.toString());
    } else {
      log.error("coupons->sendMsg,id={} 失败,msg={}", type, svo.toString());
    }
  }
}
