package com.xiao.boot.base

import com.xiao.base.testing.KtTestBase
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

/**
 *
 * @author lix wang
 */
@SpringBootTest(classes = [KtSpringBootBaseAutoConfiguration::class])
class EnvPropertyTest : KtTestBase() {
    @Test
    fun `test env properties`() {
    }
}