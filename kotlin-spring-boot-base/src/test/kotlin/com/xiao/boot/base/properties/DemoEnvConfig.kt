package com.xiao.boot.base.properties

import com.xiao.boot.base.env.EnvProperties
import com.xiao.boot.base.env.EnvProperty
import com.xiao.boot.base.env.KtConfiguration
import com.xiao.boot.base.env.ProfileType
import com.xiao.boot.base.model.EnvConfigModel

@KtConfiguration
class DemoEnvConfig {
    @EnvProperties(
        EnvProperty(profiles = [ProfileType.DEV], value = "1")
    )
    var intValue: Int = 0

    @EnvProperty(profiles = [ProfileType.DEV], value = "1")
    var stringValue: String = ""

    @EnvProperty(profiles = [ProfileType.DEV], value = "1")
    var longValue: Long = 0

    @EnvProperty(profiles = [ProfileType.DEV], value = "1")
    var byteValue: Byte = 0

    @EnvProperty(profiles = [ProfileType.DEV], value = "1")
    var shortValue: Short = 0

    @EnvProperty(profiles = [ProfileType.DEV], value = "1.1")
    var floatValue: Float = 0.0f

    @EnvProperty(profiles = [ProfileType.DEV], value = "1.2")
    var doubleValue: Double = 0.0

    @EnvProperty(profiles = [ProfileType.DEV], value = "true")
    var booleanValue: Boolean = false

    @EnvProperty(profiles = [ProfileType.DEV], value = "{\"var1\":1}")
    var mapValue: Map<String, Int> = mapOf()

    @EnvProperty(profiles = [ProfileType.DEV], value = "[1,2,3]")
    var listValue: List<Int> = listOf()

    @EnvProperty(profiles = [ProfileType.DEV], value = "{\"var1\":\"abc\",\"var2\":123}")
    var objValue: EnvConfigModel = EnvConfigModel()

    @EnvProperty(profiles = [ProfileType.DEV], allowEmpty = true)
    var nullableValue: Int? = null
}