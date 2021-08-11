package xiao.databse

/**
 *
 * @author lix wang
 */
interface TransactionHandler {
    fun beforeTransaction()

    fun commit()

    fun rollback(throwable: Throwable)

    fun afterTransaction()
}