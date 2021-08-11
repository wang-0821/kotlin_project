package xiao.base.util

import io.netty.channel.Channel
import io.netty.channel.EventLoopGroup
import io.netty.channel.ServerChannel
import io.netty.channel.epoll.Epoll
import io.netty.channel.epoll.EpollEventLoopGroup
import io.netty.channel.epoll.EpollServerSocketChannel
import io.netty.channel.epoll.EpollSocketChannel
import io.netty.channel.kqueue.KQueue
import io.netty.channel.kqueue.KQueueEventLoopGroup
import io.netty.channel.kqueue.KQueueServerSocketChannel
import io.netty.channel.kqueue.KQueueSocketChannel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.util.internal.PlatformDependent
import xiao.base.thread.NamedThreadFactory

/**
 *
 * @author lix wang
 */
object NettyUtils {
    @JvmStatic
    fun getIoEventLoopGroup(ioThreads: Int): EventLoopGroup {
        var group: EventLoopGroup? = null
        if (PlatformDependent.isOsx()) {
            if (KQueue.isAvailable()) {
                group = KQueueEventLoopGroup(
                    ioThreads,
                    NamedThreadFactory("netty-kqueue-xiao.base.thread")
                )
            }
        } else {
            if (!PlatformDependent.isWindows()) {
                if (Epoll.isAvailable()) {
                    group = EpollEventLoopGroup(
                        ioThreads,
                        NamedThreadFactory("netty-epoll-xiao.base.thread")
                    )
                }
            }
        }
        return group ?: NioEventLoopGroup(
            ioThreads,
            NamedThreadFactory("netty-nio-xiao.base.thread")
        )
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T : ServerChannel> getServerSocketChannel(): Class<T> {
        var channel: Class<T>? = null
        if (PlatformDependent.isOsx()) {
            if (KQueue.isAvailable()) {
                channel = KQueueServerSocketChannel::class.java as Class<T>
            }
        } else {
            if (!PlatformDependent.isWindows()) {
                if (Epoll.isAvailable()) {
                    channel = EpollServerSocketChannel::class.java as Class<T>
                }
            }
        }
        return channel ?: NioServerSocketChannel::class.java as Class<T>
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T : Channel> getSocketChannel(): Class<T> {
        var channel: Class<T>? = null
        if (PlatformDependent.isOsx()) {
            if (KQueue.isAvailable()) {
                channel = KQueueSocketChannel::class.java as Class<T>
            }
        } else {
            if (!PlatformDependent.isWindows()) {
                if (Epoll.isAvailable()) {
                    channel = EpollSocketChannel::class.java as Class<T>
                }
            }
        }
        return channel ?: NioSocketChannel::class.java as Class<T>
    }
}