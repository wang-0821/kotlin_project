package com.xiao.boot.env

/**
 *
 * @author lix wang
 */
class EnvInfoProviderImpl(
    private val ip: String,
    private val host: String,
    private val port: Int,
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