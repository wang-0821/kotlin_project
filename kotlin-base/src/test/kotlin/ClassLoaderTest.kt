import com.xiao.model.ClassTarget
import com.xiao.model.CustomClassLoader
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 *
 * @author lix wang
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClassLoaderTest {
    private lateinit var classLoader: ClassLoader

    @BeforeAll
    fun init() {
        classLoader = Thread.currentThread().contextClassLoader ?: ClassLoader.getSystemClassLoader()
    }

    @Test
    fun `test use classLoader loadClass and class forName`() {
        val ktClass2 = classLoader.loadClass(CLASSNAME)
        println("ClassLoader loadClass done.")
        val ktClass1 = Class.forName(CLASSNAME, true, classLoader)
        println("Class forName done. ${ClassTarget.val1}")
        Assertions.assertSame(ktClass1, ktClass2)
    }

    @Test
    fun `test use custom classLoader breaking parents delegation`() {
        val customClassLoader = CustomClassLoader()
        val ktClass1 = customClassLoader.loadClass(CLASSNAME)
        println("ClassLoader loadClass done.")
        val ktClass2 = Class.forName(CLASSNAME, true, customClassLoader)
        println("Class forName done. ${ClassTarget.val1}")
        Assertions.assertSame(ktClass1, ktClass2)
    }

    @Test
    fun `test load same class with different classLoaders`() {
        val customClassLoader = CustomClassLoader()
        val ktClass1 = customClassLoader.loadClass(CLASSNAME)
        val ktClass2 = classLoader.loadClass(CLASSNAME)
        Assertions.assertSame(ktClass1.classLoader, customClassLoader)
        Assertions.assertNotSame(ktClass1, ktClass2)
    }

    companion object {
        private const val CLASSNAME = "com.xiao.model.ClassTarget"
    }
}