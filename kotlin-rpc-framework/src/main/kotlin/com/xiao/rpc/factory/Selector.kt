package com.xiao.rpc.factory

/**
 *
 * @author lix wang
 */
@FunctionalInterface
interface Selector<T : Any> {
    fun select(): T
}