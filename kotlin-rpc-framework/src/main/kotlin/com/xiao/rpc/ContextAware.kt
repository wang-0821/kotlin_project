package com.xiao.rpc

/**
 *
 * @author lix wang
 */
interface ContextAware : Context {
    override val key: Context.Key<*>
        get() = EmptyContext.Key

    fun <E : Context> get(key: Context.Key<E>): E? {
        return Context.get(key)
    }

    class EmptyContext : Context {
        override val key: Context.Key<*>
            get() = Key

        companion object Key : Context.Key<EmptyContext>
    }
}