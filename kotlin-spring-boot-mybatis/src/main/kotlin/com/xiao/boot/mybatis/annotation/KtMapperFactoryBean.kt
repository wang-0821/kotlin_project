package com.xiao.boot.mybatis.annotation

import org.mybatis.spring.mapper.MapperFactoryBean

/**
 *
 * @author lix wang
 */
class KtMapperFactoryBean<T>(mapperInterface: Class<T>) : MapperFactoryBean<T>(mapperInterface) {

}