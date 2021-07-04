package com.xiao.boot.base.testing

import org.springframework.test.context.TestContext
import org.springframework.test.context.TestExecutionListener

class TestSpringExecutionListener : TestExecutionListener {
    override fun beforeTestClass(testContext: TestContext) {
        TestSpringContextUtils.setTestContext(testContext.testClass, testContext)
    }

    override fun afterTestClass(testContext: TestContext) {
        TestSpringContextUtils.removeTestContext(testContext.testClass)
    }
}