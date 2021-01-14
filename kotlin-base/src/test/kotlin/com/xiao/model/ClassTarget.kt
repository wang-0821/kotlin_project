package com.xiao.model

/**
 *
 * @author lix wang
 */
class ClassTarget {
    val val2 = 2

    companion object {
        const val val1 = 1
        init {
            println("Process companion init method.")
        }
    }
}