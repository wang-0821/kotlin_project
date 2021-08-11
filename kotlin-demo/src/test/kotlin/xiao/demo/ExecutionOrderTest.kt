package xiao.demo

import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder

/**
 *
 * @author lix wang
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ExecutionOrderTest {
    @Test
    @Order(1)
    fun `null values`() {
    }

    @Test
    @Order(2)
    fun `empty value`() {
    }
}