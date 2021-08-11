package xiao.demo.coroutines

import kotlinx.coroutines.CompletableDeferred

/**
 *
 * @author lix wang
 */
sealed class CounterMsg

object IncCounter : CounterMsg()

class GetCounter(val response: CompletableDeferred<Int>) : CounterMsg()