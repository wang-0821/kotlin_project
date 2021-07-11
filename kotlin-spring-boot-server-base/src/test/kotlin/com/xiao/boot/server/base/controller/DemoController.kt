package com.xiao.boot.server.base.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

/**
 * @author lix wang
 */
@RestController
@ResponseBody
@RequestMapping("/api/v1/demo")
class DemoController {
    @GetMapping("/helloWorld")
    fun helloWorld(): String {
        return "hello world"
    }
}