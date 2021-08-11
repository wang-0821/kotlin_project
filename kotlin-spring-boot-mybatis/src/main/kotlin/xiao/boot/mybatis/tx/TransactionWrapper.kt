package xiao.boot.mybatis.tx

import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation

/**
 *
 * @author lix wang
 */
class TransactionWrapper(
    var value: String = "",
    var isolation: Isolation = Isolation.DEFAULT,
    var propagation: Propagation = Propagation.REQUIRED,
    var timeout: Int = -1,
    var readOnly: Boolean = false,
    var rollbackFor: Array<Class<out Throwable>> = arrayOf(),
    var noRollbackFor: Array<Class<out Throwable>> = arrayOf()
)