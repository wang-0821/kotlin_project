package xiao.boot.base.testing

import org.springframework.test.context.TestContext
import org.springframework.test.context.TestExecutionListener

class TestSpringExecutionListener : TestExecutionListener {
    override fun prepareTestInstance(testContext: TestContext) {
        TestSpringContextUtils.setTestContext(testContext.testClass, testContext)
    }
}