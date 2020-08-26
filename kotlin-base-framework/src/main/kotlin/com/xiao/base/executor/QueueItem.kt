package com.xiao.base.executor

/**
 *
 * @author lix wang
 */
abstract class QueueItem(val name: String, val runnable: Runnable): Runnable