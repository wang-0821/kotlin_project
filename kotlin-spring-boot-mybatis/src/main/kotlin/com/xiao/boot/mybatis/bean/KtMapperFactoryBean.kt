package com.xiao.boot.mybatis.bean

import org.mybatis.spring.mapper.MapperFactoryBean

/**
 *
 * @author lix wang
 */
class KtMapperFactoryBean<T>(mapperInterface: Class<T>) : MapperFactoryBean<T>(mapperInterface) {
    override fun getObject(): T {
        return super.getObject()!!
    }
}