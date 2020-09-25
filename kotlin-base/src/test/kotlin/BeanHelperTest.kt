import com.xiao.base.context.BeanHelper
import com.xiao.base.context.BeanRegistry
import com.xiao.base.context.Context
import com.xiao.base.context.ContextScanner
import com.xiao.base.util.packageName
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 *
 * @author lix wang
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BeanHelperTest {
    class BeanComponentTest()
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