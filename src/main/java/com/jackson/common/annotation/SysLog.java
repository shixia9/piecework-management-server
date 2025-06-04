package com.jackson.common.annotation;

import java.lang.annotation.*;

/**
 * 系统日志注解
 *
 * @author at0m1c
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLog {

	String value() default "";
}
