package com.xiao.boot.parser

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

/**
 *
 * @author lix wang
 */
@Component
class ListValueParser : ValueParser<List<Any>>, ApplicationContextAware {
    private lateinit var context: ApplicationContext

    override fun parse(value: String): List<Any> {
        return listOf()
    }

    override fun classType(): Class<List<*>> {
        return List::class.java
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.context = applicationContext
    }
}