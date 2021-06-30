package com.xiao.boot.mybatis.properties

import com.xiao.boot.mybatis.annotation.KtSpringDatabase
import com.xiao.boot.mybatis.bean.BaseDatabase

/**
 *
 * @author lix wang
 */
@KtSpringDatabase(
    name = DemoDatabase.NAME,
    mapperBasePackage = "com.xiao.boot.mybatis.mapper"
)
class DemoDatabase constructor(properties: DemoDatabaseProperties) : BaseDatabase(
    properties.databaseUrl,
    properties.databaseUsername,
    properties.databasePassword
) {
    companion object {
        const val NAME = "demo"
    }
}