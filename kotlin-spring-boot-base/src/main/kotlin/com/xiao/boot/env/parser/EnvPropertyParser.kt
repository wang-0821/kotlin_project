package com.xiao.boot.env.parser

import com.xiao.boot.env.EnvProperty
import kotlin.reflect.KClass

/**
 *
 * @author lix wang
 */
interface EnvPropertyParser<T : Any> {
    fun resolve(envProperty: EnvProperty): T

    fun classType(): KClass<T>
}