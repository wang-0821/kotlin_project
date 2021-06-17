package com.xiao.boot.env.parser

import com.xiao.boot.env.EnvProperty
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

/**
 *
 * @author lix wang
 */
@Component
class BooleanEnvPropertyParser : EnvPropertyParser<Boolean> {
    override fun resolve(envProperty: EnvProperty): Boolean {
        TODO("Not yet implemented")
    }

    override fun classType(): KClass<Boolean> {
        return Boolean::class
    }
}