package xiao.boot.mybatis.testing

/**
 *
 * @author lix wang
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class TestKtSpringDatabases(
    vararg val databases: TestKtSpringDatabase = []
)