package com.xiao.boot.admin.client

/**
 * Alternative configuration for [spring.boot.admin.client.url].
 *
 * @author lix wang
 */
abstract class AdminConfig {
    open var adminServerUrl: String = ""
}