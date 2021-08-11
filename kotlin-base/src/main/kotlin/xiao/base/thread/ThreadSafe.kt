package xiao.base.thread

/**
 * Mark annotated target is xiao.base.thread safe.
 *
 * @author lix wang
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.FIELD,
    AnnotationTarget.FUNCTION
)
annotation class ThreadSafe(
    val description: String = ""
)