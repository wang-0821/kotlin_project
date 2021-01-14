package com.xiao.model

import com.xiao.base.CommonConstants
import java.io.File
import java.io.FileInputStream

/**
 * Make parent classLoader is null.
 * Use current classLoader to loadClass.
 *
 * @author lix wang
 */
class CustomClassLoader : ClassLoader() {
    override fun loadClass(name: String): Class<*> {
        synchronized(getClassLoadingLock(name)) {
            var c = findLoadedClass(name)
            if (c == null) {
                if (name.startsWith("com.xiao")) {
                    c = findClass(name)
                }

                if (c == null) {
                    try {
                        if (parent != null) {
                            c = parent.loadClass(name)
                        }
                    } catch (e: ClassNotFoundException) {
                        // ClassNotFoundException thrown if class not found
                        // from the non-null parent class loader
                    }
                }
            }
            return c
        }
    }

    override  fun findClass(name: String): Class<*> {
        val classInputStream = getClassfile(name) ?: throw ClassNotFoundException(name)
        val bytes = classInputStream.readBytes()
        return defineClass(name, bytes, 0, bytes.size) ?: throw ClassNotFoundException(name)
    }

    private fun getClassfile(name: String): FileInputStream? {
        var resourceName = name.replace(".", File.separator)
        if (resourceName.endsWith(File.separator)) {
            return null
        }
        resourceName = CommonConstants.absolutePath() + resourceName
        resourceName += CommonConstants.CLASS_SUFFIX
        val file = File(resourceName)
        if (!file.exists()) {
            return null
        }
        return FileInputStream(file)
    }
}