package xiao.boot.base.env

/**
 *
 * @author lix wang
 */
internal class DefaultEnvInfoProvider(
    private val ip: String,
    private val host: String,
    var port: Int,
    private val profile: xiao.boot.base.env.ProfileType,
    private var serverName: String
) : xiao.boot.base.env.EnvInfoProvider {
    override fun ip(): String {
        return ip
    }

    override fun host(): String {
        return host
    }

    override fun port(): Int {
        return port
    }

    override fun profile(): xiao.boot.base.env.ProfileType {
        return profile
    }

    override fun serverName(): String {
        return serverName
    }
}