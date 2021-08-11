package xiao.demo

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIf
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS

/**
 *
 * @author lix wang
 */
class ConditionalTest {
    @Test
    @EnabledOnOs(OS.MAC)
    fun `only test on mac`() {
        assertEquals(1, 1)
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    fun `only test on windows`() {
        assertEquals(1, 1)
    }

    @Test
    @EnabledIf("customCondition")
    fun `enable if custom condition`() {
        assertEquals(2, 2)
    }

    fun customCondition(): Boolean {
        return true
    }
}