package com.xiao.boot.mybatis.testing

import com.xiao.boot.base.testing.KtSpringTestBase
import org.junit.jupiter.api.extension.ExtendWith

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