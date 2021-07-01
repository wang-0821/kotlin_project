package com.xiao.boot.base

import com.xiao.base.testing.KtTestBase
import com.xiao.boot.base.autoconfiguration.KtSpringBootBaseAutoConfiguration
import com.xiao.boot.base.properties.DemoEnvProperties
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

/**
 *
 * @author lix wang
 */
@SpringBootTest(classes = [KtSpringBootBaseAutoConfiguration::class])
class EnvPropertyTest : KtTestBase() {
    @Autowired
    lateinit var properties: DemoEnvProperties

    @Test
    fun `test env properties`() {
        Assertions.assertEquals(properties.booleanValue, true)
        Assertions.assertEquals(properties.byteValue, "1".toByte())
        Assertions.assertEquals(properties.doubleValue, 1.2)
        Assertions.assertEquals(properties.encryptedValue, "Hello world!")
        Assertions.assertEquals(properties.floatValue, 1.1f)
        Assertions.assertEquals(properties.intValue, 1)
        Assertions.assertEquals(properties.longValue, 1L)
        Assertions.assertEquals(properties.nullableValue, null)
        Assertions.assertEquals(properties.shortValue, 1)
        Assertions.assertEquals(properties.stringValue, "1")
        Assertions.assertEquals(properties.listValue.size, 3)
        Assertions.assertEquals(properties.objValue.var1, "abc")
        Assertions.assertEquals(properties.mapValue["var1"], 1)
    }
}