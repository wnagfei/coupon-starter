package com.cbs.springboot.bean;

import java.util.List;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "spring.cloud.stream.kafka.binder")
@EnableConfigurationProperties
public class StreamBean {
  private List<String> brokersBatch;

}
