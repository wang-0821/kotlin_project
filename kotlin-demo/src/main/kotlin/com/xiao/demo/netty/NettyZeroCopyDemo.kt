package com.xiao.demo.netty

import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel

/**
 *
 * @author lix wang
 */
object NettyZeroCopyDemo {
    fun downloadFile(filePath: String) {
        val eventLoopGroup = NioEventLoopGroup()
        try {
            val bootStrap = Bootstrap()
            bootStrap
                .group(eventLoopGroup)
                .channel(NioSocketChannel::class.java)
                .handler(
                    object : ChannelInitializer<NioSocketChannel>() {
                        override fun initChannel(ch: NioSocketChannel) {
                            ch.pipeline()
                                .apply {
                                    addLast(FileServerHandler())
                                }
                        }
                    }
                )
            bootStrap.connect(HOST, PORT).sync().channel().closeFuture().sync()
        } finally {
            eventLoopGroup.shutdownGracefully()
        }
    }

    private const val HOST = "https://www.baidu.com"
    private const val PORT = 80
}

fun main() {
    NettyZeroCopyDemo.downloadFile("")
}