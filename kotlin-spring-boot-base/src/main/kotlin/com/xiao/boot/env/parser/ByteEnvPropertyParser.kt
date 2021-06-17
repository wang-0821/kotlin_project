package com.xiao.boot.env.parser

import com.xiao.boot.env.EnvProperty
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

/**
 *
 * @author lix wang
 */
@Component
class ByteEnvPropertyParser : EnvPropertyParser<Byte> {
    override fun resolve(envProperty: EnvProperty): Byte {
        TODO("Not yet implemented")
    }

    override fun classType(): KClass<Byte> {
        return Byte::class
    }
}