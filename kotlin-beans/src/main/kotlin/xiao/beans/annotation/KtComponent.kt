package xiao.beans.annotation

import kotlin.reflect.KClass

/**
 *
 * @author lix wang
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@AnnotationScan
annotation class KtComponent(
    val value: String = "",
    val handler: KClass<out AnnotationHandler> = ComponentResourceHandler::class
)