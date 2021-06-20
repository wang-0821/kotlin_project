package com.xiao.boot

import com.xiao.boot.env.EnvProperties
import com.xiao.boot.env.EnvProperty
import com.xiao.boot.env.KtConfiguration
import com.xiao.boot.env.ProfileType

@KtConfiguration
class DemoEnvConfig {
    @EnvProperties(
        [EnvProperty(profiles = [ProfileType.DEV], value = "1")]
    )
    var intValue: Int = 0
}