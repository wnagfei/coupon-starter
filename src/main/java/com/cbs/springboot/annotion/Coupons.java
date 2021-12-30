package com.cbs.springboot.annotion;

import com.cbs.springboot.bean.BussTypeEnum;
import com.cbs.springboot.bean.CoupousBean;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface Coupons {
  /**
   * 触发业务
   * 触发行为;0不限 1注册 2实名 3入司 4加入企微 5会员升级 6出单 7邀新
   */
  String name() default "";
  //平台编码
  String platformCode() default "";
  //发送给某人以逗号隔开
  String targetCode() default "";

  /**
   * 调用的方法名称
   */
//  String method() default "";
  /** 发放别的还是优惠券的 */
  BussTypeEnum bussTypeEnum() default BussTypeEnum.COUPON;

}
