package com.xiao.base.context

/**
 *
 * @author lix wang
 */
interface ContextAware : Context {
    override val key: Context.Key<*>
        get() = Key

    fun <E : Context> get(key: Context.Key<E>): E? {
        return Context.get(key)
    }

    private companion object Key : Context.Key<ContextAware>
}