package xiao.http

/**
 *
 * @author lix wang
 */
enum class ContentEncodingType(val text: String) {
    COMPRESS("compress"),
    DEFLATE("deflate"),
    GZIP("gzip"),
    IDENTITY("identity");

    companion object {
        fun parse(text: String): ContentEncodingType {
            for (value in values()) {
                if (value.text.equals(text, true)) {
                    return value
                }
            }
            throw UnsupportedOperationException("Unsupported content encoding type $text.")
        }
    }
}