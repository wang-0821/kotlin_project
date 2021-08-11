package xiao.demo.netty

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.LineBasedFrameDecoder
import io.netty.handler.codec.string.StringDecoder
import io.netty.handler.codec.string.StringEncoder
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import io.netty.handler.ssl.SslContext
import io.netty.handler.ssl.util.SelfSignedCertificate
import io.netty.handler.stream.ChunkedWriteHandler
import io.netty.util.CharsetUtil

/**
 *
 * @author lix wang
 */
class FileServer {
    fun run(ssl: Boolean, port: Int) {
        val sslContext = if (ssl) {
            val selfSignedCertificate = SelfSignedCertificate()
            SslContext.newServerContext(selfSignedCertificate.certificate(), selfSignedCertificate.privateKey())
        } else {
            null
        }

        val bossGroup = NioEventLoopGroup(1)
        val workerGroup = NioEventLoopGroup()
        try {
            val serverBootstrap = ServerBootstrap()
            serverBootstrap
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel::class.java)
                .option(ChannelOption.SO_BACKLOG, 100)
                .handler(LoggingHandler(LogLevel.INFO))
                .childHandler(
                    object : ChannelInitializer<SocketChannel>() {
                        override fun initChannel(ch: SocketChannel) {
                            val pipeline = ch.pipeline()
                            if (sslContext != null) {
                                pipeline.addLast(sslContext.newHandler(ch.alloc()))
                            }
                            pipeline
                                .addLast(
                                    StringEncoder(CharsetUtil.UTF_8),
                                    LineBasedFrameDecoder(8192),
                                    StringDecoder(CharsetUtil.UTF_8),
                                    ChunkedWriteHandler(),
                                    FileServerHandler()
                                )
                        }
                    }
                )
            serverBootstrap.bind(port).sync().channel().closeFuture().sync()
        } finally {
            bossGroup.shutdownGracefully()
            workerGroup.shutdownGracefully()
        }
    }
}

fun main() {
    FileServer().run(false, 8082)
}