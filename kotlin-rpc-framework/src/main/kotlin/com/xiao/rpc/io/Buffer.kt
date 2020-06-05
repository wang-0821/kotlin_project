package com.xiao.rpc.io

/**
 *
 * @author lix wang
 */
class Buffer {
    constructor(bufferSize: Int = 0) {
        this.bufferSize = bufferSize
    }

    var bufferSize: Int
    private set(value) {
        field = if (value > 0 && field != value) {
            value
        } else {
            val maxMemory = Runtime.getRuntime().maxMemory()
            when {
                maxMemory < 64 * 1024 * 1024 ->  1024
                maxMemory < 128 * 1024 * 1024 -> 4 * 1024
                else -> 8 * 1024
            }
        }
    }
}