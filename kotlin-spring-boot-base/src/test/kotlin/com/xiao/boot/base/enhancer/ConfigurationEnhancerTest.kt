package com.xiao.boot.base.enhancer

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
class ConfigurationEnhancerTest : KtSpringTestBase() {
    @Autowired
    lateinit var classA: ClassA

    @Autowired
    lateinit var classB: ClassB

    @Test
    fun `test configuration bean method enhancer`() {
        Assertions.assertEquals(classA.classB, classB)
    }
}