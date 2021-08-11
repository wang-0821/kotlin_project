package xiao.boot.mybatis.testing

import org.flywaydb.core.Flyway
import org.flywaydb.core.api.exception.FlywayValidateException
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.test.context.TestContext
import xiao.boot.base.ServerConstants
import xiao.boot.base.testing.TestSpringContextUtils

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
            ServerConstants.TEST_MYSQL_URL,
            ServerConstants.DEFAULT_TEST_MYSQL_URL
        )
        val testDatabaseUsername = environment.getProperty(
            ServerConstants.TEST_MYSQL_USERNAME,
            ServerConstants.DEFAULT_TEST_MYSQL_USERNAME
        )
        val testDatabasePassword = environment.getProperty(
            ServerConstants.TEST_MYSQL_PASSWORD,
            ServerConstants.DEFAULT_TEST_MYSQL_PASSWORD
        )

        Flyway
            .configure()
            .dataSource(testDatabaseUrl, testDatabaseUsername, testDatabasePassword)
            .sqlMigrationSuffixes(*MIGRATION_FILE_SUFFIX)
            .schemas(*MIGRATION_SCHEMAS)
            .load()
            .apply {
                try {
                    migrate()
                } catch (e: FlywayValidateException) {
                    repair()
                    migrate()
                }
            }
    }

    companion object {
        @Volatile private var migrated = false
        val MIGRATION_FILE_SUFFIX = arrayOf(".sql")
        val MIGRATION_SCHEMAS = arrayOf("testFlywaySchema")
    }
}