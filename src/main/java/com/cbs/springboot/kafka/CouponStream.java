package com.cbs.springboot.kafka;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface CouponStream {

  String COUPONS_SEND = "couponsSend";


  @Output(COUPONS_SEND)
  MessageChannel outboundCouponsSendMessage();

}
