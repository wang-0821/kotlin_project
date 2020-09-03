package com.xiao.rpc.context

import com.xiao.base.annotation.AnnotatedKtResource
import com.xiao.base.context.BeanHelper
import com.xiao.base.context.BeanRegistry
import com.xiao.base.context.Context
import com.xiao.base.context.ContextAware
import com.xiao.base.context.ContextScanner
import com.xiao.rpc.Client
import com.xiao.rpc.RunningState
import com.xiao.rpc.annotation.AutoClean
import com.xiao.rpc.annotation.ClientContext
import com.xiao.rpc.cleaner.Cleaner
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

/**
 * 不同的[ClientContextPool]的key应该使用不同的值，通过这种方式能够实现[Client]缓存区的隔离。
 *
 * @author lix wang
 */
abstract class ClientContextPool(override val key: Context.Key<*>) : ContextAware {
    private val contextClientConfig = mutableMapOf<Context.Key<*>, ClientContextConfig>()
    private val defaultClientContextConfig = ClientContextConfig(16, 60, TimeUnit.SECONDS)
    private val clientContextContainer = mutableMapOf<Context.Key<*>, Context>()
    private val cleanerContainer = mutableListOf<Cleaner>()
    private var beanRegistry: BeanRegistry? = null
    private val minCleanupDuration = 5000L
    private var cleanUpDuration: Long? = null
    private var state = RunningState()

    private val log = LoggerFactory.getLogger(this::class.java)

    fun clientConfig(key: Context.Key<*>, config: ClientContextConfig) {
        contextClientConfig[key] = config
    }

    fun clientConfig(key: Context.Key<*>): ClientContextConfig {
        return contextClientConfig[key] ?: defaultClientContextConfig
    }

    fun start() {
        ContextScanner.scanAndExecute(Client.BASE_SCAN_PACKAGE)
        val clientContextClasses = ContextScanner.annotatedKtResources.filter { it.isAnnotated(ClientContext::class) }
        val contextCleanerClasses = ContextScanner.annotatedKtResources.filter { it.isAnnotated(AutoClean::class) }
        registerClientContexts(clientContextClasses)
        registerCleaners(contextCleanerClasses)
        startClean()
    }

    fun stop() {
        synchronized(state) {
            state.updateState(RunningState.TERMINATE)
        }
    }

    fun getContext(key: Context.Key<*>): Context? {
        return clientContextContainer[key]
    }

    private fun startClean() {
        synchronized(state) {
            val thread = Thread(Runnable {
                cleanupRunnable()
            })
            thread.isDaemon = true
            thread.start()
            state.updateState(RunningState.RUNNING)
        }
    }

    private fun cleanupRunnable() {
        val sleepDuration = cleanUpDuration ?: minCleanupDuration
        while (true) {
            if (state.state() >= RunningState.TERMINATE) {
                return
            }
            cleanerContainer.forEach { cleaner ->
                clientContextContainer.values.forEach {
                    try {
                        cleaner.cleanup(it)
                    } catch (e: Exception) {
                        log.error(
                            "Cleaner ${cleaner.javaClass.simpleName} cleanup ${it.javaClass.simpleName} failed." +
                                    "${e.message}",
                            e
                        )
                    }
                }
            }
            Thread.sleep(sleepDuration)
        }
    }

    private fun registerClientContexts(contexts: List<AnnotatedKtResource>) {
        for (context in contexts) {
            val clientContext = constructClientContext<Context>(context.resource.clazz.java)
            clientContext?.let {
                clientContextContainer[it.key] = it
                computeCleanupDuration(it.key)
            }
        }
    }

    private fun computeCleanupDuration(key: Context.Key<*>) {
        val config = clientConfig(key)
        val mills = config.timeUnit.toMillis(config.idleTimeout)
        if (mills > minCleanupDuration && (cleanUpDuration == null || mills < cleanUpDuration!!)) {
            cleanUpDuration = mills
        }
    }

    private fun registerCleaners(cleaners : List<AnnotatedKtResource>) {
        for (cleaner in cleaners) {
            val cleanerInstance = BeanHelper.newInstance<Cleaner>(cleaner.resource.clazz.java)
            cleanerInstance.let {
                cleanerContainer.add(it)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <E : Any> constructClientContext(clazz: Class<*>): E? {
        val constructors = clazz.constructors
        check(constructors.size == 1) {
            "${clazz.simpleName} should have only one constructor."
        }
        val parameterTypes = constructors[0].parameterTypes
        var key: Context.Key<*>? = null
        if (Context::class.java.isAssignableFrom(clazz)) {
            val contextKeys = clazz.declaredFields.filter { Context.Key::class.java.isAssignableFrom(it.type) }
            check(contextKeys.size == 1) {
                "${clazz.simpleName} must contain a ${Context.Key::class.java.simpleName} field."
            }
            key = contextKeys[0].get(clazz) as? Context.Key<*>
        }
        if (key == null) {
            return null
        }

        // new instance
        val parameters = arrayOfNulls<Any>(parameterTypes.size)
        val beanRegistry = beanRegistry()
        for (i in parameterTypes.indices) {
            if (parameterTypes[i] == ClientContextConfig::class.java) {
                parameters[i] = clientConfig(key)
            } else {
                parameters[i] = beanRegistry?.getByType(parameterTypes[i])
            }
            check(parameters[i] != null) {
                "${clazz.simpleName} constructor parameterType ${parameterTypes[i].simpleName} can't find value."
            }
        }
        return constructors[0].newInstance(*parameters) as E?
    }

    private fun beanRegistry(): BeanRegistry? {
        if (beanRegistry == null) {
            beanRegistry = get(BeanRegistry.Key)
        }
        return beanRegistry
    }
}