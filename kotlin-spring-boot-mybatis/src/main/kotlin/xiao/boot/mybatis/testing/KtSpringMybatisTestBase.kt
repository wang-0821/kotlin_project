package xiao.boot.mybatis.testing

import org.junit.jupiter.api.extension.ExtendWith
import xiao.boot.base.testing.KtSpringTestBase

/**
 *
 * @author lix wang
 */
@ExtendWith(
    value = [
        KtMySqlFlywayMigrationExtension::class,
        KtMySqlTablesMigrationExtension::class
    ]
)
open class KtSpringMybatisTestBase : KtSpringTestBase()