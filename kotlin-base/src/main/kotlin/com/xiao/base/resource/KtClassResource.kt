package com.xiao.base.resource

import java.io.File
import kotlin.reflect.KClass

/**
 *
 * @author lix wang
 */
class KtClassResource(val file: File, val path: String, val clazz: KClass<*>)