package com.xiao.boot.mybatis.proxy

/**
 *
 * @author lix wang
 */
interface MapperInvocation {
    fun proceed(invocation: MapperInvocation?): Any?
}