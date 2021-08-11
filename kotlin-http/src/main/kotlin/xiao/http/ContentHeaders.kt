package xiao.http

/**
 *
 * @author lix wang
 */
enum class ContentHeaders(val text: String) {
    CONTENT_ENCODING("Content-Encoding"),
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    ACCEPT_ENCODING("Accept-Encoding"),
    TRANSFER_ENCODING("Transfer-Encoding");
}