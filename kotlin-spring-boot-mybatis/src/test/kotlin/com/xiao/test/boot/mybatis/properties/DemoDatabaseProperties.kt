package com.xiao.test.boot.mybatis.properties

import com.xiao.boot.base.env.ProfileType
import com.xiao.boot.base.property.EnvProperty
import com.xiao.boot.base.property.KtConfiguration

/**
 *
 * @author lix wang
 */
@KtConfiguration
class DemoDatabaseProperties {
    @EnvProperty(profiles = [ProfileType.TEST], value = "jdbc:mysql://localhost:3306/lix_database_demo")
    var databaseUrl: String = ""

    @EnvProperty(profiles = [ProfileType.TEST], value = "root")
    var databaseUsername: String = ""

    @EnvProperty(profiles = [ProfileType.TEST], value = "123456")
    var databasePassword: String = ""
}