package com.xiao.boot.env

/**
 *
 * @author lix wang
 */
interface EnvInfoProvider {
    fun ip(): String
    fun host(): String
    fun port(): Int
    fun profile(): ProfileType
}