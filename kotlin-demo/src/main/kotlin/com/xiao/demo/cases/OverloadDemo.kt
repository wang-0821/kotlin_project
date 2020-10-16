package com.xiao.demo.cases

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
    listOf("aaa", "bbb").groupBy {  }
    listOf("one", "two").groupingBy {  }.eachCount()
    println("one" in listOf("one"))
}