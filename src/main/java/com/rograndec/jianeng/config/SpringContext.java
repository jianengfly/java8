package com.rograndec.jianeng.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextAware;

public class SpringContext implements ApplicationContextAware {
    @Autowired
    private static org.springframework.context.ApplicationContext springContext;

    @Override
    public void setApplicationContext (org.springframework.context.ApplicationContext applicationContext) throws BeansException {
        springContext = applicationContext;
    }

    public static Object getBean (String beanName) {
        return springContext.getBean(beanName);
    }
}
