package com.xiao.base.resource

import java.io.File
import java.net.URL
import java.util.*

/**
 *
 * @author lix wang
 */
class PathResourceScanner(private val resourceLoader: ResourceLoader = DefaultResourceLoader) {
    fun scanByPackage(basePackage: String): List<KtResource> {
        if (basePackage.isNullOrBlank()) {
            return emptyList()
        }

        val result = mutableListOf<KtResource>()
        val realBasePackage = basePackage.replace(".", "/")
        val classLoader = resourceLoader.getClassLoader()
        val resourceUrls: Enumeration<URL> = classLoader?.getResources(realBasePackage)
            ?: ClassLoader.getSystemResources(realBasePackage)
        while (resourceUrls.hasMoreElements()) {
            val rootFile = File(resourceUrls.nextElement().toURI().schemeSpecificPart)
            val targetFiles = findResourceFiles(rootFile, resourceLoader.getMatcher())
            result.addAll(retrieveValidateResources(basePackage, rootFile.absolutePath, targetFiles))
        }
        return result
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
            if (content.isDirectory && matcher?.matchingDirectory(rootAbsolutePath, content) != false) {
                if (content.canRead()) {
                    retrieveMatchingFiles(rootAbsolutePath, content, matcher, result)
                }
            }
            if (content.isFile && matcher?.matchingFile(rootAbsolutePath, content) != false) {
                result.add(content)
            }
        }
    }

    private fun retrieveValidateResources(
        basePackage: String,
        rootAbsolutePath: String,
        files: Set<File>
    ): List<KtResource> {
        val result = mutableListOf<KtResource>()
        for (file in files) {
            retrieveValidateResource(basePackage, rootAbsolutePath, file)?.let {
                result.add(it)
            }
        }
        return result
    }

    private fun retrieveValidateResource(
        basePackage: String,
        rootAbsolutePath: String,
        file: File
    ): KtResource? {
        return try {
            var path = file.absolutePath.removePrefix(rootAbsolutePath)
                .replace(File.separator, ".")
                .removeSuffix(".class")
            path = basePackage + path
            val kClass = resourceLoader.getClassLoader()?.let {
                Class.forName(path, true, resourceLoader.getClassLoader()).kotlin
            } ?: kotlin.run {
                Class.forName(path).kotlin
            }
            KtResource(file, file.path, kClass)
        } catch (e: Exception) {
            null
        }
    }
}