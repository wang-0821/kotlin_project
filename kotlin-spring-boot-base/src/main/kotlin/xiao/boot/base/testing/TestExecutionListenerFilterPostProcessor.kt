package xiao.boot.base.testing

import org.springframework.boot.test.context.DefaultTestExecutionListenersPostProcessor
import org.springframework.test.context.TestExecutionListener

/**
 *
 * @author lix wang
 */
class TestExecutionListenerFilterPostProcessor : DefaultTestExecutionListenersPostProcessor {
    override fun postProcessDefaultTestExecutionListeners(
        listeners: MutableSet<Class<out TestExecutionListener>>
    ): MutableSet<Class<out TestExecutionListener>> {
        return listeners
            .filter {
                !excludeListeners.contains(it.name)
            }.toMutableSet()
    }

    companion object {
        private val excludeListeners = listOf(
            "org.springframework.boot.test.autoconfigure.restdocs.RestDocsTestExecutionListener",
            "org.springframework.boot.test.autoconfigure.web.client.MockRestServiceServerResetTestExecutionListener",
            "org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrintOnlyOnFailureTestExecutionListener",
            "org.springframework.boot.test.autoconfigure.web.servlet.WebDriverTestExecutionListener",
            "org.springframework.boot.test.autoconfigure.webservices.client.MockWebServiceServerTestExecutionListener",
            "org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener",
            "org.springframework.boot.test.mock.mockito.ResetMocksTestExecutionListener",
            "org.springframework.test.context.web.ServletTestExecutionListener",
            "org.springframework.test.context.support.DirtiesContextBeforeModesTestExecutionListener",
            "org.springframework.test.context.support.DirtiesContextTestExecutionListener",
            "org.springframework.test.context.transaction.TransactionalTestExecutionListener",
            "org.springframework.test.context.jdbc.SqlScriptsTestExecutionListener"
        )
    }
}