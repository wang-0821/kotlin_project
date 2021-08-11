package xiao.beans

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import xiao.base.testing.KtTestBase
import xiao.base.util.packageName
import xiao.beans.context.BeanHelper
import xiao.beans.context.BeanRegistry
import xiao.beans.context.Context
import xiao.beans.context.ContextScanner

/**
 *
 * @author lix wang
 */
class BeanHelperTest : KtTestBase() {
    class BeanComponentTest
    class BeanClass(val beanComponentTest: BeanComponentTest)
    private lateinit var beanRegistry: BeanRegistry

    @BeforeAll
    fun `setup bean registry`() {
        ContextScanner.processAnnotatedResources(
            ContextScanner.scanAnnotatedResources(BeanRegistry::class.packageName())
        )
        beanRegistry = Context.get(BeanRegistry.Key)!!
    }

    @Test
    fun `test new instance`() {
        beanRegistry.registerSingleton(BeanComponentTest())
        assertNotNull(BeanHelper.newInstance<BeanClass>(BeanClass::class.java))
    }
}