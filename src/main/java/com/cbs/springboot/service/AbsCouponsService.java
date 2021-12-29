package com.cbs.springboot.service;

import com.cbs.springboot.bean.ThreadLocalPool;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cbs.springboot.bean.CoupousBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class AbsCouponsService implements CouponService {


  public final void build(CoupousBean coupousBean) {
    ThreadLocalPool.threadLocal.set(coupousBean);
  }
  public abstract CoupousBean buildCoupousBean(CoupousBean coupousBean);
  /**
   * 获取CoupousBean实例
   *
   * @return
   */
  public CoupousBean getCoupousBeanInstance() {
    return new CoupousBean();
  }
}
