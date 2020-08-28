package com.xiao.base.context

import java.util.concurrent.ConcurrentHashMap

/**
 *
 * @author lix wang
 */
interface Context {
    interface Key<E : Context>

    val key: Key<*>

    fun <E : Context> register(key: Key<E>) {
        synchronized(container) {
            if (get(key) == null) {
                container[key] = this
            } else {
                throw IllegalStateException("Context register key duplicate $key.")
            }
        }
    }

    companion object ContextContainer {
        private var container = ConcurrentHashMap<Key<*>, Context>()

        internal fun <E : Context> get(key: Key<E>): E? {
            val context = container[key]
            return if (context != null) {
                @Suppress("UNCHECKED_CAST")
                context as E
            } else {
                null
            }
        }
    }
}

abstract class AbstractContext(override val key: Context.Key<*>) : Context {
    init {
        register(key)
    }
}