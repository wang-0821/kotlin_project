package com.xiao.boot.base.env

import com.xiao.boot.base.parser.StringValueParseResolver
import com.xiao.boot.base.util.SecureUtils
import com.xiao.boot.base.util.activeProfileType
import org.springframework.beans.factory.FactoryBean
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.Environment
import org.springframework.util.ReflectionUtils
import java.lang.reflect.Field
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

/**
 *
 * @author lix wang
 */
class EnvPropertyFactoryBean<T : Any>(
    private val clazz: Class<T>
) : FactoryBean<T>, EnvironmentAware {
    private lateinit var environment: Environment

    override fun getObject(): T {
        checkNoArgsConstructorExist(clazz)
        return clazz.newInstance()
            .also {
                parseInstanceProperties(it)
            }
    }

    override fun getObjectType(): Class<T> {
        return clazz
    }

    override fun setEnvironment(environment: Environment) {
        this.environment = environment
    }

    private fun checkNoArgsConstructorExist(clazz: Class<T>) {
        if (clazz.constructors.none { it.parameterCount == 0 }) {
            throw IllegalArgumentException("Class ${clazz.name} must has a constructor without arguments.")
        }
    }

    private fun parseInstanceProperties(instance: T) {
        val profile = environment.activeProfileType()
        instance::class.memberProperties
            .forEach { kProperty ->
                val javaField = kProperty.javaField
                ReflectionUtils.makeAccessible(javaField)
                val envProperty = getEnvProperty(javaField!!, profile)
                envProperty
                    ?.let {
                        val value = prepareValue(javaField, it)
                        val resolvedValue = StringValueParseResolver.resolve(javaField.genericType, value)
                        check(resolvedValue != null || kProperty.returnType.isMarkedNullable) {
                            "Not allowed null value set for ${kProperty.name}."
                        }
                        javaField.set(instance, resolvedValue)
                    }
            }
    }

    private fun getEnvProperty(field: Field, profileType: ProfileType): EnvProperty? {
        val envPropertyList = field.getAnnotationsByType(EnvProperty::class.java)?.toList() ?: listOf()
        val envProperties = field.getAnnotationsByType(EnvProperties::class.java)
            ?.flatMap {
                it.value.toList()
            } ?: listOf()

        val totalEnvProperties = envProperties + envPropertyList
        val result = totalEnvProperties.filter { it.profiles.contains(profileType) }
        if ((envProperties.isNotEmpty() || envPropertyList.isNotEmpty()) && result.isEmpty()) {
            throw IllegalStateException("Field ${field.name} not found env property for ${profileType.profileName}.")
        }
        check(result.size <= 1) {
            "Field ${field.name} found more than one property for active file ${profileType.profileName}."
        }
        return result.firstOrNull()
    }

    private fun prepareValue(filed: Field, envProperty: EnvProperty): String {
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
                val encryptKey = environment.getProperty(envProperty.encryptKey)
                    ?: throw RuntimeException("Not set environment property of ${envProperty.encryptKey}.")
                SecureUtils.aesDecrypt(envProperty.value, encryptKey)
            } else {
                envProperty.value
            }
        }
    }
}