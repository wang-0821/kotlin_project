package com.xiao.base.demo.coroutines

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 *
 * @author lix wang
 */
class ChannelDemo {
    fun foo() {
        val channel = Channel<Int>()
        runBlocking {
            launch {
                for (i in 1..5) {
                    channel.send(i * i)
                }
                channel.close()
            }
            repeat(5) {
                println(channel.receive())
            }
        }
        println("Done!")
    }
}

fun main() {
    val obj = ChannelDemo()
    obj.foo()
}
