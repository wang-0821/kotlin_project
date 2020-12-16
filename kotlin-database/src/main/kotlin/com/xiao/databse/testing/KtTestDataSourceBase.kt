package com.xiao.databse.testing

import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

/**
 *
 * @author lix wang
 */
@ExtendWith(
    FlywayMigrateExtension::class,
    TablesMigrateExtension::class
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class KtTestDataSourceBase
