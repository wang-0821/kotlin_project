package com.xiao.base.resource

import java.io.File

/**
 *
 * @author lix wang
 */
object ClassResourceMatcher : ResourceMatcher {
    override fun matchingDirectory(file: File): Boolean {
        return true
    }

    override fun matchingFile(file: File): Boolean {
        return file.name.endsWith(".class")
    }
}