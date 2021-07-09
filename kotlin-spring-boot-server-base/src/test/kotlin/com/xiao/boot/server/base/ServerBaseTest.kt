package com.xiao.boot.server.base

import com.xiao.boot.base.testing.KtSpringTestBase
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext

/**
 *
 * @author lix wang
 */
@SpringBootTest(classes = [ServerBaseAutoConfiguration::class])
class ServerBaseTest : KtSpringTestBase() {
    @Autowired
    lateinit var applicationContext: ApplicationContext

    @Test
    fun `test spring server`() {
        
    }
}