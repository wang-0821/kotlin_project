import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 *
 * @author lix wang
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FirstTest {
    @BeforeAll
    fun beforeAllTest() {
        assertEquals(4, 1 + 3)
    }

    @Test
    fun assertPlus() {
        assertEquals(2, 1 + 1)
    }

    @Test
    fun assertPlus2() {
        assertEquals(3, 1 + 2)
    }

    @BeforeEach
    fun beforeEachTest() {
        assertEquals(2, 1 + 1)
    }
}