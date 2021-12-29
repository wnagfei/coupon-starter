package com.cbs.springboot.configure;

import com.cbs.springboot.annotion.CouponsAspect;
import com.cbs.springboot.bean.StreamBean;
import com.cbs.springboot.kafka.CouponStream;
import com.cbs.springboot.service.CouponService;
import com.cbs.springboot.service.impl.CouponServiceImpl;
import java.util.List;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.serialization.StringDeserializer;
import java.util.HashMap;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.kafka.channel.AbstractKafkaChannel;
import org.springframework.integration.kafka.channel.SubscribableKafkaChannel;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.AbstractMessageChannel;
import org.springframework.messaging.support.GenericMessage;

/** Created by wf. */
@Configuration
@ConditionalOnClass(value = {KafkaTemplate.class})
@EnableConfigurationProperties
@EnableAspectJAutoProxy
public class CouponAutoConfigure {

  @Bean
  @ConditionalOnMissingBean(CouponService.class)
  public CouponService getCouponsProperties() {
    return new CouponServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean(StreamBean.class)
  public StreamBean streamBean() {
    return new StreamBean();
  }

  @Bean
  @ConditionalOnMissingBean(CouponsAspect.class)
  public CouponsAspect getAspectJ() {
    return new CouponsAspect();
  }

  @Bean
  @ConditionalOnMissingClass(value = {"CouponStream.class"})
  public CouponStream getCoupon() {
    return new CouponStream() {
      @Override
      public MessageChannel outboundCouponsSendMessage() {
        HashMap<String, Object> configs = new HashMap<>();
        // 设置序列化
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, streamBean().getBrokersBatch());
        configs.put(ProducerConfig.ACKS_CONFIG, "all");
        configs.put("producer.headermode","none");
        configs.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 3);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        DefaultKafkaProducerFactory producerFactory = new DefaultKafkaProducerFactory(configs);
        return new SubscribableKafkaChannel(
            // 设置自定义分区
            new KafkaTemplate(producerFactory),
            new ConcurrentKafkaListenerContainerFactory<>(),
            CouponStream.COUPONS_SEND);
      }
    };
  }
}
