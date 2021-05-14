import com.xiao.base.testing.KtTestBase
import com.xiao.model.ClassTarget
import com.xiao.model.CustomClassLoader
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/**
 *
 * @author lix wang
 */
class ClassLoaderTest : KtTestBase() {
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

    @Test
    fun `test load same class with same custom classLoader`() {
        val customClassLoader = CustomClassLoader()
        val ktClass1 = customClassLoader.loadClass(CLASSNAME)
        val ktClass2 = customClassLoader.loadClass(CLASSNAME)
        Assertions.assertSame(ktClass1, ktClass2)
        Assertions.assertSame(ktClass1.classLoader, customClassLoader)
    }

    companion object {
        private const val CLASSNAME = "com.xiao.model.ClassTarget"
    }
}