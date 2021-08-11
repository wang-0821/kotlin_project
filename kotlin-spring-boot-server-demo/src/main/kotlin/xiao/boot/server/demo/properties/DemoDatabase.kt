package xiao.boot.server.demo.properties

import org.springframework.stereotype.Component
import xiao.boot.mybatis.database.BaseDatabase

/**
 *
 * @author lix wang
 */
@Component
@xiao.boot.mybatis.annotation.KtSpringDatabase(
    name = DemoDatabase.NAME,
    mapperBasePackage = "xiao.boot.server.demo.mybatis.mapper",
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