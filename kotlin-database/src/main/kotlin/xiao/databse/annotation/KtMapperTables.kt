package xiao.databse.annotation

/**
 * 设置Mapper使用到的表，用以方便测试时以具体的表来做数据迁移。
 * 避免全部表都进行数据迁移，提升测试执行效率。
 *
 * @author lix wang
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class KtMapperTables(
    val value: Array<String> = []
)