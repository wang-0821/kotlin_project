package com.xiao.boot.server.demo.properties

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
    mapperBasePackage = "com.xiao.boot.server.demo.mybatis.mapper",
    dataScriptPattern = "classpath*:db/${DemoDatabase.NAME}/*.sql"
)
class DemoDatabase(
    properties: DemoProperties
) : BaseDatabase(
    properties.databaseUrl,
    properties.databaseUsername,
    properties.databasePassword
) {
    companion object {
        const val NAME = "demo"
        const val transactionServiceName = "${NAME}TransactionService"
    }
}