package com.xiao.boot.base.env

import com.xiao.boot.base.KtSpringBootBaseAutoConfiguration
import com.xiao.boot.base.testing.KtSpringTestBase
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

/**
 *
 * @author lix wang
 */
@SpringBootTest(classes = [KtSpringBootBaseAutoConfiguration::class])
class EnvInfoTest : KtSpringTestBase() {
    @Autowired
    lateinit var envInfoProvider: EnvInfoProvider

    @Test
    fun `test env info bean`() {
        Assertions.assertEquals(envInfoProvider.port(), 8088)
        Assertions.assertEquals(envInfoProvider.profile(), ProfileType.TEST)
    }
}