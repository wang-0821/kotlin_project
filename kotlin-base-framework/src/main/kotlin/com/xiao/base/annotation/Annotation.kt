package com.xiao.base.annotation

/**
 *
 * @author lix wang
 */
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