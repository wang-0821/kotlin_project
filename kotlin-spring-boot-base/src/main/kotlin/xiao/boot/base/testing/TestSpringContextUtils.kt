package xiao.boot.base.testing

import org.springframework.test.context.TestContext

object TestSpringContextUtils {
    private val testContextMap = HashMap<Class<*>, TestContext>()

    internal fun setTestContext(clazz: Class<*>, testContext: TestContext) {
        testContextMap[clazz] = testContext
    }

    internal fun removeTestContext(clazz: Class<*>) {
        testContextMap.remove(clazz)
    }

    fun getTestContext(clazz: Class<*>): TestContext {
        return testContextMap[clazz] ?: throw IllegalStateException("Can't find testContext for ${clazz.name}.")
    }
}