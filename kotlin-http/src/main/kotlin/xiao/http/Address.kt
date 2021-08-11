package xiao.http

/**
 *
 * @author lix wang
 */
class Address {
    val isTls: Boolean
    val host: String
    val port: Int

    constructor(host: String, scheme: String, port: Int = -1) {
        this.isTls = scheme == "https"
        this.host = host
        this.port = if (port > 0) {
            port
        } else {
            if (isTls) {
                443
            } else {
                80
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as xiao.http.Address

        if (host != other.host || port != other.port) return false

        return true
    }

    override fun hashCode(): Int {
        var result = host.hashCode()
        result = 31 * result + port
        return result
    }

    override fun toString(): String {
        return "Address(isTls=$isTls, host='$host', port=$port)"
    }
}