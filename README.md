[读书笔记](ReadingNotes.md)

* [1.项目简介](#1)
* [2.IO](#2)
* [3.线程](#3)
* [4.Kotlin协程](#4)
* [5.Spring事务](#5)
* [6.Redis](#6)
* [7.代码规范及测试](#7)

<h2 id="1">1.项目简介</h2>
&emsp;&emsp; 本项目中包含一些源码阅读笔记和技术书阅读笔记。本项目分为12个子模块，两个大类，一类单纯做了一些功能，
没有集成SpringBoot，另一类是基于SpringBoot实现了一些功能。本项目最终目标是全面运用kotlin协程，
包括：数据库交互、RPC调用、HttpServlet处理、Redis交互，凡是涉及到IO的地方都希望能使用Kotlin协程。

        本项目模块：
            kotlin-base：
                实现了一些基本的功能，如异步队列、基于堆外内存的基本数据类型数组、slfj4日志、
                基于ScheduledExecutorService的定时任务、自定义FastThreadLocal、CoroutineFastThreadLocal等。
                
            kotlin-beans：
                仿spring-beans实现了一个简单的对象管理及依赖注入的容器。
            
            kotlin-database：
                基于MyBatis简化了mapper interface和xml的扫描，实现了一个简单的TransactionHelper，
                可以实现一个transaction block中多个dataSource的事务提交和回滚，并且基于Junit5 Extension 
                和Flyway实现了测试时自动执行数据库迁移。
            
            kotlin-demo：
                kotlin模块使用demo。
            
            kotlin-http：
                不借助框架，仿OkHttp实现的一个Http client，用以加深Http理解。本Http Client支持Transfer-Encoding: gzip, chunked，
                支持HTTP/1.0、HTTP/1.1，支持Socket连接池，支持域名解析InetAddress复用，支持Kotlin协程，不支持IO多路复用。
                
            kotlin-log4j2: 
                本项目全局使用slf4j，因此把log4j2单独拆分为一个模块，可以根据需求替换成其他的日志框架。
            
            kotlin-metrics：
                利用kotlin-base中的定时任务及堆外内存基本数据类型数组，实现了简单的运行指标的间隔输出功能。
                
            kotlin-redis：
                基于Lettuce实现支持Kotlin协程的RedisService，IO多路复用使用Epoll或KQueue，并且实现基于Redis的分布式锁。
            
            kotlin-spring-boot-base：
                实现基于注解的环境变量配置，自定义以下Bean：AutoConfigurationImportFilter 处理自动配置、
                DefaultTestExecutionListenersPostProcessor处理SpringBoot Test中的TestExecutionListener。
                SpringBoot中很多AutoConfiguration并不符合我们的业务需求，例如：FlywayAutoConfiguration，
                我们可以根据业务需求，自定义AutoConfiguration。SpringBoot的EnableAutoConfiguration机制是核心。
                
            kotlin-spring-boot-mybatis：
                基于MyBatis和SpringBoot @Import，实现数据源自动配置，基于注解和Flyway实现数据自动迁移，
                改造测试环境MyBatis的XMLLanguageDriver及SqlSource，实现自动监控缺失的数据迁移。
                
            kotlin-spring-boot-servre-base：
                提供了WebServer支持Kotlin协程的能力，利用@RestControllerAdvice实现了全局异常处理，
                自定义RequestMappingHandlerAdapter、ServletInvocableHandlerMethod，实现支持Http Servlet协程执行。
                
            kotlin-spring-boot-server-undertow：
                利用全局线程池替换掉Undertow默认的线程池，用来提升性能。自定义WebServerFactoryCustomizer
                和UndertowRootInitialHttpHandler，实现利用Kotlin协程来执行请求。

### Kotlin协程是什么？解决了什么问题？
&emsp;&emsp; Kotlin协程基于状态机的原理实现，将协程挂起恢复后要执行的逻辑，都封装到了resumeWith方法中，根据不同的状态执行不同的逻辑。
Java异步导致的问题在于：异步执行一个方法，后续等待获取结果时，通常使用Future.get()，这个方法会使CPU自旋等待异步任务结束，
这会导致CPU执行效率会降低。如果想要解决这个问题，可以每一步都采用callback回调，但Java中全局使用callback回调，会导致代码非常复杂。
Kotlin协程通过挂起和恢复简化了回调的复杂度，并且Kotlin是完全非阻塞的，不会导致CPU自旋，从而来提升CPU效率。

<h2 id="2">2.IO</h2>
&emsp;&emsp; Redis客户端Lettuce、Undertow、HttpClient都采用了IO多路复用，Selector.select()会去遍历
描述符集，但是如果描述符很多，那么每次遍历开销很大，因此我们在Server中通常使用KQueue或Epoll。
Netty使用时我们应该优先选择KQueueEventLoopGroup或EpollEventLoopGroup。

```kotlin
fun getIoEventLoopGroup(ioThreads: Int): EventLoopGroup {
    var group: EventLoopGroup? = null
    if (PlatformDependent.isOsx()) {
        if (KQueue.isAvailable()) {
            group = KQueueEventLoopGroup(
                ioThreads,
                NamedThreadFactory("netty-kqueue-thread")
            )
        }
    } else {
        if (!PlatformDependent.isWindows()) {
            if (Epoll.isAvailable()) {
                group = EpollEventLoopGroup(
                    ioThreads,
                    NamedThreadFactory("netty-epoll-thread")
                )
            }
        }
    }
    return group ?: NioEventLoopGroup(
        ioThreads,
        NamedThreadFactory("netty-nio-thread")
    )
}
```

### 堆内存及堆外内存的使用
&emsp;&emsp; 堆外内存某些情况下可以减少内核空间与用户进程空间之间的数据交换带来的CPU拷贝和上下文切换，
而且堆外空间可以减轻JVM垃圾回收的压力，但是堆外空间需要手动释放，如果没有管理好堆外空间，
可能会引起内存泄漏。堆外空间使用建议使用Netty的ByteBuf，Netty能够先分配Chunk，
然后从Chunk中再次分配具体大小，这样可以避免内存浪费，而且Netty使用内存池化，
可以避免内存重复分配及释放。对于堆内内存，可以直接放到FastThreadLocal中复用。

我们可以利用堆外内存来创建基本数据类型数组：
```kotlin
abstract class DirectArray<T>(
    val capacity: Int,
    val elementBytes: Int
) : AutoCloseable {
    abstract fun get(index: Int): T

    abstract fun set(index: Int, value: T)

    protected fun getOffset(index: Int): Long {
        return index.toLong() * elementBytes
    }
}

```

基于Unsafe的堆外基本数据类型数组：
```kotlin
abstract class UnsafeDirectArray<T>(
    capacity: Int,
    elementBytes: Int
) : DirectArray<T>(capacity, elementBytes) {
    val address = UnsafeUtils.UNSAFE.allocateMemory(getOffset(capacity))

    override fun close() {
        UnsafeUtils.UNSAFE.freeMemory(address)
    }
}
```
    
基于Netty的堆外基本数据类型数组：
```kotlin
abstract class NettyDirectArray<T>(
    capacity: Int,
    elementSize: Int
) : DirectArray<T>(capacity, elementSize) {
    private val byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(checkCapacity())

    override fun close() {
        ReferenceCountUtil.release(byteBuf)
    }

    private fun checkCapacity(): Int {
        val totalCapacity = getOffset(capacity)
        if (totalCapacity > Int.MAX_VALUE) {
            throw IllegalArgumentException(
                "Out of bounds of ${Int.MAX_VALUE}, capacity: $capacity, elementSize: $elementBytes."
            )
        }
        return totalCapacity.toInt()
    }
}
```

<h2 id="3">3.线程</h2>
&emsp;&emsp; 多线程能够充分发挥CPU的执行效率，但是线程过多反而会导致线程切换开销过大。线程池大小的设置，
主要在于判断应用是IO密集型还是计算密集型。例如是CPU密集型，那么线程池大小可以直接设置为CPU数+1。对于IO密集型，
需要判断线程的执行效率，比如一个线程执行过程中只有1/4时间为CPU执行时间，那么线程池大小应该设置为4CPU数。

### 守护线程
&emsp;&emsp; 线程分为守护线程和非守护线程，如果只剩下守护线程，那么JMV会立刻停止。因此在选择时，
要考虑是否希望线程任务执行完，还需要考虑线程中的任务死循环导致线程任务无法结束怎么办。

### ThreadLocal
&emsp;&emsp; ThreadLocal使用很频繁，但有弊端：1，每次通过Hash值读写，可能导致hash冲突。
2，key使用弱引用，导致可能有些value对应的key变为null。3，虽然每次访问会清理null，
但如果长时间不访问，且对应的value占了较大内存，可能导致内存泄漏。Netty的FastThreadLocal，使用自增的index，
避免动态扩容，避免了hash冲突，且index访问更快，但缺点在于占用空间更多，是典型的空间换时间，且只支持FastThreadLocalThread。

自定义的FastThreadLocal，放宽了支持的线程种类：
```kotlin
class KtFastThreadLocal<T> {
    private var threadLocal: ThreadLocal<T>? = null
    private var fastThreadLocal: FastThreadLocal<T>? = null
    private val index = nextIndex()

    @Suppress("UNCHECKED_CAST")
    fun get(): T? {
        return when (val thread = Thread.currentThread()) {
            is ThreadLocalValueProvider -> {
                if (thread.indexedVariables != null && index < thread.indexedVariables!!.size) {
                    thread.indexedVariables!![index] as T?
                } else {
                    null
                }
            }
            is FastThreadLocalThread -> fastThreadLocal?.get()
            else -> {
                threadLocal?.get()
            }
        }
    }

    fun fetch(func: () -> T): T {
        return get() ?: func().also { set(it) }
    }

    fun set(value: T?) {
        when (val thread = Thread.currentThread()) {
            is ThreadLocalValueProvider -> {
                if (thread.indexedVariables == null) {
                    // initial
                    thread.indexedVariables = arrayOfNulls(tableSizeFor(index))
                    thread.indexedVariables!![index] = value
                } else {
                    // out of range, do expand.
                    if (index >= thread.indexedVariables!!.size) {
                        thread.indexedVariables = expandIndexVariables(thread.indexedVariables!!, tableSizeFor(index))
                    }
                    thread.indexedVariables!![index] = value
                }
            }
            is FastThreadLocalThread -> {
                if (fastThreadLocal == null) {
                    synchronized(this) {
                        if (fastThreadLocal == null) {
                            fastThreadLocal = FastThreadLocal<T>()
                        }
                    }
                }
                fastThreadLocal!!.set(value)
            }
            else -> {
                if (threadLocal == null) {
                    synchronized(this) {
                        if (threadLocal == null) {
                            threadLocal = ThreadLocal<T>()
                        }
                    }
                }
                threadLocal!!.set(value)
            }
        }
    }

    fun reset() {
        get()?.let {
            if (it is AutoCloseable) {
                it.close()
            }
            set(null)
        }
    }

    private fun expandIndexVariables(src: Array<Any?>, size: Int): Array<Any?> {
        val dest = arrayOfNulls<Any?>(size)
        System.arraycopy(src, 0, dest, 0, src.size)
        return dest
    }

    private fun tableSizeFor(index: Int): Int {
        var size = index
        size = size or (size ushr 1)
        size = size or (size ushr 2)
        size = size or (size ushr 4)
        size = size or (size ushr 8)
        size = size or (size ushr 16)

        size = when {
            size < 0 -> {
                1
            }
            size >= Int.MAX_VALUE -> {
                Int.MAX_VALUE
            }
            else -> {
                size + 1
            }
        }
        return size.coerceAtLeast(16)
    }

    companion object {
        private val indexGenerator = AtomicInteger(0)
        private fun nextIndex() = indexGenerator.getAndIncrement()
    }
}
```

KtFastThreadLocal在协程中的使用：
```kotlin
internal data class KtFastThreadLocalKey(
    private val threadLocal: KtFastThreadLocal<*>
) : CoroutineContext.Key<CoroutineThreadLocal<*>>

class CoroutineThreadLocal<T>(
    private val threadLocal: KtFastThreadLocal<T>,
    private val value: T,
) : ThreadContextElement<Unit> {
    override val key: CoroutineContext.Key<CoroutineThreadLocal<*>> = KtFastThreadLocalKey(threadLocal)

    override fun restoreThreadContext(context: CoroutineContext, oldState: Unit) {
        threadLocal.reset()
    }

    override fun updateThreadContext(context: CoroutineContext) {
        threadLocal.set(value)
    }
}
```

<h2 id="4">4.Kotlin协程</h2>
&emsp;&emsp; 协程是非阻塞的，我们可以在会发生CPU自旋的地方使用协程。利用异步回调的思想，
可以很简单的将阻塞线程转化为非阻塞的Kotlin协程。可以利用CompletableDeferred将非suspend lambda块转化为协程执行。

```kotlin
@Suppress("UNCHECKED_CAST")
fun <T> CoroutineScope.deferred(block: () -> T): SafeDeferred<T> {
    val deferred = CompletableDeferred<T>()
    val result = SafeCompletableDeferred(deferred)
    val job = launch {
        CoroutineCompletableCallback(block, null, deferred as CompletableDeferred<Any?>).run()
    }
    result.putJob(job)
    return result
}
```

改造Undertow HttpHandler及RequestMappingHandlerAdapter，可以简单的实现Http请求的协程化，
由于这里使用了runBlocking，因此还是会有部分阻塞，可以参考DispatcherServlet执行流程，
在WebServer处理链最顶层实现一个类DispatcherServlet的协程化处理模型，进而实现全局协程化。

Undertow协程简单实现：
```kotlin
class UndertowCoroutineInitialHttpHandler(
    applicationContext: ApplicationContext,
    httpHandler: HttpHandler,
    private val ktServerArgs: KtServerArgs
) : UndertowInitialHttpHandler(applicationContext, httpHandler) {
    private val defaultExecutor = Executor { runnable ->
        ktServerArgs.coroutineScope!!.launch(
            createCoroutineThreadLocal()
        ) {
            runnable.run()
        }
    }

    // TODO improve handleRequest by replace exchange dispatchTask.
    override fun handleRequest(exchange: HttpServerExchange) {
        // Use global executor instead of undertow taskPool.
        prepareAttachment(exchange)
        exchange.dispatchExecutor = defaultExecutor
        httpHandler.handleRequest(exchange)
    }

    private fun createCoroutineThreadLocal(): CoroutineThreadLocal<UndertowRequestInfo> {
        val requestInfo = UndertowRequestInfo()
            .apply {
                requestStartMills = System.currentTimeMillis()
                requestUuid = UUID.randomUUID().toString()
            }
        return CoroutineThreadLocal(
            threadLocal,
            requestInfo
        ).apply {
            requestInfo.threadContextElement = this
        }
    }
}
```

Servlet协程化简单实现：
```kotlin
class CoroutineServletInvocableHandlerMethod(
    handlerMethod: HandlerMethod
) : ServletInvocableHandlerMethod(handlerMethod) {
    internal var ktServerArgs: KtServerArgs? = null
    private val isSuspendMethod = KotlinDetector.isSuspendingFunction(bridgedMethod)

    override fun invokeForRequest(
        request: NativeWebRequest,
        mavContainer: ModelAndViewContainer?,
        vararg providedArgs: Any?
    ): Any? {
        return if (isSuspendMethod) {
            // TODO use coroutine whole request process.
            runBlocking(getCoroutineContext()) {
                val args = getMethodArgumentValues(request, mavContainer, *providedArgs)
                invokeMethodSuspend(*args)
            }
        } else {
            val args = getMethodArgumentValues(request, mavContainer, *providedArgs)
            invokeMethod(*args)
        }
    }
}
```

协程Controller的用法：
```kotlin
//1，使用自定义的@CoroutineSpringBootApplication注解启动服务
@CoroutineSpringBootApplication
class UndertowServerApplication

//2，编写Controller
@RestController
@RequestMapping("/api/v1/demo")
class DemoController {
    @PostMapping("/printInputSuspend")
    suspend fun printInputSuspend(@RequestBody input: String): String {
        return input
    }
}

//3，测试验证
@SpringBootTest(
    classes = [UndertowServerApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class DemoControllerTest : KtSpringTestBase() {
    @Autowired
    lateinit var envInfoProvider: EnvInfoProvider
    
    @Test
    fun `test post request suspend`() {
        val result = RestTemplate().exchange(
            "http://localhost:${envInfoProvider.port()}/api/v1/demo/printInputSuspend",
            HttpMethod.POST,
            HttpEntity("hello world"),
            String::class.java,
            mapOf<String, String>()
        )
        Assertions.assertEquals(result.body, "hello world")
    }
}
```
<h2 id="5">5.Spring事务</h2>
&emsp;&emsp; Spring事务基于AOP实现，Spring Aop通过两种代理方式实现：JDK动态代理和CGLIB动态代理。
JDK动态代理是通过反射生成目标代理接口的匿名实现类，而CGLIB通过继承，使用字节码增强，为目标代理类生成
代理子类。AOP只有基础功能如日志、鉴权才可能会用到，但是AOP使用复杂，因此推荐使用注解加拦截的方式替代AOP，
例如有些方法需要鉴权，有些不用，那么可以在@Controller接口方法上添加鉴权注解，
然后利用Spring的HandlerInterceptor拦截，这样更直观更便捷。

@Transactional方法的执行逻辑如下，本质是通过生成的MethodInterceptor Bean，环绕事务方法执行：
```java
public Object invoke(MethodInvocation invocation) throws Throwable {
        // Work out the target class: may be {@code null}.
        // The TransactionAttributeSource should be passed the target class
        // as well as the method, which may be from an interface.
        Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);

        // Adapt to TransactionAspectSupport's invokeWithinTransaction...
        return invokeWithinTransaction(invocation.getMethod(), targetClass, new CoroutinesInvocationCallback() {
                @Override
                @Nullable
                public Object proceedWithInvocation() throws Throwable {
                        return invocation.proceed();
                }
                @Override
                public Object getTarget() {
                        return invocation.getThis();
                }
                @Override
                public Object[] getArguments() {
                        return invocation.getArguments();
                }
        });
}
```
Spring @Transactional的缺点在于：在代理模式下，仅拦截代理传入的外部方法调用，因此对象内部调用@Transactional并不会生效，
了解了@Transactional工作机制后，我们可以自己写一个TransactionService，在DataSource创建时注入Bean，
这样可以以方法调用的形式来使用，使用更方便。
```kotlin
// 自定义Spring TransactionService实现
class SpringPlatformTransactionService(
    private val transactionManager: PlatformTransactionManager
) : TransactionService {
    override fun <T> runInTransaction(block: () -> T): T {
        return runInTransaction(DEFAULT_TRANSACTION_WRAPPER, block)
    }

    override fun <T> runInTransaction(wrapper: TransactionWrapper, block: () -> T): T {
        val transactionAttribute = parseTransactionAttribute(wrapper)
        val transactionStatus = transactionManager.getTransaction(transactionAttribute)
        val value: T
        try {
            value = block()
        } catch (ex: Throwable) {
            completeTransactionAfterThrowing(transactionStatus, transactionAttribute, transactionManager, ex)
            throw ex
        }
        transactionManager.commit(transactionStatus)
        return value
    }
}

// TransactionService使用
@Service
class TransactionDemoService(
    private val userMapper: UserMapper,
    @Qualifier(DemoDatabase.transactionServiceName)
    private val transactionService: TransactionService
) {
    @Transactional(rollbackFor = [Exception::class])
    fun updateUsernameInTransaction(id: Long, username: String) {
        userMapper.updateUsernameById(id, username)
        throw RuntimeException("throw exception")
    }

    fun rollbackOnTransactionService(id: Long, username: String) {
        transactionService.runInTransaction {
            userMapper.updateUsernameById(id, username)
            throw RuntimeException("throw exception")
        }
    }
}

// TransactionService测试
@Test
fun `test rollback on transaction service`() {
    Assertions.assertEquals(userMapper.selectById(1).username, "user_1")
    assertThrows<HttpServerErrorException.InternalServerError> {
        RestTemplate().exchange(
            "http://localhost:${envInfoProvider.port()}/api/v1/demo/transaction/testTransactionServiceRollback" +
                "?id=1&username=name123",
            HttpMethod.POST,
            HttpEntity.EMPTY,
            Unit::class.java,
            mapOf<String, String>()
        )
    }
    Assertions.assertEquals(userMapper.selectById(1).username, "user_1")
}
```

<h2 id="6">6.Redis</h2>
&emsp;&emsp; Redis客户端我们通常采用Lettuce，Lettuce使用Netty，支持同步、异步、响应式模式。
多个线程可以共享一个连接实例，不必担心多线程并发问题。Redis使用时，我们可以采用主从模式、哨兵模式、
集群模式。对于数据量比较大的情况下，优先使用集群模式，集群模式扩容更方便。

Lettuce集群使用：1，先定义一个基础的抽象代理类，可以缓存连接实例。
```kotlin
abstract class AbstractRedisProxy<T : StatefulConnection<String, String>>(
    private val redisClient: AbstractRedisClient
) : InvocationHandler {
    @Volatile private var connection: T? = null

    fun getConnection(): T {
        return if (isConnectionValid()) {
            connection!!
        } else {
            val oldConnection = connection
            synchronized(redisClient) {
                if (isConnectionValid() && oldConnection != connection) {
                    return connection!!
                }
                redisClient.defaultTimeout = RedisHelper.REDIS_TIMEOUT
                connection = connect()
                connection!!
            }
        }
    }

    protected abstract fun connect(): T

    private fun isConnectionValid(): Boolean {
        return connection != null && connection!!.isOpen
    }
}
```

2，定义一个抽象的ClusterClient代理对象，用来存放Redis Client实例。
```kotlin
abstract class BaseRedisClusterProxy(
    private val redisClient: RedisClusterClient
) : AbstractRedisProxy<StatefulRedisClusterConnection<String, String>>(redisClient) {
    override fun connect(): StatefulRedisClusterConnection<String, String> {
        return redisClient.connect()
    }
}
```

3，定义一个具体的Redis Client代理对象
```kotlin
class RedisClusterAsyncServiceProxy(redisClient: RedisClusterClient) : BaseRedisClusterProxy(redisClient) {
    override fun invoke(proxy: Any?, method: Method, args: Array<Any?>?): Any? {
        try {
            return ProxyUtils.invoke(getConnection().async(), method, args)
        } catch (e: Exception) {
            log.error("Redis cluster async method: ${method.name} failed, ${e.message}.", e)
            throw e
        }
    }

    @KtLogger(LoggerType.REDIS)
    companion object : Logging()
}
```

4，Redis集群异步方法使用
```kotlin
@JvmStatic
fun getRedisClusterAsyncService(urls: Set<String>): RedisClusterAsyncService {
    val redisURIs = urls.map { RedisURI.create(it) }
    return Proxy.newProxyInstance(
        RedisClusterAsyncService::class.java.classLoader,
        arrayOf(RedisClusterAsyncService::class.java),
        RedisClusterAsyncServiceProxy(RedisClusterClient.create(clientResources, redisURIs))
    ) as RedisClusterAsyncService
}
```

5，RedisFuture转Kotlin协程Deferred方法。
```kotlin
// add suspend modifier, because we expect this method used in coroutine.
@Suppress("UNCHECKED_CAST", "RedundantSuspendModifier")
suspend fun <T : Any?> RedisFuture<T>.suspend(): SafeDeferred<T> {
    val deferred = CompletableDeferred<T>()
    val result = SafeCompletableDeferred(deferred)
    whenComplete { value, throwable ->
        throwable?.also {
            throw it
        }
        CoroutineCompletableCallback({ value }, null, deferred as CompletableDeferred<Any?>).run()
    }
    return result
}
```

6，Redis基于Kotlin协程的集群异步方法使用及测试
```kotlin
class RedisClusterClientTest : KtTestBase() {
    @Test
    fun `test redis cluster coroutine commands`() {
        runBlocking {
            val clusterAsyncCommands = RedisHelper.getRedisClusterAsyncService(CLUSTER_REDIS_URLS)
            clusterAsyncCommands.del(KEY).suspend().awaitNanos()
            clusterAsyncCommands.set(KEY, VALUE).suspend().awaitNanos()
            Assertions.assertEquals(clusterAsyncCommands.get(KEY).suspend().awaitNanos(), VALUE)
        }
    }
    
    companion object {
        private const val KEY = "hello"
        private const val VALUE = "world"
        private val CLUSTER_REDIS_URLS = setOf(
            "redis://localhost:6381",
            "redis://localhost:6382",
            "redis://localhost:6383",
            "redis://localhost:6384",
            "redis://localhost:6385",
            "redis://localhost:6386"
        )
    }
}
````

<h2 id="7">7.代码规范及测试</h2>
&emsp;&emsp; 本项目使用ktlint来进行代码格式校验及自动纠正。定义gradle ktlintCheck 任务来校验kotlin代码格式，并将ktlintCheck任务放置在
verification check任务之前，那么在执行gradle build之前就会先执行ktlintCheck。还定义了一个 gradle ktlintFormat 任务，这个任务是单独的，
执行这个任务可以根据代码规范，自动进行格式纠正。

```groovy
task ktlintCheck(type: JavaExec, group: "verification") {
    description = "Gradle check kotlin verification."
    classpath = configurations.ktlint
    main = "com.pinterest.ktlint.Main"
    args "src/**/*.kt"
}

check.dependsOn ktlintCheck

task ktlintFormat(type: JavaExec, group: "formatting") {
    description = "Gradle check kotlin formatting."
    classpath = configurations.ktlint
    main = "com.pinterest.ktlint.Main"
    args "-F", "src/**/*.kt"
}
```

### 测试
&emsp;&emsp; 本项目使用Github Action配合Junit5执行测试。单测很重要，通过单测能够发现bug，
更重要的是当需要重构你的代码或者业务变更时，单测在确保逻辑正确方面能发挥很大的作用。
通常建议在开发完功能后，立即完成所有逻辑分支的单测编写。在我们实际开发过程中，鉴于时间关系，
通常直接以API为切入点进行单测的编写，对于复杂的方法或者Mapper这种API单测可能覆盖不到的，
才会逐个进行测试。对于紧急需求，可以先不写单测，但需要有个时间节点来补上。本项目单测覆盖率100%。
    
```groovy
name: Build CI

# Controls when the action will run. 
on: [push, pull_request]

# Run jobs automatically.
# A workflow run is made up of one or more jobs that can run sequentially or in parallel
jobs:
# This workflow contains a single job called "build"
build:
# The type of runner that the job will run on
runs-on: ubuntu-latest

# Service containers to run with the job
services:
  # mysql service
  mysql:
    image: mysql:5.7
    env:
      MYSQL_ROOT_PASSWORD: 123456
    ports:
      - "3306:3306"
    options: >-
      --health-cmd "mysqladmin ping"
      --health-interval 10s
      --health-timeout 5s
      --health-retries 3

  # redis service
  redis:
    # Docker Hub image
    image: redis
    ports:
      - "6379:6379"
    # Set health checks to wait until redis has started
    options: >-
      --health-cmd "redis-cli ping"
      --health-interval 10s
      --health-timeout 5s
      --health-retries 5

# Steps represent a sequence of tasks that will be executed as part of the job
steps:
  # Checks-out your repository under $GITHUB_WORKSPACE, so your job can access it
  - uses: actions/checkout@v2

  # Set up jdk version
  - uses: actions/setup-java@v1
    with:
      java-version: 1.8

  # use dependencies cache to speed up
  - uses: actions/cache@v2
    with:
      path: |
        ~/.gradle/caches
        ~/.gradle/wrapper
      key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
      restore-keys: |
        ${{ runner.os }}-gradle-

  # Runs build
  - run: |
      echo "Build start..."
      ./gradlew build
      echo "Bulid finished."

  # cleanup gradle cache
  # Remove some files from the Gradle cache, so they aren't cached by GitHub Actions.
  # Restoring these files from a GitHub Actions cache might cause problems for future builds.
  - run: |
      rm -f ~/.gradle/caches/modules-2/modules-2.lock
      rm -f ~/.gradle/caches/modules-2/gc.properties
```
