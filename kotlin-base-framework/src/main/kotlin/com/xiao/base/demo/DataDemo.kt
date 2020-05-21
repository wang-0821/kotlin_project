package com.xiao.base.demo

/**
 *
 * @author lix wang
 */
data class DataDemo(val name: String) {
    var age: Int = 0
}

fun main() {
    var dataDemo = DataDemo("name").also {  }
    dataDemo.age = 10

    println(dataDemo.toString())
}