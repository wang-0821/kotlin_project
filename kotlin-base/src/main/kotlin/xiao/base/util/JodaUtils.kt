package xiao.base.util

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

/**
 *
 * @author lix wang
 */
object JodaTimeUtils {
    @JvmField
    val DEFAULT_DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ")
    @JvmField
    val CST_TIME_ZONE: DateTimeZone = DateTimeZone.forID("Asia/Shanghai")

    @JvmStatic
    fun fromUtcString(timeString: String): DateTime {
        return DateTime.parse(timeString).withZone(DateTimeZone.UTC)
    }

    @JvmStatic
    fun fromCstString(timeString: String): DateTime {
        return DateTime.parse(timeString).withZone(CST_TIME_ZONE)
    }
}

fun DateTime.toCstString(): String {
    return withZone(JodaTimeUtils.CST_TIME_ZONE).toString(JodaTimeUtils.DEFAULT_DATE_TIME_FORMATTER)
}

fun DateTime.toUtcString(): String {
    return withZone(DateTimeZone.UTC).toString(JodaTimeUtils.DEFAULT_DATE_TIME_FORMATTER)
}