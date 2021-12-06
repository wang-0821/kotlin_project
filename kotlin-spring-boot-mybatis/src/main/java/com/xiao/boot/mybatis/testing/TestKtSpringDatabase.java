package com.xiao.boot.mybatis.testing;

import com.xiao.boot.mybatis.database.BaseDatabase;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Repeatable(TestKtSpringDatabases.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TestKtSpringDatabase {
    Class<? extends BaseDatabase> database();
    Class[] mappers() default {};
}
