package com.xiao.boot.base

/**
 *
 * @author lix wang
 */
object ServerConstants {
    const val ENV_TEST = "test"
    const val ENV_DEVELOP = "develop"
    const val ENV_BETA = "beta"
    const val ENV_STAGING = "staging"
    const val ENV_PRODUCTION = "production"

    const val DEFAULT_SERVER_PORT = 8080
    const val DEFAULT_TEST_MYSQL_URL = "jdbc:mysql://localhost:3306"
    const val DEFAULT_TEST_MYSQL_USERNAME = "root"
    const val DEFAULT_TEST_MYSQL_PASSWORD = "123456"

    const val ENV_ENCRYPT_KEY = "spring.boot.encrypt.key"
    const val TEST_MYSQL_URL = "spring.boot.test.mysql.url"
    const val TEST_MYSQL_USERNAME = "spring.boot.test.mysql.username"
    const val TEST_MYSQL_PASSWORD = "spring.boot.test.mysql.password"

    const val SERVER_NAME_KEY = "spring.boot.server.name"
}