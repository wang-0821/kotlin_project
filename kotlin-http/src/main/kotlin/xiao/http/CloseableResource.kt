package xiao.http

/**
 *
 * @author lix wang
 */
interface CloseableResource {
    fun tryClose(keepAliveMills: Long): Boolean

    fun tryUse(): Boolean

    fun unUse(): Boolean
}