package com.xiao.boot.base.env

/**
 *
 * @author lix wang
 */
object EnvConstants {
    const val ENV_TEST = "test"
    const val ENV_DEVELOP = "develop"
    const val ENV_BETA = "beta"
    const val ENV_STAGING = "staging"
    const val ENV_PRODUCTION = "production"

    const val DEFAULT_SERVER_PORT = 8080
    const val ENV_ENCRYPT_KEY = "spring.boot.encrypt.key"
}