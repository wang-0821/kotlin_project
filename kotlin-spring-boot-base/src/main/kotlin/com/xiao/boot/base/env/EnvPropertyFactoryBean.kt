package com.xiao.boot.base.env

import com.xiao.base.util.JsonUtils
import com.xiao.boot.base.parser.StringValueParseResolver
import com.xiao.boot.base.util.SecureUtils
import com.xiao.boot.base.util.activeProfileType
import org.springframework.beans.factory.FactoryBean
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.Environment
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
    init {
        println("init properties factoryBean.")
    }

    private lateinit var environment: Environment

    override fun getObject(): T {
        checkNoArgsConstructorExist(clazz)
        val instance = clazz.newInstance()
        parseInstanceProperties(instance)
        println("Create bean ${clazz.name}, ${JsonUtils.serialize(instance)}.")
        return instance
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
                val envProperties = javaField?.getAnnotationsByType(EnvProperty::class.java)
                if (!envProperties.isNullOrEmpty()) {
                    val realEnvProperties = envProperties.filter { it.profiles.contains(profile) }
                    assert(realEnvProperties.size == 1) {
                        "Class ${clazz.name} property ${kProperty.name}, " +
                            "need have exact one value in ${profile.profileName} env."
                    }

                    val value = prepareValue(javaField, realEnvProperties.first())
                    val resolvedValue = StringValueParseResolver.resolve(javaField.genericType, value)
                    check(resolvedValue == null && !kProperty.returnType.isMarkedNullable) {
                        "Not allowed null value set for ${kProperty.name}."
                    }
                    javaField.set(instance, resolvedValue)
                }
            }
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