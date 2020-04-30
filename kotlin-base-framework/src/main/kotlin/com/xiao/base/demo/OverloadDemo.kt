package com.xiao.base.demo

/**
 *
 * @author lix wang
 */
class OverloadDemo {
    fun printStr(str1: String = "hello", str2: String = "world") {
        println("$str1 $str2")
    }
    fun printStr(str1: String) {
        println("$str1")
    }
}

fun main() {
    OverloadDemo().printStr("hello")
}