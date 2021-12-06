package com.xiao.database.annotation;

import com.xiao.databse.BaseDatabase;
import com.xiao.databse.annotation.KtTestDatabases;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Repeatable(KtTestDatabases.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface KtTestDatabase {
    Class<? extends BaseDatabase> database();
    Class[] mappers() default {};
}
