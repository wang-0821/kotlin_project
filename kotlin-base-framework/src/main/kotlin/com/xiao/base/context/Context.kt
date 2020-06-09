package com.xiao.base.context

import com.xiao.base.exception.KtException
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 *
 * @author lix wang
 */
interface Context {
    interface Key<E : Context>

    val key: Key<*>

    @Throws(KtException::class)
    fun <E : Context> register(key: Key<E>) {
        lock.withLock {
            if (get(key) == null) {
                container[key] = this
            } else {
                throw KtException().message("Context register key duplicate $key")
            }
        }
    }

    companion object ContextContainer {
        private var container = mutableMapOf<Key<*>, Context>()
        private val lock = ReentrantLock()

        internal fun <E : Context> get(key: Key<E>): E? {
            val context = container[key]
            return if (context != null) {
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