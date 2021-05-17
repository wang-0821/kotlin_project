package com.xiao.beans

import com.xiao.base.testing.KtTestBase
import com.xiao.base.util.packageName
import com.xiao.beans.context.BeanHelper
import com.xiao.beans.context.BeanRegistry
import com.xiao.beans.context.Context
import com.xiao.beans.context.ContextScanner
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

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