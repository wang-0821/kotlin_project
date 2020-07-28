package com.xiao.base.demo

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

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
    val httpUrl = "http://www.baidu.com/api/demo.txt?a=a&b=b".toHttpUrlOrNull()
    println(httpUrl)
}