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
    val dataSources: List<DataSource> = listOf(),
    val isolation: TransactionIsolationLevel = TransactionIsolationLevel.REPEATABLE_READ,
    val timeout: Long? = null,
    val timeUnit: TimeUnit? = null,
    val rollbackFor: List<KClass<out Throwable>> = listOf(Exception::class),
    val noRollbackFor: List<KClass<out Throwable>> = listOf()
)