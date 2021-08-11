package xiao.base.io

import io.netty.buffer.PooledByteBufAllocator
import io.netty.util.ReferenceCountUtil

/**
 *
 * @author lix wang
 */
abstract class NettyDirectArray<T>(
    capacity: Int,
    elementSize: Int
) : DirectArray<T>(capacity, elementSize) {
    private val byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(checkCapacity())

    override fun close() {
        ReferenceCountUtil.release(byteBuf)
    }

    private fun checkCapacity(): Int {
        val totalCapacity = getOffset(capacity)
        if (totalCapacity > Int.MAX_VALUE) {
            throw IllegalArgumentException(
                "Out of bounds of ${Int.MAX_VALUE}, capacity: $capacity, elementSize: $elementBytes."
            )
        }
        return totalCapacity.toInt()
    }
}