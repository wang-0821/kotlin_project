package com.xiao.demo.netty

import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.DefaultFileRegion
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.ssl.SslHandler
import io.netty.handler.stream.ChunkedFile
import java.io.RandomAccessFile

/**
 *
 * @author lix wang
 */
class FileServerHandler : SimpleChannelInboundHandler<String>() {
    override fun channelActive(ctx: ChannelHandlerContext) {
        sendFile(ctx, "JUnit5.md")
    }

    override fun messageReceived(ctx: ChannelHandlerContext, msg: String) {
        // do nothing
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        if (ctx.channel().isActive) {
            ctx.writeAndFlush("ERR: ${cause::class.java.simpleName}: ${cause.message}\n")
                .addListener(ChannelFutureListener.CLOSE)
        }
    }

    private fun sendFile(ctx: ChannelHandlerContext, fileName: String) {
        var raf: RandomAccessFile? = null
        var length: Long = -1
        try {
            raf = RandomAccessFile(fileName, "r")
            length = raf.length()
        } catch (e: Exception) {
            ctx.writeAndFlush("ERR: ${e::class.java.simpleName}: ${e.message}\n")
            return
        } finally {
            if (length < 0 && raf != null) {
                raf.close()
            }
        }

        if (ctx.pipeline().get(SslHandler::class.java) == null) {
            ctx.writeAndFlush(DefaultFileRegion(raf!!.channel, 0, length))
        } else {
            ctx.write(ChunkedFile(raf))
        }
    }
}