package com.xiao.databse

import org.apache.ibatis.session.TransactionIsolationLevel
import java.util.concurrent.TimeUnit
import javax.sql.DataSource
import kotlin.reflect.KClass

/**
 *
 * @author lix wang
 */
class TransactionalWrapper (
    var dataSources: List<DataSource> = listOf(),
    var isolation: TransactionIsolationLevel = TransactionIsolationLevel.REPEATABLE_READ,
    var timeout: Long? = null,
    var timeUnit: TimeUnit? = null,
    var rollbackFor: List<KClass<out Throwable>> = listOf(Exception::class),
    var noRollbackFor: List<KClass<out Throwable>> = listOf()
)