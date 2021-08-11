import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import xiao.base.model.CustomClassLoader
import xiao.base.testing.KtTestBase

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
        val ktClass1 = Class.forName(CLASSNAME, true, classLoader)
        Assertions.assertSame(ktClass1, ktClass2)
    }

    @Test
    fun `test use custom classLoader breaking parents delegation`() {
        val customClassLoader = CustomClassLoader()
        val ktClass1 = customClassLoader.loadClass(CLASSNAME)
        val ktClass2 = Class.forName(CLASSNAME, true, customClassLoader)
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
        private const val CLASSNAME = "xiao.base.model.ClassTarget"
    }
}