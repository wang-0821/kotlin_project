package xiao.http.factory

/**
 *
 * @author lix wang
 */
@FunctionalInterface
interface Selector<T : Any> {
    fun select(): T
}