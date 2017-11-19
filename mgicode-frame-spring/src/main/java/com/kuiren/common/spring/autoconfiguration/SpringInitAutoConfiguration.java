package com.kuiren.common.spring.autoconfiguration;


import com.kuiren.common.spring.SpringInit;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 彭仁夔
 * @email 546711211@qq.com
 * @time 2017/8/28 09:13
 */
@Configuration
//@EnableConfigurationProperties(ThriftServerProperties.class)
@AutoConfigureOrder
public class SpringInitAutoConfiguration {


    @Bean()
    //@ConditionalOnMissingBean(name = "_checkService")
    public SpringInit springInit() {
        return new SpringInit();
    }

//    @Bean
//    // @ConditionalOnBean(annotation = ThriftController.class)
//    public ThriftServer thriftServer(ThriftServerProperties configure, ApplicationContext applicationContext) {
//        return new ThriftServer(configure, applicationContext);
//    }


}
