package com.cbs.springboot.service.impl;

import com.cbs.springboot.bean.CoupousBean;
import com.cbs.springboot.service.AbsCouponsService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CouponServiceImpl extends AbsCouponsService {
  @private AService aService;
  @Override
  public CoupousBean buildCoupousBean(CoupousBean coupousBean) {
    log.info("构建发送优惠券Bean开始：{}", coupousBean.toString());
    super.build(coupousBean);
    return coupousBean;
  }
}
