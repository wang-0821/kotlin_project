package com.xiao.base.util

import java.lang.reflect.Field
import java.lang.reflect.Modifier
import kotlin.reflect.KClass

/**
 *
 * @author lix wang
 */
object ClassUtils {
    @JvmStatic
    fun makeAccessible(field: Field) {
        if ((!Modifier.isPublic(field.modifiers) ||
                !Modifier.isPublic(field.declaringClass.modifiers) ||
                Modifier.isFinal(field.modifiers)) && !field.isAccessible) {
            field.isAccessible = true
        }
    }

    @JvmStatic
    fun setFieldValue(instance: Any, fieldName: String, fieldValue: Any) {
        val field = instance::class.java.getDeclaredField(fieldName)
        makeAccessible(field)
        field.set(instance, fieldValue)
    }
}

fun KClass<*>.packageName(): String {
    val name = this.java.name
    val lastDotIndex: Int = name.lastIndexOf(".")
    return if (lastDotIndex != -1) name.substring(0, lastDotIndex) else ""
}

fun <T : Class<out Any>> T.extractAnnotations(): List<Annotation> {
    val result = mutableListOf<Annotation>()
    return this.extractAnnotations(result)
}

private fun <T : Class<out Any>> T.extractAnnotations(result: MutableList<Annotation>): List<Annotation> {
    val annotations = this.annotations.filter { !result.contains(it) }
    result.addAll(annotations)
    for (annotation in annotations) {
        annotation.annotationClass.java.extractAnnotations(result)
    }
    return result
}