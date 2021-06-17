package com.xiao.boot.env.parser

import com.xiao.boot.env.EnvProperty
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

/**
 *
 * @author lix wang
 */
@Component
class IntEnvPropertyParser : EnvPropertyParser<Int> {
    override fun resolve(envProperty: EnvProperty): Int {
        TODO("Not yet implemented")
    }

    override fun classType(): KClass<Int> {
        return Int::class
    }
}