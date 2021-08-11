package xiao.databse.annotation

import xiao.databse.BaseDatabase
import kotlin.reflect.KClass

/**
 *  用以配置测试类使用到的数据源。
 *
 * @author lix wang
 */
@Repeatable
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class KtTestDatabase(
    val database: KClass<out xiao.databse.BaseDatabase>,
    val mappers: Array<KClass<*>> = []
)