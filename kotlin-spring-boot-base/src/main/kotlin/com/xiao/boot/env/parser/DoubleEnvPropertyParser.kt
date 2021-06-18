package com.xiao.boot.env.parser

import com.xiao.boot.env.EnvProperty
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

/**
 *
 * @author lix wang
 */
@Component
class DoubleEnvPropertyParser : EnvPropertyParser<Double> {
    override fun resolve(envProperty: EnvProperty): Double {
        TODO("Not yet implemented")
    }

    override fun classType(): KClass<Double> {
        return Double::class
    }
}