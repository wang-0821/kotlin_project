package com.xiao.base.resource

import java.io.File
import kotlin.reflect.KClass

/**
 *
 * @author lix wang
 */
class KtResource(val file: File, val path: String, val clazz: KClass<*>)