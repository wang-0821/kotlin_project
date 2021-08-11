package xiao.beans.resource

import java.io.File
import kotlin.reflect.KClass

/**
 *
 * @author lix wang
 */
data class KtClassResource(val file: File, val path: String, val clazz: KClass<*>)