package com.xiao.base.resource

import com.xiao.base.logging.Logging
import java.io.File
import java.net.URL
import java.util.Enumeration

/**
 *
 * @author lix wang
 */
object PathResourceScanner : Logging() {
    private val defaultClassLoader = Thread.currentThread().contextClassLoader ?: ClassLoader.getSystemClassLoader()

    fun scanClassResources(
        basePackage: String
    ): List<KtClassResource> {
        if (basePackage.isBlank()) {
            return emptyList()
        }
        val classLoader = defaultClassLoader
        return retrieveClassResources(
            scanFileResources(basePackage, ClassResourceMatcher, classLoader),
            classLoader
        )
    }

    fun scanFileResources(
        basePackage: String,
        matcher: ResourceMatcher,
        classLoader: ClassLoader = defaultClassLoader
    ): List<KtFileResource> {
        try {
            val result = mutableListOf<KtFileResource>()
            val realBasePackage = basePackage.replace(".", "/")
            val resourceUrls: Enumeration<URL> = classLoader.getResources(realBasePackage)
                ?: ClassLoader.getSystemResources(realBasePackage)
            while (resourceUrls.hasMoreElements()) {
                val rootFile = File(resourceUrls.nextElement().toURI().schemeSpecificPart)
                val targetFiles = findResourceFiles(rootFile, matcher)
                result.addAll(targetFiles.map { KtFileResource(it, it.absolutePath) })
            }
            return result
        } catch (e: Exception) {
            throw e
        }
    }

    fun scanFileResourcesWithSuffix(basePackage: String, suffix: String): List<KtFileResource> {
        return scanFileResources(
            basePackage,
            object : ResourceMatcher {
                override fun matchingDirectory(file: File): Boolean {
                    return true
                }

                override fun matchingFile(file: File): Boolean {
                    return file.name.endsWith(suffix)
                }
            }
        )
    }

    private fun findResourceFiles(rootDir: File, matcher: ResourceMatcher?): Set<File> {
        if (!rootDir.exists() || !rootDir.isDirectory || !rootDir.canRead()) {
            return emptySet()
        }
        val result = mutableSetOf<File>()
        retrieveMatchingFiles(rootDir.absolutePath, rootDir, matcher, result)
        return result
    }

    private fun retrieveMatchingFiles(
        rootAbsolutePath: String,
        currentFile: File,
        matcher: ResourceMatcher?,
        result: MutableSet<File>
    ) {
        val files = currentFile.listFiles()
        if (files.isNullOrEmpty()) {
            return
        }
        files.sort()
        for (content in files) {
            if (content.isDirectory && matcher?.matchingDirectory(content) != false) {
                if (content.canRead()) {
                    retrieveMatchingFiles(rootAbsolutePath, content, matcher, result)
                }
            }
            if (content.isFile && matcher?.matchingFile(content) != false) {
                result.add(content)
            }
        }
    }

    private fun retrieveClassResources(
        ktFileResources: List<KtFileResource>,
        classLoader: ClassLoader
    ): List<KtClassResource> {
        val result = mutableListOf<KtClassResource>()
        for (file in ktFileResources) {
            retrieveClassResource(file, classLoader)?.let {
                result.add(it)
            }
        }
        return result
    }

    private fun retrieveClassResource(
        ktFileResource: KtFileResource,
        classLoader: ClassLoader
    ): KtClassResource? {
        return try {
            val classNameSplitArray = ktFileResource.file.path.split("/")
            if (classNameSplitArray.isEmpty() || !classNameSplitArray.last().endsWith(".class")) {
                return null
            }
            var dirIndex = classNameSplitArray.indexOf("main")
            if (dirIndex < 0) {
                dirIndex = classNameSplitArray.indexOf("test")
            }
            val className = classNameSplitArray.subList(dirIndex + 1, classNameSplitArray.size)
                .joinToString(".")
                .removeSuffix(".class")
            val kClass = Class.forName(className, true, classLoader).kotlin
            KtClassResource(ktFileResource.file, ktFileResource.file.path, kClass)
        } catch (e: Exception) {
            log.error("Retrieve resource failed. ${e.message}", e)
            null
        }
    }
}