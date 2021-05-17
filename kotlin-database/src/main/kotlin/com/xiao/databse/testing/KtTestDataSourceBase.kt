package com.xiao.databse.testing

import com.xiao.base.testing.KtTestBase
import org.junit.jupiter.api.extension.ExtendWith

/**
 *
 * @author lix wang
 */
@ExtendWith(
    FlywayMigrateExtension::class,
    TablesMigrateExtension::class
)
abstract class KtTestDataSourceBase : KtTestBase()