package com.jackson.modules.app.jwt;


import java.lang.annotation.*;

/**
 * JWT登录效验
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JwtLogin {
    //该字段用于做权限控制
    String[] value() default {};
}
