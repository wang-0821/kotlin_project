package xiao.base.io

/**
 * @author lix wang
 */
abstract class DirectArray<T>(
    val capacity: Int,
    val elementBytes: Int
) : AutoCloseable {
    abstract fun get(index: Int): T

    abstract fun set(index: Int, value: T)

    protected fun getOffset(index: Int): Long {
        return index.toLong() * elementBytes
    }
}