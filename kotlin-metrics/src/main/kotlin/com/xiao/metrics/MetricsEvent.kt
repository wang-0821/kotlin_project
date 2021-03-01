package com.xiao.metrics

/**
 *
 * @author lix wang
 */
class MetricsEvent(
    val type: MetricsType,
    val prefixName: String,
    val suffixName: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MetricsEvent

        if (type != other.type) return false
        if (prefixName != other.prefixName) return false
        if (suffixName != other.suffixName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + prefixName.hashCode()
        result = 31 * result + suffixName.hashCode()
        return result
    }
}