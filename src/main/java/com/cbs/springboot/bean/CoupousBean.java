package com.cbs.springboot.bean;

import java.util.List;
import java.util.Map;
import lombok.Data;

/** 优惠券bean */

/** 发放对象必传 */
@Data
public class CoupousBean {

  /** 发送对象必传,手机号List，mobile List /OpenIdList/agentCode list */
  private List<String> targetCodes;

  /** 批量使用优惠券优惠券 */
  private List<String> couponsIds;

  /** 发放类型 代理人或者是普通 1代理人 2 普通用户 */
  private Integer type;
  /** 平台编码 */
  private String platformCode;
  /** 业务切入点 */
  private String point;

  /** 优惠券idid */
  private String couponId;


  @Override
  public String toString() {
    return "CoupousBean{" +
        "targetCodes=" + targetCodes +
        ", couponsIds=" + couponsIds +
        ", type=" + type +
        ", platformCode='" + platformCode + '\'' +
        ", point='" + point + '\'' +
        ", couponId='" + couponId + '\'' +
        '}';
  }
  @Data
   class SimpleBean{
      private ReportEnum targetType;
      private List<TargetBean> targetCode;
   }
}
