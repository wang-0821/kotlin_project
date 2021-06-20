package com.xiao.boot.env

import com.xiao.boot.util.SecureUtils
import org.springframework.beans.factory.FactoryBean
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import java.lang.reflect.Field
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

/**
 *
 * @author lix wang
 */
class EnvPropertyFactoryBean<T : Any>(
    private val clazz: Class<T>
) : FactoryBean<T>, ApplicationContextAware {
    private lateinit var applicationContext: ApplicationContext

    override fun getObject(): T {
        checkNoArgsConstructorExist(clazz)
        val instance = clazz.newInstance()
        val envInfoProvider = applicationContext.getBean(EnvInfoProvider::class.java)
        parseInstanceProperties(instance, envInfoProvider)
        return instance
    }

    override fun getObjectType(): Class<T> {
        return clazz
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

    private fun checkNoArgsConstructorExist(clazz: Class<T>) {
        if (clazz.constructors.none { it.parameterCount == 0 }) {
            throw IllegalArgumentException("Class ${clazz.name} must has a constructor without arguments.")
        }
    }

    private fun parseInstanceProperties(instance: T, envInfoProvider: EnvInfoProvider) {
        val profile = envInfoProvider.profile()
        instance::class.memberProperties
            .forEach { kProperty ->
                val javaField = kProperty.javaField
                val envProperties = javaField?.getAnnotationsByType(EnvProperty::class.java)
                if (!envProperties.isNullOrEmpty()) {
                    val realEnvProperties = envProperties.filter { it.profiles.contains(profile) }
                    assert(realEnvProperties.size == 1) {
                        "Class ${clazz.name} property ${kProperty.name}, " +
                            "need have exact one value in ${profile.profileName} env."
                    }
                    val value = decryptValue(javaField, realEnvProperties.first())
                    val parser = EnvPropertyValueParser(applicationContext, javaField.type, value)
                    javaField.set(instance, parser.parse())
                }
            }
    }

    private fun decryptValue(filed: Field, envProperty: EnvProperty): String {
        return if (envProperty.value.isEmpty()) {
            if (envProperty.allowEmpty) {
                envProperty.value
            } else {
                throw IllegalArgumentException("Field ${filed.name} not allowed empty.")
            }
        } else {
            if (envProperty.encrypt) {
                check(envProperty.encryptKey.isNotEmpty()) {
                    "Field ${filed.name} encryptKey must not empty."
                }
                val encryptKey = applicationContext.environment.getProperty(envProperty.encryptKey)
                    ?: throw RuntimeException("Not set environment property of ${envProperty.encryptKey}.")
                SecureUtils.aesDecrypt(envProperty.value, encryptKey)
            } else {
                envProperty.value
            }
        }
    }
}