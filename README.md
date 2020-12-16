[读书笔记](ReadingNotes.md)

## 项目简介
* [1.日志](#1)
* [2.对象容器](#2)
* [3.资源扫描与处理](#3)
* [4.Http](#4)
* [5.数据源](#5)
* [6.事务](#6)
* [7.测试](#7)

<h2 id="1">1.日志</h2>
&emsp;&emsp; 本项目使用Slf4j门面模式来使用日志。使用slf4j，可以根据具体的需求，自主选择具体的日志框架。本项目中使用log4j2，也可以exclude掉，
用其他日志框架替换掉。并且可以根据@Log注解，来选择使用具体的Logger，避免无意义的多个相似Logger对象的创建。
    
    private fun logger(): Logger {
            val loggerName = loggerName()
            val logger = LoggerFactory.getLogger(loggerName)
            if (logger == NOPLogger.NOP_LOGGER) {
                Util.report("There is no available logger named {$loggerName}, please implement it.")
            }
            return logger
        }
    
        private fun loggerName(): String {
            val loggerAnnotation = this::class.java.getAnnotation(Log::class.java)
            return if (loggerAnnotation != null && loggerAnnotation.value.isNotBlank()) {
                loggerAnnotation.value
            } else {
                this::class.java.name
            }
        }

<h2 id="2">2.对象容器</h2>
&emsp;&emsp; 实现了一个简易的对象容器，用于管理运行时对象，类似Spring。与Spring不同的是，本项目中对象容器使用Key来进行隔离，
使得同Key的对象可以用来放在同一个子容器中，不同的Key来进行隔离，不同Key的子容器互不关联。Context作为外层容器，包含多个子容器。

<br>
&emsp;&emsp; kotlin-rpc模块中，ClientContextPool是一个Context，表示一个http客户端上下文，这个上下文可以注入到Context.container中，
而ConnectionContext和RouteContext，都是上下文中的元素，而且上下文中还可以有多种元素，那么就可以把这些元素都声明为Context，通过这种抽象的
方式，就可以把所有的元素都放在上下文元素容器中，并且由于不同的Context有不同的Key，可以很容易的对各种元素进行区分。

    // ClientContextPool中，包含多种类型的Context元素。
    private val clientContextContainer = mutableMapOf<Context.Key<*>, Context>()
    
    // 可以将元素声明成Context，既能进行抽象也能进行区分。
    class RouteContext(private val contextConfig: ClientContextConfig) : Context, Cleaner {
        companion object Key : Context.Key<RouteContext>, Logging()
        override val key: Context.Key<RouteContext>
            get() = Key
    
        private var routePool = ConcurrentHashMap<Address, MutableList<Route>>()
        ......

&emsp;&emsp; 可以使用BeanRegistry容器里面的对象，实现依赖注入。只需要在类上加@Component注解即可。
    
    // 对@Component注解的类进行Bean的生成和注入。
    object ComponentResourceHandler : AnnotationHandler, BeanRegistryAware {
        override fun invoke(p1: AnnotatedKtResource) {
            val component = p1.annotationsByType(Component::class).first()
            val obj = BeanHelper.newInstance<Any>(p1.classResource.clazz.java)
            getByType(p1.classResource.clazz.java) ?: kotlin.run {
                if (component.value.isBlank()) {
                    registerSingleton(obj)
                } else {
                    registerSingleton(component.value, obj)
                }
            }
        }
    }
    
    // 根据Class，即可通过构造器的解析，进行对象的创建和构造函数的填充。构造参数来自于BeanRegistry子容器。
    @Suppress("UNCHECKED_CAST")
        fun <E> newInstance(clazz: Class<*>): E {
            val constructors = clazz.constructors
            check(constructors.size == 1) {
                "${clazz.simpleName} should have only one constructor."
            }
            val constructor = constructors[0]
            val parameterTypes = constructor.parameterTypes
            val parameters = arrayOfNulls<Any>(parameterTypes.size)
            for (i in parameterTypes.indices) {
                parameters[i] = getByType(parameterTypes[i])
                check(parameters[i] != null) {
                    "${clazz.simpleName} constructor parameterType ${parameterTypes[i].simpleName} can't find value."
                }
            }
            return constructor.newInstance(*parameters) as E
        }
        
<h2 id="3">3.资源扫描与处理</h2>
&emsp;&emsp; 可以通过指定包名，ClassLoader，ResourceMatcher的方式来扫描资源。

    // 在扫描MyBatis xml mapper文件时，只需要指定路径和ResourceMatcher，使用默认的ClassLoader即可扫出所有的xml文件。
    PathResourceScanner.scanFileResourcesByPackage(
        path,
        object : ResourceMatcher {
            override fun matchingDirectory(file: File): Boolean {
                return true
            }

            override fun matchingFile(file: File): Boolean {
                return file.name.endsWith(".xml")
            }
        }
    )
    
### 注解类扫描与处理
&emsp;&emsp; ContextScanner.scanAnnotatedResources(basePackage: String)可以通过包名，扫描出所有被@AnnotationScan注解的Class。
ContextScanner.processAnnotatedResources(annotatedKtResources: List<AnnotatedKtResource>)会处理所有的注解类。

    @AnnotationScan     被其注解的类，会被扫描出来。
    @Component          被其注解的类，会生成实例对象，并以单例的方式注入到BeanRegistry
    @ContextInject      被其注解的类，会生成Context上下文实例对象，并注册到Context.container中。
    @Log                被其注解的Logging对象，会根据注解获取对应名称的Logger对象。
    
<h2 id="4">4.Http</h2>
&emsp;&emsp; kotlin-rpc模块实现了简易的Http客户端，支持http、https、多路复用http、http dns缓存、http connection缓存、
io字节数组及字符数组复用。该客户端可以使用 chunked transfer-encoding方式，可以使用 gzip content-encoding方式。
而且提供了基于线程的异步请求方式和基于kotlin协程的异步请求方式。

### http缓存
&emsp;&emsp; http dns域名解析会消耗大量时间，http刚建立连接时，tcp慢启动会导致传输速度很慢，因此持久连接会加快字节流的传输速度。
采用RouteContext、ConnectionContext提高同一server的重复请求性能。

### io字节数组字符数组复用
&emsp;&emsp; 一次http请求中，会涉及多次header、content的读取，每次都生成字节数组和字符数组，会消耗一定的资源。
因此可以通过ThreadLocal实现资源复用。io时，我们需要先读取字节集合，然后再转化为字符集合。如果把解析后的字符内容都放在一个字符数组中，
那么随着解析内容的增多，需要不断的对数组进行扩容并拷贝，这样会消耗资源，因此我们可以将每次解析后的内容放入多个字符数组，
最后再对多个字符数组进行合并，这样可以避免大量的字符数组内容拷贝。

    // readLine时，复用CharArray和ByteArray
    fun readPlainTextLine(
        inputStream: InputStream,
        charset: Charset = Charsets.UTF_8
    ): String {
        val byteArray = getByteArray()
        val charArray = getCharArray()
        val result = readLine(inputStream, byteArray, charArray, charset)
        cacheByteArray(byteArray)
        cacheCharArray(charArray)
        return result
    }

    // PooledBuffer中包含多个PooledCharArrayBuffer，每个PooledCharArrayBuffer中存储CharArray。
    class PooledBuffer {
        private val buffers = mutableListOf<PooledCharArrayBuffer>()
        private var currentBuffer: PooledCharArrayBuffer
        private val pooledCharArrayBuffer = object : RpcContextKey<PooledCharArrayBuffer> {}
        private val maxCacheSize = 32
    
        constructor() {
            this.currentBuffer = PooledCharArrayBuffer(IoHelper.BUFFER_SIZE)
            buffers.add(currentBuffer)
        }
        ......
        
    // 在PooledBuffer asString时，会将所有的PooledCharArrayBuffer合并成一个CharArray。
    fun asString(): String {
        val count = (buffers.size - 1) * IoHelper.BUFFER_SIZE + currentBuffer.index
        val charArray = CharArray(count)
        var pos = 0
        for (buffer in buffers) {
            System.arraycopy(buffer.charArray, 0, charArray, pos, buffer.index)
            pos += buffer.index
            cachePooledCharArrayBuffer(buffer)
        }
        return String(charArray)
    }

### 异步http
&emsp;&emsp; 使用Rpc对象，可以简单的进行同步、线程异步、kotlin协程异步http请求。并且可以打印出每次请求的执行信息，包括执行次数、重试次数、
每次执行花费的时间。

    val request = UrlParser.parseUrl("https://www.baidu.com")
    // 同步
    Rpc.sync("GetBaiduSync", request).asString()
    
    // 线程异步
    Rpc.future("GetBaiduAsync", request).get(timeout, TimeUnit.MILLISECONDS).asString()
    
    // 协程异步
    var response: Response? = null
    val job = AsyncUtil.coroutineScope.launch {
        response = Rpc.deferred("GetBaiduDeferred", request).result(timeout, TimeUnit.MILLISECONDS)
    }
    while (true) {
        if (job.isCompleted) {
            break
        }
    }
    response!!.asString()

<h2 id="5">5.数据源</h2>
&emsp;&emsp; 使用@KtDatabase注解配置数据源。在获取SqlSessionFactory时，会扫描对应的xml mappers和class mappers。
可以使用TransactionHelper进行事务操作。使用@KtTestDatabase配置测试类的数据源。

    @KtDatabase(
        name = DemoDatabase.NAME,
        mapperPath = DemoDatabase.MAPPER_PATH,
        mapperXmlPath = DemoDatabase.MAPPER_XML_PATH,
        dataSetPath = DemoDatabase.DATASET_PATH
    )
    class DemoDatabase : MyBatisDatabase(URL, USERNAME, PASSWORD) {
        companion object {
            const val NAME = "demo"
            const val MAPPER_PATH = "com.xiao.database.mybatis.mapper"
            const val MAPPER_XML_PATH = "classpath*:mybatis/mapper/"
            const val DATASET_PATH = "db/demo"
            const val URL = "jdbc:mysql://localhost:3306/lix_database_demo"
            const val USERNAME = "root"
            const val PASSWORD = "123456"
        }
    }
    
    // 测试类数据源配置
    @KtTestDatabase(
        database = DemoDatabase::class,
        tables = ["users"]
    )
    
<h2 id="6">6.事务</h2>
&emsp;&emsp; 利用TransactionHelper，可以很容易使用事务。使用BaseDatabase配置数据源，使用KtMapperProxy作为MyBatis mapper代理类，
就可以直接在TransactionHelper中执行事务。
    
    @Test
    fun `test mapper rollback with transaction`() {
        val userMapper = MapperUtils.getMapper(sqlSessionFactory, UserMapper::class.java)
        assertEquals(userMapper.getById(1L).password, "password_1")
        val exception = assertThrows<IllegalStateException> {
            TransactionHelper.doInTransaction {
                assertEquals(userMapper.getById(1L).password, "password_1")
                userMapper.updatePasswordById(1L, "password_temp")
                assertEquals(userMapper.getById(1L).password, "password_temp")
                throw IllegalStateException("throws exception.")
            }
        }
        assertEquals("throws exception.", exception.message)
        assertEquals(userMapper.getById(1L).password, "password_1")
    }

    
    
<h2 id="7">7.测试</h2>
&emsp;&emsp; 使用@KtTestDatabase注解，可以声明测试类使用的数据源。KtDataSourceTestBase利用@BeforeAll和@BeforeEach，
可以在每个@Test执行前使用flyway进行迁移。

    @Order(0)
    @BeforeAll
    fun registerDatabase() {
        val annotations = mutableListOf<KtTestDatabase>()
        javaClass.getAnnotation(KtTestDatabase::class.java)?.let {
            annotations.add(it)
        }
        javaClass.getAnnotation(KtTestDatabases::class.java)?.let {
            annotations.addAll(it.value)
        }
        databaseAnnotations = annotations.associateBy { it.database }
        databases = annotations.associate { it.database to databaseInstance(it.database) }
    }
    
    @BeforeEach
    fun migration() {
        val params = buildMigrationParams(databases.values)
            params.forEach { param ->
                val flyway = Flyway
                    .configure()
                    .dataSource(param.url, param.username, param.password)
                    .locations(*(param.locations + "db/migration").toTypedArray())
                    .sqlMigrationSuffixes(".sql")
                    .schemas(*param.schemas.toTypedArray())
                    .load()
                flyway.clean()
                flyway.migrate()
            }
    }
    
&emsp;&emsp; 利用自定义注解实现数据库有关测试，在执行每个方法前会使用flyway来进行数据迁移。而且可以使用TransactionHelper，很方便的实现
事务。
    
    @KtTestDatabase(
        database = DemoDatabase::class,
        tables = ["users"]
    )
    class MyBatisUsageTest : KtTestDataSourceBase() {
        private lateinit var sqlSessionFactory: SqlSessionFactory
    
        @BeforeAll
        fun init() {
            sqlSessionFactory = getDatabase(DemoDatabase::class).sqlSessionFactory()
        }
        
        @Test
        fun `test mapper rollback with transaction`() {
            val userMapper = MapperUtils.getMapper(sqlSessionFactory, UserMapper::class.java)
            assertEquals(userMapper.getById(1L).password, "password_1")
            val exception = assertThrows<IllegalStateException> {
                TransactionHelper.doInTransaction {
                    assertEquals(userMapper.getById(1L).password, "password_1")
                    userMapper.updatePasswordById(1L, "password_temp")
                    assertEquals(userMapper.getById(1L).password, "password_temp")
                    throw IllegalStateException("throws exception.")
                }
            }
            assertEquals("throws exception.", exception.message)
            assertEquals(userMapper.getById(1L).password, "password_1")
        }
    }