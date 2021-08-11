package xiao.beans.context

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
        parameterTypes.indices
            .forEach {
                parameters[it] = getByType(parameterTypes[it])
                check(parameters[it] != null) {
                    "${clazz.simpleName} constructor parameterType ${parameterTypes[it].simpleName} can't find value."
                }
            }
        return constructor.newInstance(*parameters) as E
    }
}