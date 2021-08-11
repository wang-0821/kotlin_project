package xiao.boot.mybatis.factory

import xiao.boot.mybatis.invocation.RetryKtInvocation
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 *
 * @author lix wang
 */
class KtSpringMapperProxy(
    sourceClass: Class<*>,
    private val mapper: Any
) : InvocationHandler {
    private val retryKtInvocation = RetryKtInvocation(sourceClass)

    override fun invoke(proxy: Any?, method: Method, args: Array<Any?>?): Any? {
        return retryKtInvocation.invoke(mapper, method, args)
    }
}