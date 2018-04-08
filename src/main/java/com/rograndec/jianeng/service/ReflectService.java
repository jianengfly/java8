package com.rograndec.jianeng.service;
import com.rograndec.jianeng.annotation.PasswordCheckType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.stereotype.Service;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.regex.Pattern;
import java.lang.reflect.Method;

public class ReflectService {
    private static final String SERVICE_PACKAGE_PATH = "com.rograndec.jianeng.service";

    private static final String AUTH_PACKAGE_PATh = "com.rograndec.jianeng.auth";

    private static final Logger logger = LoggerFactory.getLogger(ReflectService.class);

    static final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);

    static Set<BeanDefinition> classes;

    static Set<BeanDefinition> authClasses;

    static {
        provider.addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*")));

        classes = provider.findCandidateComponents(SERVICE_PACKAGE_PATH);
        authClasses = provider.findCandidateComponents(AUTH_PACKAGE_PATh);
    }

    /**
     * 获取service的方法
     *
     * @param serviceName
     * @param methodName
     * @return
     */
    public static Method getMethod(String serviceName, String methodName) {
        String clazzPattern = ".*" + serviceName;
        String methodPattern = ".*" + methodName;
        for (BeanDefinition bean : classes) {
            try {
                Class<?> clazz = Class.forName(bean.getBeanClassName());
                if (!clazz.isAnnotationPresent(Service.class) || !Pattern.matches(clazzPattern, clazz.getName())) {
                  continue;
                }
                for (Method m : clazz.getMethods()) {
                    if (!Pattern.matches(methodPattern, m.getName())) {
                        continue;
                    }
                    return m;
                }
            } catch (Throwable e) {
                ;
            }
        }
        return null;
    }

    /**
     * 获取某注解类的某注解方法
     * @param clazzAnnotation
     * @param methodAnnotation
     * @return
     */
    public static Method getServiceMethod (Class<? extends Annotation> clazzAnnotation, Class<? extends Annotation> methodAnnotation) {
        for (BeanDefinition bean : classes) {
          try {
              Class<?> clazz = Class.forName(bean.getBeanClassName());
              if (!clazz.isAnnotationPresent(clazzAnnotation)) {
                  continue;
              }
              for (Method m : clazz.getMethods()) {
                  if (!m.isAnnotationPresent(methodAnnotation)) {
                      continue;
                  }
                  return m;
              }
          } catch (Throwable e) {
              ;
            }
        }
        return null;
    }


    /**
     * 获取启用的密码验证规则类
     *
     * @param clazzAnnotation
     * @return
     */
    public static Class<?> getPasswordCheckTypeClazz(Class<? extends Annotation> clazzAnnotation) {
        for (BeanDefinition bean : authClasses) {
            try {
                Class<?> clazz = Class.forName(bean.getBeanClassName());
                if (!clazz.isAnnotationPresent(clazzAnnotation)) {
                  continue;
                }
                if (clazz.getAnnotation(PasswordCheckType.class).enabled()) {
                    return clazz;
                }
            } catch (Throwable e) {
                ;
            }
        }
        return null;
    }
}
