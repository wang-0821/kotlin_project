package com.xiao.boot.mybatis.factory

import org.apache.ibatis.session.SqlSessionFactory
import org.mybatis.spring.SqlSessionFactoryBean
import org.springframework.core.io.Resource

/**
 *
 * @author lix wang
 */
class KtSqlSessionFactoryBean(
    private val xmlMapperResources: Array<Resource>
) : SqlSessionFactoryBean() {
    override fun getObject(): SqlSessionFactory {
        setMapperLocations(*xmlMapperResources)
        return super.getObject()!!
    }
}