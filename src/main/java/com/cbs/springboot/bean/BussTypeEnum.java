package com.cbs.springboot.bean;

import lombok.Getter;

public enum BussTypeEnum {


  COUPON(1, "优惠券");

  private Integer code;

  private String message;

  BussTypeEnum(Integer code, String message) {
    this.code = code;
    this.message = message;
  }

  public Integer getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }
}
