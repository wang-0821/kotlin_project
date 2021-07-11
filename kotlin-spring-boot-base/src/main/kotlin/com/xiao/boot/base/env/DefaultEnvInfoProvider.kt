package com.xiao.boot.base.env

/**
 *
 * @author lix wang
 */
internal class DefaultEnvInfoProvider(
    private val ip: String,
    private val host: String,
    var port: Int,
    private val profile: ProfileType
) : EnvInfoProvider {
    override fun ip(): String {
        return ip
    }

    override fun host(): String {
        return host
    }

    override fun port(): Int {
        return port
    }

    override fun profile(): ProfileType {
        return profile
    }
}