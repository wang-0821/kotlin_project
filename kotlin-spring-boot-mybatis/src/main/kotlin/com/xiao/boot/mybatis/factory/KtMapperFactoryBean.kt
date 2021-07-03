package com.xiao.boot.mybatis.factory

import org.mybatis.spring.mapper.MapperFactoryBean
import java.lang.reflect.Proxy

/**
 *
 * @author lix wang
 */
class KtMapperFactoryBean<T>(mapperInterface: Class<T>) : MapperFactoryBean<T>(mapperInterface) {
    @Suppress("UNCHECKED_CAST")
    override fun getObject(): T {
        return Proxy.newProxyInstance(
            mapperInterface.classLoader,
            arrayOf(mapperInterface),
            KtSpringMapperProxy(mapperInterface, super.getObject()!!)
        ) as T
    }
}