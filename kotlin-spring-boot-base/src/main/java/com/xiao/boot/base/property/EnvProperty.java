package com.xiao.boot.base.property;

import com.xiao.boot.base.ServerConstants;
import com.xiao.boot.base.env.ProfileType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Repeatable(EnvProperties.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnvProperty {
    ProfileType[] profiles() default {};
    String value() default "";
    boolean allowEmpty() default false;
    boolean encrypt() default false;
    String encryptKey() default ServerConstants.ENV_ENCRYPT_KEY;
}
