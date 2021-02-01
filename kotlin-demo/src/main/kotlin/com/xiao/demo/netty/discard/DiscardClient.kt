package com.xiao.demo.netty.discard

import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel

/**
 *
 * @author lix wang
 */
class DiscardClient {
    fun run() {
        val group = NioEventLoopGroup()
        try {
            val bootstrap = Bootstrap()
            bootstrap
                .group(group)
                .channel(NioSocketChannel::class.java)
                .handler(
                    object : ChannelInitializer<SocketChannel>() {
                        override fun initChannel(ch: SocketChannel) {
                            ch.pipeline().addLast(DiscardClientHandler())
                        }
                    }
                )

            val channelFuture = bootstrap.connect(DiscardServer.HOST, DiscardServer.PORT).sync()
            channelFuture.channel().closeFuture().sync()
        } finally {
            group.shutdownGracefully()
        }
    }
}

fun main() {
    DiscardClient().run()
}