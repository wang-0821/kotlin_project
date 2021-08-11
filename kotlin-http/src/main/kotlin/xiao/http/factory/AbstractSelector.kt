package xiao.http.factory

import xiao.beans.context.BeanRegistryAware
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 *
 * @author lix wang
 */
abstract class AbstractSelector<T : Any> : Selector<T>, BeanRegistryAware {
    override fun select(): T {
        return getByType(getTypeReference()) ?: selectDefault()
    }

    protected abstract fun selectDefault(): T

    @Suppress("UNCHECKED_CAST")
    private fun getTypeReference(): Class<T> {
        var parameterizedType: Type? = null
        var type = this::class.java.genericSuperclass
        if (type is ParameterizedType) {
            parameterizedType = type.actualTypeArguments?.get(0)
        }
        if (parameterizedType is Class<*>) {
            return parameterizedType as Class<T>
        } else {
            throw IllegalArgumentException("AbstractSelector parameterized type must be Class.")
        }
    }
}