package com.xiao.base.annotation

/**
 * Class annotated by a annotation which annotated by this annotation will be scanned out.
 *
 * @author lix wang
 */
@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class AnnotationScanner