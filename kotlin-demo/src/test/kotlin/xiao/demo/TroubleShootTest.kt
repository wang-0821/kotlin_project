package xiao.demo

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.util.ArrayList

/**
 *
 * @author lix wang
 */
@Disabled
class TroubleShootTest {
    @Test
    fun `make cpu high usage`() {
        while (true) {
            // do nothing
        }
    }

    @Test
    fun `make memory high usage`() {
        val lists: MutableList<List<Int>> = ArrayList()
        try {
            while (true) {
                lists.add(ArrayList(1024))
            }
        } catch (error: Error) {
            while (true) {
                lists.size
                Thread.sleep(300)
            }
        }
    }
}