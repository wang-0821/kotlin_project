package com.xiao.boot.env.parser

import com.xiao.boot.env.EnvProperty
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

/**
 *
 * @author lix wang
 */
@Component
class LongEnvPropertyParser : EnvPropertyParser<Long> {
    override fun resolve(envProperty: EnvProperty): Long {
        TODO("Not yet implemented")
    }

    override fun classType(): KClass<Long> {
        return Long::class
    }
}