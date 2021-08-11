package xiao.demo.netty.discard

import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import xiao.base.util.NettyUtils

/**
 *
 * @author lix wang
 */
class DiscardClient {
    fun run() {
        val group = NettyUtils.getIoEventLoopGroup(Runtime.getRuntime().availableProcessors())
        try {
            val bootstrap = Bootstrap()
            bootstrap
                .group(group)
                .channel(NettyUtils.getSocketChannel())
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