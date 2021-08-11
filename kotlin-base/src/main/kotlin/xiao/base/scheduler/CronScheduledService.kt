package xiao.base.scheduler

import java.lang.reflect.Method
import java.time.Duration

/**
 *
 * @author lix wang
 */
interface CronScheduledService {
    fun start()

    fun shutdown()

    fun execScheduledMethod(method: Method)

    fun execScheduledMethod(
        initial: Duration,
        fixedDelay: Duration,
        fixedRate: Duration,
        method: Method
    )
}