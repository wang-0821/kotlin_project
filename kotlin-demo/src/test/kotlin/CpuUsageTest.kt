import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

/**
 *
 * @author lix wang
 */
class CpuUsageTest {
    @Test
    fun `make cpu high usage`() {
        while (true) {
            // do nothing
        }
    }

    @Test
    fun `make memory high usage`() {
        val list = mutableListOf<List<Int>>()
        try {
            while (true) {
                list.add(MutableList(1000) { 0 })
            }
        } catch (e: Error) {
            while (true) {
                Thread.sleep(500)
            }
        }
    }
}