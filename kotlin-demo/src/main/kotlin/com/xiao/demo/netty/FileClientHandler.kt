package com.xiao.demo.netty

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

/**
 *
 * @author lix wang
 */
class FileClientHandler : SimpleChannelInboundHandler<Any>() {
    override fun messageReceived(ctx: ChannelHandlerContext, msg: Any) {
        println(ctx.toString() + msg.toString())
    }
}