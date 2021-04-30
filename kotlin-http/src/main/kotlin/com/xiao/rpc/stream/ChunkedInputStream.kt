package com.xiao.rpc.stream

import com.xiao.base.util.IoUtils
import java.io.InputStream
import java.nio.ByteBuffer

/**
 *
 * @author lix wang
 */
class ChunkedInputStream : InputStream {
    private val inputStream: InputStream
    private val byteBuffer: ByteBuffer = ByteBuffer.allocate(IoUtils.BUFFER_SIZE)
    private var pos: Int = 0
    private var chunkSize: Int = 0
    private var eof = false
    private var closed = false
    private var state = ChunkState.CHUNK_LENGTH

    private enum class ChunkState {
        CHUNK_LENGTH, CHUNK_LINE_FEED
    }

    constructor(inputStream: InputStream) {
        this.inputStream = inputStream
        byteBuffer.flip()
    }

    override fun read(): Int {
        if (available() <= 0) {
            return -1
        }
        val byte = byteBuffer.get()
        pos ++
        if (pos >= chunkSize) {
            state = ChunkState.CHUNK_LINE_FEED
        }
        return byte.toInt() and 0xff
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        if (off + len > b.size) {
            throw IndexOutOfBoundsException("Read index out of dest array bounds.")
        }
        if (available() <= 0) {
            return -1
        }
        val length = len.coerceAtMost(byteBuffer.remaining())
        System.arraycopy(byteBuffer.array(), byteBuffer.position(), b, off, length)
        byteBuffer.position(byteBuffer.position() + length)
        pos += length
        if (pos >= chunkSize) {
            state = ChunkState.CHUNK_LINE_FEED
        }
        return length
    }

    override fun close() {
        closed = true
    }

    override fun available(): Int {
        if (closed || eof) {
            return -1
        }

        if (chunkBufferAvailable() <= 0) {
            reFill()
        }
        return chunkBufferAvailable()
    }

    private fun chunkBufferAvailable(): Int {
        return (chunkSize - pos).coerceAtMost(byteBuffer.remaining())
    }

    private fun reFill() {
        // double check
        check(byteBuffer.remaining() <= 0) {
            "Stream buffer can't refill while not empty."
        }
        if (chunkSize - pos <= 0) {
            nextChunk()
        }

        // eof
        if (eof) {
            return
        }

        byteBuffer.clear()
        val length = chunkBufferAvailable()
        val read = try {
            inputStream.read(byteBuffer.array(), 0, length)
        } catch (e: Exception) {
            throw e
        }
        byteBuffer.position(read)
        byteBuffer.flip()
    }

    private fun nextChunk() {
        if (ChunkState.CHUNK_LINE_FEED == state) {
            // skip line feed
            val chunkSizeStr = IoUtils.readPlainTextLine(inputStream)
            check(chunkSizeStr.isEmpty()) {
                "Unexpected content at end of chunk."
            }
            state = ChunkState.CHUNK_LENGTH
        }

        // calculate chunk size
        if (ChunkState.CHUNK_LENGTH == state) {
            val chunkSizeStr = IoUtils.readPlainTextLine(inputStream)
            chunkSize = Integer.parseInt(chunkSizeStr, 16)
            pos = 0
            if (chunkSize <= 0) {
                eof = true
            }
        }
    }
}