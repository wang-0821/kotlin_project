package xiao.base.io

/**
 *
 * @author lix wang
 */
interface UnsafeDirectArrayAllocator<T : UnsafeDirectArray<*>> {
    fun allocate(capacity: Int): T
}