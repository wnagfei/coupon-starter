package com.cbs.springboot.service;

import com.cbs.springboot.bean.CoupousBean;

public interface CouponService {

  void build(CoupousBean coupousBean);

  CoupousBean getCoupousBeanInstance();
}
