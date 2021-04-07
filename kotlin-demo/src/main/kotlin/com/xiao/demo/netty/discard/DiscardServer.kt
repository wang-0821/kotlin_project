package com.xiao.demo.netty.discard

import com.xiao.base.util.NettyUtils
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.socket.SocketChannel

/**
 *
 * @author lix wang
 */
class DiscardServer {
    fun run() {
        val bossGroup = NettyUtils.getIoEventLoopGroup(1)
        val workerGroup = NettyUtils.getIoEventLoopGroup(Runtime.getRuntime().availableProcessors())
        try {
            val serverBootstrap = ServerBootstrap()
            serverBootstrap
                .group(bossGroup, workerGroup)
                .channel(NettyUtils.getServerSocketChannel())
                .childHandler(
                    object : ChannelInitializer<SocketChannel>() {
                        override fun initChannel(ch: SocketChannel) {
                            ch.pipeline().addLast(DiscardServerHandler())
                        }
                    }
                )
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)

            val channelFuture = serverBootstrap.bind(PORT).sync()
            channelFuture.channel().closeFuture().sync()
        } finally {
            workerGroup.shutdownGracefully()
            bossGroup.shutdownGracefully()
        }
    }

    companion object {
        const val PORT = 8886
        const val HOST = "localhost"
    }
}

fun main() {
    DiscardServer().run()
}