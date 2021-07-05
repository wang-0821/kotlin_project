package com.xiao.boot.mybatis.testing

import com.xiao.boot.base.env.EnvConstants
import com.xiao.boot.base.testing.TestSpringContextUtils
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.test.context.TestContext

/**
 * Only migrate files with ".sql" suffix. Only migrate once around whole test process.
 *
 * Use with @SpringBootTest.
 *
 * @author lix wang
 */
class KtMySqlFlywayMigrationExtension : BeforeAllCallback {
    override fun beforeAll(context: ExtensionContext) {
        if (!migrated) {
            synchronized(KtMySqlFlywayMigrationExtension::class) {
                if (!migrated) {
                    doMigrate(TestSpringContextUtils.getTestContext(context.requiredTestClass))
                }
            }
        }
    }

    private fun doMigrate(testContext: TestContext) {
        val environment = testContext.applicationContext.environment
        val testDatabaseUrl = environment.getProperty(
            EnvConstants.TEST_MYSQL_URL,
            EnvConstants.DEFAULT_TEST_MYSQL_URL
        )
        val testDatabaseUsername = environment.getProperty(
            EnvConstants.TEST_MYSQL_USERNAME,
            EnvConstants.DEFAULT_TEST_MYSQL_USERNAME
        )
        val testDatabasePassword = environment.getProperty(
            EnvConstants.TEST_MYSQL_PASSWORD,
            EnvConstants.DEFAULT_TEST_MYSQL_PASSWORD
        )

        Flyway
            .configure()
            .dataSource(testDatabaseUrl, testDatabaseUsername, testDatabasePassword)
            .sqlMigrationSuffixes(".sql")
            .schemas("testFlywaySchema")
            .load()
            .migrate()
    }

    companion object {
        @Volatile private var migrated = false
    }
}