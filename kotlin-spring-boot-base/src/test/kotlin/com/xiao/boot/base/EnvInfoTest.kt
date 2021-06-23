package com.xiao.boot.base

import com.xiao.base.testing.KtTestBase
import com.xiao.boot.base.env.EnvInfoProvider
import com.xiao.boot.base.env.ProfileType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

/**
 *
 * @author lix wang
 */
@SpringBootTest(classes = [KtSpringBootBaseAutoConfiguration::class])
class EnvInfoTest : KtTestBase() {
    @Autowired
    lateinit var envInfoProvider: EnvInfoProvider

    @Test
    fun `test env info bean`() {
        Assertions.assertEquals(envInfoProvider.port(), 8088)
        Assertions.assertEquals(envInfoProvider.profile(), ProfileType.TEST)
    }
}