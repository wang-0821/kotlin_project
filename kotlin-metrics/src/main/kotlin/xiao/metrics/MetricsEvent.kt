package xiao.metrics

/**
 *
 * @author lix wang
 */
data class MetricsEvent(
    val type: String,
    val state: String,
    val prefix: String? = null,
    val suffix: String? = null
) {
    val name = concat(prefix, suffix)

    val fullName = concat(concat(state, name), state)

    private fun concat(value1: String?, value2: String?): String {
        return if (value1 != null && value2 != null) {
            "$value1.$value2"
        } else {
            value1 ?: value2
        } ?: ""
    }
}