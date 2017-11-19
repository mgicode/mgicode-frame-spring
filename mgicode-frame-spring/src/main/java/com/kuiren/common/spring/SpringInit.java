package com.kuiren.common.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author 彭仁夔
 * @email 546711211@qq.com
 * @time 2017/11/18 20:12
 */
//@Component
public class SpringInit implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringInit.applicationContext == null) {
            SpringInit.applicationContext = applicationContext;
        }
    }

    // 获取applicationContext
    public static ApplicationContext getApplicationContext() {

        return applicationContext;
    }

    // 通过name获取 Bean.
    public static Object getBean(String name) {
        if (getApplicationContext() == null) {
            // TracingClient.error("SpringContext上下文还没有初始化，就调用了其上下文进行跟踪处理");
            return null;
        }
        if (getApplicationContext().containsBean(name)) {

            return getApplicationContext().getBean(name);
        } else {
            return null;
        }
    }


}
