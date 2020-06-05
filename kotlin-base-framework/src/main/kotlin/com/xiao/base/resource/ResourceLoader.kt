package com.xiao.base.resource

import java.io.File

/**
 *
 * @author lix wang
 */
interface ResourceLoader {
    fun  getClassLoader(): ClassLoader?

    fun getMatcher(): ResourceMatcher?
}

object DefaultResourceLoader : ResourceLoader {
    override fun getClassLoader(): ClassLoader? {
        return Thread.currentThread().contextClassLoader ?: ClassLoader.getSystemClassLoader()
    }

    override fun getMatcher() = object : ResourceMatcher {
        override fun matchingDirectory(basePackageAbsolutePath: String, currentFile: File): Boolean {
            return true
        }

        override fun matchingFile(basePackageAbsolutePath: String, currentFile: File): Boolean {
            val names: List<String> = currentFile.name.split(".")
            return names.size == 2 && !names[0].contains("$") && names[1] == "class"
        }
    }
}

interface ResourceMatcher {
    fun matchingDirectory(basePackageAbsolutePath: String, currentFile: File): Boolean

    fun matchingFile(basePackageAbsolutePath: String, currentFile: File): Boolean
}