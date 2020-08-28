package com.xiao.base.context

import com.xiao.base.annotation.AnnotatedKtResource
import com.xiao.base.annotation.AnnotationScan
import com.xiao.base.annotation.Component
import com.xiao.base.annotation.ContextInject
import com.xiao.base.annotation.extractAnnotations
import com.xiao.base.resource.KtResource
import com.xiao.base.resource.PathResourceScanner

/**
 * [scanAndExecute]只用来刷新一次
 *
 * @author lix wang
 */
object ContextScanner : BeanRegistryAware {
    var annotatedKtResources = listOf<AnnotatedKtResource>()
    var ktResources = listOf<KtResource>()

    @Volatile
    private var refreshed = false

    /**
     * 这里有可能一个线程执行到同步代码块中，另一个在等待获取同步锁，因此需要在同步代码块中再次判断刷新状态，避免重复刷新。
     * 之所以不在方法上加同步锁，是因为这样每次都需要获取锁，通过先对状态的判断，可减少锁的使用。
     *
     * 这里需要先设置[refreshed]值为true再加载资源，因为类加载初始化阶段，会执行类类构造器<clinit>() 方法，
     * 如果代码中会调用这个方法来加载类资源，且即将加载类的中也有静态属性或静态代码块会调用这个方法，那么[refreshed]值就会一直是true，
     * 进而会导致多次加载。
     */
    fun scanAndExecute(basePackage: String) {
        if (refreshed) {
            return
        }
        synchronized(this) {
            if (refreshed) {
                return
            }
            refreshed = true
            ktResources = PathResourceScanner().scanByPackage(basePackage)
            annotatedKtResources = scan(ktResources)
            handleResourceProcessors(annotatedKtResources)
        }
    }

    private fun scan(resources: List<KtResource>): List<AnnotatedKtResource> {
        val annotationResources = mutableListOf<AnnotatedKtResource>()
        for (resource in resources) {
            filterResource(resource)?.let {
                annotationResources.add(it)
            }
        }
        return annotationResources
    }

    private fun handleResourceProcessors(annotatedKtResources: List<AnnotatedKtResource>) {
        handleContextInject(annotatedKtResources)?.let {
            handleComponentProcessor(it)
        }
    }

    private fun handleContextInject(annotatedKtResources: List<AnnotatedKtResource>): List<AnnotatedKtResource>? {
        val contextInjectResources = annotatedKtResources.filter { it.isAnnotated(ContextInject::class) }
        for (resource in contextInjectResources) {
            val contextInject = resource.annotationsByType(ContextInject::class).first()
            val handler = contextInject.handler.objectInstance ?: contextInject.handler.objectInstance
            handler?.let {
                it(resource)
            }
        }
        return annotatedKtResources.filterNot { contextInjectResources.contains(it) }
    }

    private fun handleComponentProcessor(annotatedKtResources: List<AnnotatedKtResource>) {
        val componentResources = annotatedKtResources.filter { it.isAnnotated(Component::class) }
        for (resource in componentResources) {
            val component = resource.annotationsByType(Component::class).first()
            val handler = component.handler.objectInstance ?: component.handler.objectInstance
            handler?.let {
                it(resource)
            }
        }
    }

    private fun filterResource(resource: KtResource): AnnotatedKtResource? {
        val annotations = resource.clazz.java.extractAnnotations()
        annotations.firstOrNull { it.annotationClass == AnnotationScan::class }?.let {
            val annotationScan = it as AnnotationScan
            val includeTypeFilter = annotationScan.includeTypeFilter.objectInstance
                ?: annotationScan.includeTypeFilter.objectInstance
            val excludeTypeFilter = annotationScan.excludeTypeFilter.objectInstance
                ?: annotationScan.excludeTypeFilter.objectInstance
            if (excludeTypeFilter != null && excludeTypeFilter(resource)) {
                return null
            }
            if (includeTypeFilter != null && includeTypeFilter(resource)) {
                return AnnotatedKtResource(resource, annotations)
            }
        }
        return null
    }
}