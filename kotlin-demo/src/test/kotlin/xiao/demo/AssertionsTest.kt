package xiao.demo

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.assertTimeout
import org.junit.jupiter.api.assertTimeoutPreemptively
import java.time.Duration

/**
 *
 * @author lix wang
 */
class AssertionsTest {
    @Test
    fun `exception absence testing`() {
        val result = assertDoesNotThrow { 0.div(1) }
        assertEquals(0, result)
    }

    @Test
    fun `expected exception testing`() {
        val exception = assertThrows<ArithmeticException> {
            1.div(0)
        }
        assertEquals("/ by zero", exception.message)
    }

    @Test
    fun `grouped assertions`() {
        assertAll(
            "assert all",
            { assertEquals(1, 1) },
            { assertEquals(2, 1 + 1) }
        )
    }

    @Test
    fun `timeout not exceeded testing`() {
        assertTimeout(
            Duration.ofMillis(1000)
        ) { assertEquals(2, 1 + 1) }
    }

    @Disabled
    @Test
    fun `timeout exceeded with preemptive termination`() {
        assertTimeoutPreemptively(Duration.ofMillis(10)) {
            Thread.sleep(100)
        }
    }
}