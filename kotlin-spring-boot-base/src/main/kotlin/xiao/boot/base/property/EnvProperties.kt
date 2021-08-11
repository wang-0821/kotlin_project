package xiao.boot.base.property

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class EnvProperties(
    vararg val value: EnvProperty = []
)