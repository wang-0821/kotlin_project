package xiao.boot.base.env

/**
 *
 * @author lix wang
 */
interface EnvInfoProvider {
    fun ip(): String
    fun host(): String
    fun port(): Int
    fun profile(): ProfileType
    fun serverName(): String
}