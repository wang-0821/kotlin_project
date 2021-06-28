package com.xiao.boot.mybatis.properties

import com.xiao.boot.mybatis.annotation.KtSpringDatabase
import com.xiao.boot.mybatis.bean.BaseDatabase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 *
 * @author lix wang
 */
@Component
@KtSpringDatabase(
    name = DemoDatabase.NAME,
    mapperBasePackage = "com.xiao.boot.mybatis.mapper"
)
class DemoDatabase @Autowired constructor(properties: DemoDatabaseProperties) : BaseDatabase(
    properties.databaseUrl,
    properties.databaseUsername,
    properties.databasePassword
) {
    companion object {
        const val NAME = "demo"
    }
}