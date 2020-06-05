package com.xiao.base.demo

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 *
 * @author lix wang
 */
@RestController
@RequestMapping("/api/users")
class DemoApi {
    @RequestMapping("")
    fun get() {

    }
}