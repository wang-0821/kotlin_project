package com.xiao.test.boot.server.undertow.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author lix wang
 */
@RestController
@RequestMapping("/api/v1/demo")
class DemoController {
    @GetMapping("/helloWorld")
    fun helloWorld(): String {
        return "hello world"
    }

    @PostMapping("/printInput")
    fun printInut(@RequestBody body: String): String {
        return body
    }

    @GetMapping("/throwException")
    fun throwException() {
        throw RuntimeException("throw exception")
    }

    @GetMapping("/throwExceptionSuspend")
    suspend fun throwExceptionSuspend() {
        throw RuntimeException("thorw exception suspend")
    }

    @GetMapping("/helloWorldSuspend")
    suspend fun helloWorldSuspend(): String {
        return "hello world"
    }
}