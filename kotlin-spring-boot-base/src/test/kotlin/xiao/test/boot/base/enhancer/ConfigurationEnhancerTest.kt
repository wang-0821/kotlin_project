package xiao.test.boot.base.enhancer

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import xiao.boot.base.testing.KtSpringTestBase
import xiao.test.boot.base.KtSpringBootBaseAutoConfiguration

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