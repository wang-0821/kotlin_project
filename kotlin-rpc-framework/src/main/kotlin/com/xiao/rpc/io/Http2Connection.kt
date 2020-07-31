package com.xiao.rpc.io

import com.xiao.rpc.Route

/**
 *
 * @author lix wang
 */
class Http2Connection : Connection {
    override fun connect() {

    }

    override fun validateAndUse(): Boolean {
        return true
    }

    override fun route(): Route {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun writeHeaders(request: Request) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun writeBody(request: Request) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun write(message: ByteArray) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun finishRequest() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun response(): Response {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun close() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}