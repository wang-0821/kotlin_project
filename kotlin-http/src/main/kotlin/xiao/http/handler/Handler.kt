package xiao.http.handler

import xiao.http.io.Response

/**
 *
 * @author lix wang
 */
interface Handler {
    fun handle(): Response

    val chain: Chain
}