package com.xiao.base.context

/**
 *
 * @author lix wang
 */
interface GenericContextAware  {
    fun <E : Context> get(key: Context.Key<E>): E? {
        return Context.get(key)
    }
}