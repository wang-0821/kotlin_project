import com.xiao.base.testing.KtTestBase
import com.xiao.model.ClassTarget
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicInteger

/**
 *
 * @author lix wang
 */
class ReferenceTest : KtTestBase() {
    @Test
    fun `test weak reference object clean up by gc`() {
        val classTargetReference = WeakReference(ClassTarget())
        System.gc()
        Assertions.assertEquals(classTargetReference.get(), null)
    }

    @Test
    fun `test strong reference and weak reference wrapped object`() {
        val atomicInt = AtomicInteger(1)
        val weakReferenceInt = WeakReference(atomicInt)
        Assertions.assertEquals(weakReferenceInt.get()!!.get(), 1)
        System.gc()
        Assertions.assertEquals(weakReferenceInt.get()!!.get(), 1)
    }
}