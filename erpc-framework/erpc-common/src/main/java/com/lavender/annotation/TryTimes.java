package com.lavender.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-05 15:03
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TryTimes {
    int tryTimes() default 3;
    int intervalTime() default 2000;
}
