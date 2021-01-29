package com.xiao.demo.netty.discard

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerAdapter
import io.netty.channel.ChannelHandlerContext
import io.netty.util.ReferenceCountUtil

/**
 *
 * @author lix wang
 */
class DiscardServerHandler : ChannelHandlerAdapter() {
    override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
        val byteBuf = msg as? ByteBuf
        try {
            byteBuf?.let {
                val byteArray = ByteArray(it.readableBytes())
                it.readBytes(byteArray)
                println("Discard server read: ${String(byteArray)}")
            }
        } finally {
            ReferenceCountUtil.release(msg)
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        ctx.close()
    }
}