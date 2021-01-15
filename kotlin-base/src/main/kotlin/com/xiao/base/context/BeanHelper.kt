package com.xiao.base.context

/**
 *
 * @author lix wang
 */
object BeanHelper : BeanRegistryAware {
    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <E> newInstance(clazz: Class<*>): E {
        val constructors = clazz.constructors
        check(constructors.size == 1) {
            "${clazz.simpleName} should have only one constructor."
        }
        val constructor = constructors[0]
        val parameterTypes = constructor.parameterTypes
        val parameters = arrayOfNulls<Any>(parameterTypes.size)
        for (i in parameterTypes.indices) {
            parameters[i] = getByType(parameterTypes[i])
            check(parameters[i] != null) {
                "${clazz.simpleName} constructor parameterType ${parameterTypes[i].simpleName} can't find value."
            }
        }
        return constructor.newInstance(*parameters) as E
    }
}