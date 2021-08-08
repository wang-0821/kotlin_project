package util

import io.netty.buffer.PooledByteBufAllocator
import io.netty.util.ReferenceCountUtil
import org.junit.jupiter.api.Test

/**
 * @author lix wang
 */
class NettyBufferTest {
    @Test
    fun `test netty buffer`() {
        val byteBuf = PooledByteBufAllocator.DEFAULT.buffer(8196)
        byteBuf.writeBytes("hello".toByteArray())
        println(byteBuf.array().toString())
        byteBuf.clear()
        ReferenceCountUtil.release(byteBuf)
    }
}