package com.xiao.boot.admin.client.config

import com.xiao.boot.admin.client.AdminConfig
import com.xiao.boot.base.property.KtConfiguration

/**
 *
 * @author lix wang
 */
@KtConfiguration
class DemoAdminConfig : AdminConfig() {
    override var adminServerUrl: String = "localhost:8088"
}