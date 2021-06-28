package com.xiao.boot.mybatis.bean

import org.springframework.beans.factory.FactoryBean
import javax.sql.DataSource

/**
 *
 * @author lix wang
 */
class KtDataSourceFactoryBean : FactoryBean<DataSource> {
    lateinit var database: BaseDatabase

    override fun getObject(): DataSource {
        return database.initDataSource()
    }

    override fun getObjectType(): Class<*> {
        return DataSource::class.java
    }
}