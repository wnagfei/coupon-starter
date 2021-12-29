package com.cbs.springboot.service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("coupons.client")
@Data
@Component
@EnableConfigurationProperties(value = CouponsProperties.class)
public class CouponsProperties {
  private String sendUri = "http://equity/imCouponsInfo/v1/send";

  private String useUri = "http://equity/imCouponsInfo/v1/use";
}
