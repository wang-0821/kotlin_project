package com.xiao.base.resource

import java.io.File

/**
 *
 * @author lix wang
 */
interface ResourceMatcher {
    fun matchingDirectory(file: File): Boolean

    fun matchingFile(file: File): Boolean
}