package xiao.databse.testing

import org.junit.jupiter.api.extension.ExtendWith
import xiao.base.testing.KtTestBase

/**
 *
 * @author lix wang
 */
@ExtendWith(
    FlywayMigrateExtension::class,
    TablesMigrateExtension::class
)
abstract class KtTestDataSourceBase : KtTestBase()