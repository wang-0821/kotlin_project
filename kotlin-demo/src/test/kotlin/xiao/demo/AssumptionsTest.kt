package xiao.demo

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.Assumptions.assumingThat
import org.junit.jupiter.api.Test

/**
 *
 * @author lix wang
 */
class AssumptionsTest {
    @Test
    fun `test only ci server`() {
        assumeTrue("CI" == System.getenv("ENV"))
    }

    @Test
    fun `test only on developer work station`() {
        assumeTrue("DEV" == System.getenv("ENV")) { "Aborting test: not on developer work station" }
    }

    @Test
    fun `test in all environments`() {
        assumingThat("CI" == System.getenv("ENV")) {
            assertEquals(2, 4.div(2))
        }
        assertEquals(42, 6 * 7)
    }
}