package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//实现为某个方法自动填充字段的注解
@Target(ElementType.METHOD)   //用在方法上的注解
@Retention(RetentionPolicy.RUNTIME)   //保留策略
public @interface AutoFill {
    OperationType value();
}
