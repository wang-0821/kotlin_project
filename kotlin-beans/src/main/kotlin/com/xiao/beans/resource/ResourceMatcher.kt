package com.xiao.beans.resource

import java.io.File

/**
 *
 * @author lix wang
 */
interface ResourceMatcher {
    fun matchingDirectory(file: File): Boolean

    fun matchingFile(file: File): Boolean
}