package com.xiao.demo.netty.discard

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.util.concurrent.Future

/**
 *
 * @author lix wang
 */
class DiscardClientHandler : SimpleChannelInboundHandler<Any>() {
    private var channelHandlerContext: ChannelHandlerContext? = null
    private var byteBuf: ByteBuf? = null
    private var times = 0

    override fun channelActive(ctx: ChannelHandlerContext) {
        this.channelHandlerContext = ctx
        byteBuf = ctx.alloc().directBuffer(BUFFER_SIZE).writeZero(BUFFER_SIZE)
        byteBuf!!.writeBytes("Hello world!".toByteArray())
        generateTraffic()
    }

    override fun channelInactive(ctx: ChannelHandlerContext?) {
        byteBuf!!.release()
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        ctx.close()
    }

    private fun generateTraffic() {
        if (times < MAX_TIMES) {
            channelHandlerContext!!.writeAndFlush(byteBuf!!.retain()).addListener(trafficGenerator)
        }
    }

    private val trafficGenerator: ((Future<*>) -> Unit) = { future ->
        if (future.isSuccess) {
            generateTraffic()
        } else {
            future.cause().printStackTrace()
            future.cancel(true)
        }
    }
    companion object {
        private const val BUFFER_SIZE = 512
        private const val MAX_TIMES = 20
    }

    override fun channelRead0(ctx: ChannelHandlerContext?, msg: Any?) {
        TODO("Not yet implemented")
    }
}