package xiao.demo.netty

import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.ssl.SslContext
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import java.net.URI

/**
 *
 * @author lix wang
 */
class FileClient {
    fun run(url: String) {
        val uri = URI(url)
        val schema = uri.scheme
        val host = uri.host
        var port = uri.port
        if (port == -1) {
            port = if ("http" == schema) {
                80
            } else {
                443
            }
        }

        val sslContext = if ("https" == schema) {
            SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE)
        } else {
            null
        }

        val bootStrap = Bootstrap()
        val nioEventLoopGroup = NioEventLoopGroup()
        try {
            bootStrap
                .group(nioEventLoopGroup)
                .channel(NioSocketChannel::class.java)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(
                    object : ChannelInitializer <NioSocketChannel>() {
                        override fun initChannel(ch: NioSocketChannel) {
                            val pipeline = ch.pipeline()
                            if (sslContext != null) {
                                pipeline.addLast(sslContext.newHandler(ch.alloc()))
                            }
                            pipeline.addLast(FileClientHandler())
                        }
                    }
                )
            bootStrap.connect(host, port).sync().channel().closeFuture().sync()
        } finally {
            nioEventLoopGroup.shutdownGracefully()
        }
    }
}

fun main() {
    FileClient().run("http://127.0.0.1:8082")
}