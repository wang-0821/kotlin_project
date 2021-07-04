package com.xiao.boot.mybatis.properties

import com.xiao.boot.mybatis.annotation.KtSpringDatabase
import com.xiao.boot.mybatis.database.BaseDatabase
import org.springframework.stereotype.Component

/**
 *
 * @author lix wang
 */
@Component
@KtSpringDatabase(
    name = DemoDatabase.NAME,
    mapperBasePackage = "com.xiao.boot.mybatis.mapper",
    mapperXmlPattern = "classpath*:com/xiao/boot/mybatis/mapper/*.xml",
    dataScriptPattern = "classpath*:db/${DemoDatabase.NAME}/*.sql"
)
class DemoDatabase(properties: DemoDatabaseProperties) : BaseDatabase(
    properties.databaseUrl,
    properties.databaseUsername,
    properties.databasePassword
) {
    companion object {
        const val NAME = "demo"
    }
}