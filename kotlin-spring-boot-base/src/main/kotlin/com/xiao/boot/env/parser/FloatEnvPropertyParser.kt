package com.xiao.boot.env.parser

import com.xiao.boot.env.EnvProperty
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

/**
 *
 * @author lix wang
 */
@Component
class FloatEnvPropertyParser : EnvPropertyParser<Float> {
    override fun resolve(envProperty: EnvProperty): Float {
        TODO("Not yet implemented")
    }

    override fun classType(): KClass<Float> {
        return Float::class
    }
}