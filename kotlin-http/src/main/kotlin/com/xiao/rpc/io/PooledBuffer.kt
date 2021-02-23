package com.xiao.rpc.io

import com.xiao.rpc.helper.IoHelper
import com.xiao.rpc.helper.RpcContextKey
import com.xiao.rpc.helper.RpcHelper
import java.nio.CharBuffer

/**
 *
 * @author lix wang
 */
class PooledBuffer {
    private val buffers = mutableListOf<PooledCharArrayBuffer>()
    private var currentBuffer: PooledCharArrayBuffer
    private val pooledCharArrayBuffer = object : RpcContextKey<PooledCharArrayBuffer> {}
    private val maxCacheSize = 32

    constructor() {
        this.currentBuffer = PooledCharArrayBuffer(IoHelper.BUFFER_SIZE)
        buffers.add(currentBuffer)
    }

    fun appendCharBuffer(charBuffer: CharBuffer) {
        while (true) {
            val buffer = ensureCurrentBuffer()
            if (buffer.copy(charBuffer) <= 0) {
                return
            }
        }
    }

    fun asString(): String {
        val count = (buffers.size - 1) * IoHelper.BUFFER_SIZE + currentBuffer.index
        val charArray = CharArray(count)
        var pos = 0
        for (buffer in buffers) {
            System.arraycopy(buffer.charArray, 0, charArray, pos, buffer.index)
            pos += buffer.index
            cachePooledCharArrayBuffer(buffer)
        }
        return String(charArray)
    }

    private fun ensureCurrentBuffer(): PooledCharArrayBuffer {
        if (currentBuffer.remaining() <= 0) {
            currentBuffer = getPooledCharArrayBuffer()
            buffers.add(currentBuffer)
        }
        return currentBuffer
    }

    private class PooledCharArrayBuffer {
        var index = 0
        var capacity: Int
        var charArray: CharArray

        constructor(capacity: Int) {
            this.capacity = capacity
            this.charArray = CharArray(capacity)
        }

        fun copy(charBuffer: CharBuffer): Int {
            val currentRemaining = remaining()
            val charBufferRemaining = charBuffer.remaining()
            return if (charBufferRemaining <= currentRemaining) {
                System.arraycopy(charBuffer.array(), charBuffer.position(), charArray, index, charBufferRemaining)
                index += charBufferRemaining
                0
            } else {
                System.arraycopy(charBuffer.array(), charBuffer.position(), charArray, index, currentRemaining)
                charBuffer.position(charBuffer.position() + currentRemaining)
                index += currentRemaining
                charBuffer.remaining()
            }
        }

        fun remaining(): Int {
            return capacity - index
        }

        fun clear() {
            index = 0
        }
    }

    private fun cachePooledCharArrayBuffer(buffer: PooledCharArrayBuffer): Boolean {
        val pooledCharArrayBuffers = RpcHelper.fetch(pooledCharArrayBuffer) {
            mutableListOf<PooledCharArrayBuffer>()
        }
        return if (pooledCharArrayBuffers.size >= maxCacheSize) {
            false
        } else {
            buffer.clear()
            pooledCharArrayBuffers.add(buffer)
        }
    }

    private fun getPooledCharArrayBuffer(): PooledCharArrayBuffer {
        val pooledCharArrayBuffers = RpcHelper.fetch(pooledCharArrayBuffer) {
            mutableListOf<PooledCharArrayBuffer>()
        }
        return if (pooledCharArrayBuffers.isNotEmpty()) {
            pooledCharArrayBuffers.removeAt(0)
        } else {
            PooledCharArrayBuffer(IoHelper.BUFFER_SIZE)
        }
    }
}