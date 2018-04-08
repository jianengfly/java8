package com.rograndec.jianeng.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.rograndec.jianeng.annotation.enumtype.ShiroCheckType;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ShiroCheck {
    ShiroCheckType[] value();
}
