package xiao.base.executor

import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.ThreadPoolExecutor

/**
 *
 * @author lix wang
 */
abstract class AbstractExecutorService(
    val executorService: ExecutorService
) {
    open fun shutdown() {
        executorService.shutdown()
    }

    open fun shutdownNow() {
        executorService.shutdownNow()
    }

    open fun isShutdown(): Boolean {
        return executorService.isShutdown
    }

    open fun fastShutdown(): Future<Unit> {
        shutdown()
        if (executorService is ThreadPoolExecutor) {
            while (executorService.queue.poll() == null) {
                break
            }
        }
        return xiao.base.executor.CallableFuture {
            while (isShutdown()) {
                break
            }
        }
    }
}