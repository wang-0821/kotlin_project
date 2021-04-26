package com.xiao.beans.context

/**
 *
 * @author lix wang
 */
interface ContextAware : Context {
    override val key: Context.Key<*>
        get() = object : Context.Key<ContextAware> {}

    fun <E : Context> get(key: Context.Key<E>): E? {
        return Context.get(key)
    }
}