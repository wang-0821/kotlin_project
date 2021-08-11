package xiao.test.boot.mybatis.properties

import org.springframework.stereotype.Component
import xiao.boot.mybatis.database.BaseDatabase

/**
 *
 * @author lix wang
 */
@Component
@xiao.boot.mybatis.annotation.KtSpringDatabase(
    name = DemoDatabase.NAME,
    mapperBasePackage = "xiao.test.boot.mybatis.mapper",
    mapperXmlPattern = "classpath*:mybatis/mapper/*.xml",
    dataScriptPattern = "classpath*:db/${DemoDatabase.NAME}/*.sql"
)
class DemoDatabase(properties: DemoDatabaseProperties) : BaseDatabase(
    properties.databaseUrl,
    properties.databaseUsername,
    properties.databasePassword
) {
    companion object {
        const val NAME = "demo"
        const val transactionServiceName = "demoTransactionService"
    }
}