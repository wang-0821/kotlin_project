* [1.JUnit5组成](#1)
* [2.注解](#2)
* [3.测试类与测试方法](#3)
* [4.@SpringBootTest](#4)
* [5.NodeTestTask](#5)

<h2 id="1">1.JUnit5组成</h2>
&emsp;&emsp; JUnit5由JUnit Platform、JUnit Jupiter、JUnit Vintage三部分组成。JUnit Platform提供了运行单元测试的能力，定义了TestEngine API，
能够发现测试创建测试计划，启动测试计划，执行测试，并报告测试结果。JUnit Jupiter提供了使用 JUnit 5 编写测试的API，包含了一个TestEngine的具体实现。
JUnit Vintage 提供了一个TestEngine用来兼容JUnit 3和JUnit 4。

<h2 id="2">2.注解</h2>
&emsp;&emsp; JUnit Jupiter API中有各种注解供测试使用。

    @Test                   标记方法为测试方法，不包含任何参数
    @ParameterizedTest      标记测试方法包含参数
    @RepeatedTest           标记测试方法为重复测试方法，可以设置重复测试次数
    @TestFactory            标记方法为测试工厂，用来生产动态测试
    @TestTemplate           标记方法为测试模版，不是测试用例
    @TestMethodOrder        设置测试方法的执行顺序
    @TestInstance           设置测试对象的生命周期
    @DisplayName            测试类或测试方法设置自定义展示名称
    @DisplayNameGeneration  自定义测试类展示名称generator
    @BeforeEach             标记这个注解标注的方法，会在当前类每个@Test、@RepeatedTest、@ParameterizedTest、@TestFactory注解方法之前执行
    @AfterEach              与@BeforeEach相似，会在每个@Test、@RepeatedTest、@ParameterizedTest、@TestFactory注解方法之后执行
    @BeforeAll              与@BeforeEach相似，但是只会执行一次。在@Test、@RepeatedTest、@ParameterizedTest、@TestFactory之前执行
                            被标注的方法必须是static或者所在类要标注@TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @AfterAll               与@BeforeAll相似，但是会在@Test、@RepeatedTest、@ParameterizedTest、@TestFactory之后执行
                            被标注的方法必须是static或者所在类要标注@TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested                 表示被标注的类是一个非静态嵌套测试类，@BeforeAll、@AfterAll不能直接在@Nested标注类中使用，除非声明了PER_CLASS生命周期
    @Tag                    用来过滤测试，可以用在测试类或者测试方法中
    @Disable                使一个测试类或测试方法无效
    @Timeout                给测试、测试工厂、测试模版、生命周期方法添加执行超时
    @ExtendWith             注册扩展声明
    @RegisterExtension      注册扩展程序
    @TempDir                用来注释类字段或声明周期中的参数或File、Path类型的测试方法，完成操作后，将创建临时目录，测试完毕会删除临时目录
    
### 自定义注解
&emsp;&emsp; JUnit Jupiter中的注解可以被用来当作元注解，通过这些元注解来实现自定义注解。
    
    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Tag("fast")
    @Test
    public @interface Fast {}

<h2 id="3">3.测试类与测试方法</h2>
&emsp;&emsp; 测试类不能是抽象类，并且必须只有一个构造函数。

    Test Method：任何被@Test，@RepeatedTest、@ParameterizedTest、@TestFactory、@TestTemplate注解的实例方法。
    Lifecycle Method：任何被@BeforeAll、@AfterAll、@BeforeEach、@AfterEach注解的方法。
    
### Display Name Generators
&emsp;&emsp; Jupiter能够自定义展示名称，通过@DisplayNameGeneration注解配置。通过@DisplayName注解提供值。
DisplayNameGenerator可以设置显示类型：Standard、Simple、ReplaceUnderscores、IndicativeSentences。
可以设置默认的DisplayNameGenerator，在/test/resources/junit-platform.properties 中设置。

    junit.jupiter.displayname.generator.default = \
        org.junit.jupiter.api.DisplayNameGenerator$ReplaceUnderscores

### 并行执行测试
&emsp;&emsp; 默认的JUnit Jupiter是在单个线程中线性的执行测试。并行执行测试能够加速测试，缩短测试时间。

    在junit-platform.properties中配置junit.jupiter.execution.parallel.enable = true，开启并行执行测试。
    
<br>
&emsp;&emsp; 开启了并行执行测试，也还是会默认线性执行，测试树中每个节点是否并行执行取决于执行模式，默认使用SAME_THREAD模式。
并行执行可能会与已经设置的测试顺序冲突，如果产生冲突，那么只有当测试类或测试方法被@Execution(CONCURRENT)标注，才会以并行的方式执行，
其他情况以线性方式执行。
    
    SAME_THREAD: 强制与parent节点使用同样的线程执行。如果是在测试方法中使用，那么该测试方法将会与@BeforeAll、@AfterAll使用同样的线程。
    CONCURRENT: 并行执行，除非资源锁强制在同一个线程中执行。
    
    可以通过在junit-platform.properties中配置junit.jupiter.execution.parallel.mode.default = concurrent
    
<br>
&emsp;&emsp; 可以配置顶层的类并行执行，但类中的方法以线性的方式执行。
    
    junit.jupiter.execution.parallel.enabled = true
    junit.jupiter.execution.parallel.mode.default = same_thread
    junit.jupiter.execution.parallel.mode.classes.default = concurrent

### 并行执行配置
&emsp;&emsp; 可以通过ParallelExecutionConfigurationStrategy配置并行模式和最大线程池。JUnit Platform提供了两种实现：dynamic、fixed。
而且支持自定义实现。如果没有配置策略，那么默认使用dynamic策略。

    dynamic：可用的处理器数量 * junit.jupiter.execution.parallel.config.dynamic.factor(默认为1)
    fixed: 取junit.jupiter.execution.parallel.config.fixed.parallelism值
    custom: 自定义策略，通过配置junit.jupiter.execution.parallel.config.custom.class
    
### 同步
&emsp;&emsp; @ResourceLock，可以确保同步访问资源，已经定义的常量有：SYSTEM_PROPERTIES、SYSTEM_OUT、SYSTEM_ERR、LOCALE、TIME_ZONE。
如果有测试类需要隔离执行，那么可以添加@Isolated注解，那么该测试类执行时，其他测试类不会在同一时刻并行执行。

    // 资源锁可以设置模式，在READ模式下，可以并行执行，但是在READ_WRITE模式时，必须同步执行
    @ResourceLock(value = SYSTEM_PROPERTIES, mode = READ)
    
<h2 id="4">4.Junit5 Extension</h2>
&emsp;&emsp; JUnit5的Extension支持自定义拓展，不同的拓展可以在不同的时间节点来执行。

    DEFAULT_EXTENSIONS包含：
        DisabledCondition、
        TempDirectory、
        TimeoutExtension、
        RepeatedTestExtension、
        TestInfoParameterResolver、
        TestReporterParameterResolver。  

    NodeTestTask属性包含：taskContext、testDescriptor、node、context、parentContext、throwableCollector。

                                    Junit5执行顺序：
                        获取rootTestTask(NodeTestTask)，执行rootTestTask
                                            |
                                            V
                        执行Node(JupiterEngineDescriptor).prepare(null)
                                            |
                                            V
                    创建MutableExtensionRegistry，并将DEFAULT_EXTENSIONS注入
                                            |
                                            V
            创建JupiterEngineExecutionContext(prepare完成，赋值给当前NodeTestTask context)
                                            |
                                            V                                      isSkipped = true
     执行Node(JupiterEngineDescriptor).shouldBeSkipped(JupiterEngineExecutionContext) -------------> NodeTestTask执行完毕
                                            | isSkipped = false
                                            V executeRecursively
                根据NodeTestTask children([ClassTestDescriptor])构建子NodeTestTask集合
                                            |
                                            V
                执行Node(JupiterEngineDescriptor).before(JupiterEngineExecutionContext)
                                            |
                                            V
        执行Node(JupiterEngineDescriptor).execute(JupiterEngineExecutionContext, DynamicTestExecutor)
                                            |
        ----------------------------------> V
        |   执行Node(ClassTestDescriptor).prepare(JupiterEngineExecutionContext)
        |                                   |
        |                                   V
        |               根据当前测试类上的@ExtendWith注解获取Extension集合
        |                                   |
        |                                   V
        |   根据parent MutableExtensionRegistry和当前Extensions集合，创建MutableExtensionRegistry，并注入Extension
        |                                   |
        |                                   V
        |   根据测试类中被@RegisterExtension注解的静态Field的值，获取Extension集合，并注入到registry
        |                                   |
        |                                   V
        |   根据registry中已注册的TestInstanceFactory，确定当前ClassTestDescriptor的 testInstanceFactory
        |                                   |
        |                                   V
        |       将测试类中被@BeforeEach注解的方法，注册为Extension到registry
        |                                   |
        |                                   V
        |           将测试类中被@AfterEach注解的方法，注册为Extension到registry
        |                                   |
        |                                   V
        |   找到测试类中被@BeforeAll注解的静态方法，赋值给ClassTestDescriptor beforeAllMethods
        |                                   |
        |                                   V
        |   找到测试类中被@AfterAll注解的静态方法，赋值给ClassTestDescriptor afterAllMethods
        |                                   |
        |                                   V
        |   创建JupiterEngineExecutionContext(prepare完成，赋值给当前NodeTestTask context)
        |                                   |
        |                                   V                                     isSkipped = true
        |   执行Node(ClassTestDescriptor),shouldBeSkipped(JupiterEngineExecutionContext)---------> NodeTestTask执行完毕
        |                                   | isSkipped = false
        |                                   V executeRecursively
        |       根据NodeTestTask children([TestMethodTestDescripter])构建子NodeTestTask集合
        |                                   |
        |                                   V
        |       执行Node(ClassTestDescriptor).before(JupiterEngineExecutionContext)
        |                                   | is Lifecycle.PER_CLASS
        |                                   V
        |       如果ClassTestDescriptor.testInstanceFactory不为空，则据此创建testInstance
        |                                   | testInstanceFactory为空
        |                                   V
        |           获取测试类构造器，并根据构造器解析构造参数列表，构造ConstructorInvocation
        |                                   |
        |                                   V
        |               执行ConstructorInvocation.proceed() 创建testInstance
        |                                   |
        |                                   V
        |       执行[TestInstancePostProcessor].postProcessTestInstance(instance, ClassExtensionContext)
        |                                   |
        |                                   V
        |           注册testInstance中被@RegisterExtension注解的Field Extension
        |                                   |
        |                                   V
        |               执行[BeforeAllCallback].beforeAll(context)
        |                                   |
        |                                   V
        |               执行ClassTestDescriptor beforeAllMethods
        |   ------------------------------> |
        |  |                                V
        |  |    执行TestMethodTestDescriptor.prepare(JupiterEngineExecutionContext)
        |  |                                |
        |  |                                V
        |  | 根据parent MutableExtensionRegistry和当前TestMethod @ExtendWith，创建MutableExtensionRegistry，并注入Extension
        |  |                                |
        |  |                                V
        |  |    获取testInstance，构建JupiterEngineExecutionContext给NodeTestTask context赋值
        |  |                                |
        |  |                                V
        |  |                            检查是否skip
        |  |                                |
        |  |                                V
        |  |        执行[BeforeEachCallback].beforeEach(context)
        |  |                                |
        |  |                                V
        |  |    执行[BeforeEachMethodAdaptar].invokeBeforeEachMethod(context, registry)
        |  |                                |
        |  |                                V
        |  |    执行[BeforeTestExecutionCallback].beforeTestExecution(context)
        |  |                                |
        |  |                                V
        |  |                        执行testMethod
        |  |                                |
        |  |                                V
        |  |    执行[AfterTestExecutionCallback].afterTestExecution(context)
        |  |                                |
        |  |                                V
        |  |   执行[AfterEachMethodAdaptar].invokeAfterEachMethod(context, registry)
        |  |                                |
        |  |                                V
        |  |            执行[AfterEachCallback].afterEach(context)
        |  |  TestMethodTestDescriptor      |
        |  -------------------------------- V Node.after(context)
        |               执行ClassTestDescriptor afterAllMethods
        |                                   |
        |                                   V
        |               执行[AfterAllCallback].afterAll(context)
        |                                   |
        |    ClassTestDescriptor loop       V
        --------执行[TestInstancePreDestroyCallback].preDestroyTestInstance(context)

<h2 id="4">4.@SpringBootTest</h2>
&emsp;&emsp; 在SpringBoot框架中使用JUnit5测试时，需要在测试类上添加@SpringBootTest注解。
使用了@SpringBootTest注解的测试类，执行流程跟普通的测试类是一样的，本质上@SpringBootTest
被@ExtendWith(SpringExtension.class)所注解，因此@SpringBootTest注解的测试类跟普通的测试类相比，
只是多了一个SpringExtension。@SpringBootTest中classes属性，是SpringApplication中的primarySources。

### SpringExtension
&emsp;&emsp; SpringExtension实现了：TestInstancePostProcessor、BeforeAllCallback、AfterAllCallback、
BeforeEachCallback、AfterEachCallback、BeforeTestExecutionCallback、AfterTestExecutionCallback、ParameterResolver。

    @SpringBootTest被@BootstrapWith(SpringBootTestContextBootstrapper.class)注解。

    1，SpringExtension.postProcessTestInstance(testInstance, extensionContext)：
        创建BootstrapContext DefaultBootstrapContext(testClass, DefaultCacheAwareContextLoaderDelegate)
                                                |
                                                V
                    根据testClass @BootstrapWith 获取TestContextBootstrapper
                                                |
                                                V
                       构建DefaultTestContext TestContext 并设置activateListener配置的值
                                                |
                                                V
                        根据TestContextBootstrapper创建TestContextManager
                                                |
                                                V
               根据testClass @TestExecutionListeners获取TestExecutionListener集合
                                                |
                 --------------------------------------------------------------------
                | 找到了listeners                                                    | 没有找到listeners
                V                                                                   V
     返回listeners[Class<TestExecutionListener>]      查找spring.factories org.springframework.test.context.TestExecutionListener
                |                                                                   |
                |                                                                   V
                |                                         获取spring.factories DefaultTestExecutionListenersPostProcessor
                |                                                                   |
                |                                                                   V
                |                     执行[DefaultTestExecutionListenersPostProcessor].postProcessDefaultTestExecutionListeners(listeners)
                |                                                                   |
                |                                                                   V
                |                                            返回执行结果作为listeners [Class<TestExecutionListener>]
                |                                                                   |
                 --------------------------------------------------------------------
                                                |
                                                V     
                                       初始化所有listeners
                                                |
                                                V
                  向TestContextManager中注册listeners对象 (getTestContextManager完成)
                                                |
                                                V
                    执行[TestExecutionListener].prepareTestInstance(testContext)
                    
    2，SpringExtension.beforeAll(context): 
        获取TestContextManager，根据TestContextManager中的testExecutionListeners，
        执行[TestExecutionListener].beforeTestClass(testContext)。
        
    3，SpringExtension.beforeEach(context): 
        获取TestContextManager，根据TestContextManager中的testExecutionListeners，
        执行[TestExecutionListener].beforeTestMethod(testContext)。
        
    4，SpringExtension.beforeTestExecution(context)：
        获取TestContextManager，根据TestContextManager中的testExecutionListeners，
        执行[TestExecutionListener].beforeTestExecution(testContext)。
        
    5，SpringExtension.afterTestExecution(context):
        获取TestContextManager，根据TestContextManager中的testExecutionListeners，
        执行[TestExecutionListener].afterTestExecution(testContext)。
        
    6，SpringExtension.afterEach(context):
        获取TestContextManager，根据TestContextManager中的testExecutionListeners，
        执行[TestExecutionListener].afterEach(testContext)。
        
    7，SpringExtension.afterAll(context):
        获取TestContextManager，根据TestContextManager中的testExecutionListeners，
        执行[TestExecutionListener].afterTestClass(testContext)。
        
### TestExecutionListener
&emsp;&emsp; 使用spring-boot-starter-test，spring.factories中配置了15种TestExecutionListener。

    RestDocsTestExecutionListener
    MockRestServiceServerResetTestExecutionListener
    MockMvcPrintOnlyOnFailureTestExecutionListener
    WebDriverTestExecutionListener
    MockWebServiceServerTestExecutionListener
    MockitoTestExecutionListener
    ResetMocksTestExecutionListener
    ServletTestExecutionListener
    DirtiesContextBeforeModesTestExecutionListener
    ApplicationEventsTestExecutionListener
    SpringBootDependencyInjectionTestExecutionListener
    DirtiesContextTestExecutionListener
    TransactionalTestExecutionListener
    SqlScriptsTestExecutionListener
    EventPublishingTestExecutionListener

### TestContextManager
&emsp;&emsp; 在SpringExtension中，上下文信息是放在TestContext中的，而TestContext是从TestContextManager中获取的。

    创建TestContextManager流程：
                先创建DefaultCacheAwareContextLoaderDelegate对象
                                    |
                                    V
      根据testClass和CacheAwareContextLoaderDelegate创建DefaultBootstrapContext
                                    |
                                    V
    根据testClass上的@BootstrapWith获取TestContextBootstrapper(SpringBootTestContextBootstrapper)
                                    |
                                    V
       创建TestContextBootstrapper对象，并设置其bootstrapContext为之前的DefaultBootstrapContext
                                    |
                                    V
      执行TestContextBootstrapper.buildTestContext()，将其值设置为TestContextManager的testContext
                 | testClass没有@ContextConfiguration、                              | 有注解
                 V @ContextHierarchy                                                V                              
      根据testClass和@SpringBootTest.classes创建ContextConfigurationAttributes  根据注解创建[ContextConfigurationAttributes]
                    |                                                               |
                     ---------------------------------------------------------------
                                    |
                                    V
       将@SpringBootTest中的classes添加到每个ContextConfigurationAttributes中的classes中
                                    |
                                    V
       获取任一ContextConfigurationAttributes中的contextLoaderClass，找不到默认SpringBootContextLoader
                                    |
                                    V
                 根据contextLoaderClass创建ContextLoader对象
                                    |                                         
                                    V                                                               
       根据contextLoader类型，执行SmartContextLoader.processContextConfiguration(configAttributes) 或者
                    ContextLoader.processLocations(clazz, ...locations)
                                    |
                                    V
            从spring.factories中获取[ContextCustomizerFactory]
                                    |
                                    V
      执行[ContextCustomizerFactory].createContextCustomizer(testClass, [ContextConfigurationAttributes])
                                    |
                                    V
      根据testClass上的@TestPropertySource构建MergedTestPropertySources
                                    |
                                    V
             根据testClass的@ActiveProfiles来解析activeProfiles
                                    |
                                    V
        根据[ContextConfigurationAttributes]、testClass、MergedTestPropertySources、
            activeProfiles、[ContextCustomizer]创建MergedContextConfiguration
                                    |
                                    V
                    处理MergedContextConfiguration
                                    |
                                    V
         使用testClass、MergedContextConfiguration、CacheAwareContextLoaderDelegate 创建DefaultTestContext
                                    |
                                    V
              向TestContextManager中注册[TestExecutionListener]
                                    |
                                    V
                         TestContextManager创建完毕

<h2 id="1">1.JUnit5组成</h2>
&emsp;&emsp; Junit5最终执行的是NodeTestTask，NodeTestTask中的Node分为：ClassBasedTestDescriptor、
DynamicNodeTestDescriptor、JupiterEngineDescriptor、JupiterTestDescriptor、TestMethodTestDescriptor、
TestTemplateTestDescriptor六种。

        NodeTestTask执行顺序：
            1，NodeTestTask.node.prepare(NodeTestTask.parentcontext)，准备上下文生成NodeTestTask.context。
            2，NodeTestTask.node.shouldBeSkipped(NodeTestTask.context)，检查是否需要跳过执行。如跳过不执行步骤3。
            3.1，NodeTestTask.node.before(NodeTestTask.context)。
            3.2，NodeTestTask.context = NodeTestTask.node.execute(NodeTestTask.context, DefaultDynamicTestExecutor)。
            3.3，NodeTestTask.testDescriptor.children.map(descriptor -> NodeTestTask(taskContext, descriptor))，
                执行HierarchicalTestExecutorService.invokeAll([NodeTestTask])。
            3.4，执行DefaultDynamicTestExecutor.awaitFinished()。
            3.5，执行NodeTestTask.node.after(NodeTestTask.context)。
            4，执行NodeTestTask.node.cleanUp(NodeTestTask.context)。
            5，执行NodeTestTask.node.nodeSkipped(NodeTestTask.context, testDescriptor, skipResult) 和
                NodeTestTask.taskContext.getListener().executionSkipped(testDescriptor, reason) 或者
                NodeTestTask.node.nodeFinished(context, testDescriptor, testExecutionResult) 和
                NodeTestTask.taskContext.getListener().executionFinished(testDescriptor, testExecutionResult)。


            1，JupiterEngineDescriptor.prepare(JupiterEngineExecutionContext)。
                注册了Extension，构建了ExtensionRegistry和ExtensionContext。
                Extension包含：DisabledCondition、TempDirectory、TimeoutExtension、
                    RepeatedTestExtension、TestInfoParameterResolver、TestReporterParameterResolver。

            2，JupiterEngineDescriptor.shouldBeSkipped(JupiterEngineExecutionContext)。都不跳过。
                
            3，JupiterEngineDescriptor.before(JupiterEngineExecutionContext)。返回原来的JupiterEngineExecutionContext。

            4，JupiterEngineDescriptor.execute(JupiterEngineExecutionContext, DefaultDynamicTestExecutor)。 没有执行。

            5，ClassTestDescriptor.prepare(JupiterEngineExecutionContext)。
                1，从当前Class上的@ExtendWith注解获取Extension，并注册到ExtensionRegistry中，生成一个新的ExtensionRegistry。
                2，从当前Class及SuperClass及interface上获取DeclaredFields，且这些Fields是static被@RegisterExtension注解，
                    如果这些Fields的值是Extension，那么将这些Extension注册到ExtensionRegistry中。
                3，获取ExtensionRegistry中的TestInstanceFactory类型的Extension作为testInstanceFactory，如果多于一个那么会报错。
                4，获取Class上被@BeforeEach注解的Method集合，Method必须是void且不是静态方法。注册该方法为一个Extension。
                5，获取Class上被@AfterEach注解的Method集合，Method必须是void且不是静态方法。注册该方法为一个Extension。
                6，构建ClassExtensionContext对象。
                6，获取Class上被@BeforeAll注解的Method集合，如果lifecycle是PER_METHOD，那么要求是static。
                7，获取Class上被@AfterAll注解的Method集合，如果lifecycle是PER_METHOD，那么要求是static。
                8，构建TestInstancesProvider、ExtensionRegistry、ExtensionContext、ThrowableCollector。
            
            6，ClassTestDescriptor.shouldBeSkipped(JupiterEngineExecutionContext)，计算是否会跳过执行。

            7，ClassTestDescriptor.before(JupiterEngineExecutionContext)。
                1，如果是PER_CLASS Lifecycle，执行TestInstancesProvider.getInstances(extensionRegistry, throwableCollector)，
                    获取到TestInstances对象，并设置ClassExtensionContext.testInstances。
                2，获取TestInstances时，如果TestInstanceFactory不为空，那么执行TestInstanceFactory.createTestInstance()，
                    如果为空，那么使用Class constructor来构建TestInstances对象。
                3，从ExtensionRegistry中获取TestInstancePostProcessor Extension集合，
                    执行TestInstancePostProcessor.postProcessTestInstance(instance, ClassExtensionContext)。
                    这一步会执行SpringExtension.postProcessTestInstance(instance, ClassExtensionContext)。
                4，从当前Class及SuperClass及interface上获取DeclaredFields，且这些Fields不是static被@RegisterExtension注解，
                    如果这些Fields的值是Extension，那么将这些Extension注册到ExtensionRegistry中。
                5，获取ExtensionRegistry中的BeforeAllCallback Extension集合，执行BeforeAllCallback.beforeAll(ClassExtensionContext)。
                6，获取beforeAllMethods集合，执行beforeAll。

            8，ClassTestDescriptor.execute(JupiterEngineExecutionContext)，没有执行。

            9，TestMethodTestDescriptor.prepare(JupiterEngineExecutionContext)，获取testMethod上的@ExtendWith注解，
                将Extension注册到ExtensionRegistry中。

            10，TestMethodTestDescriptor.shouldBeSkipped(JupiterEngineExecutionContext)，计算是否跳过执行。
        
            11，TestMethodTestDescriptor.before(JupiterEngineExecutionContext)，没有执行。

            12，TestMethodTestDescriptor.execute(JupiterEngineExecutionContext, DynamicTestExecutor)。
                1，执行BeforeEachCallback.beforeEach(JupiterEngineExecutionContext)。
                2，执行BeforeEachMethodAdapter.invokeBeforeEachMethod(MethodExtensionContext, ExtensionRegistry)。
                3，执行BeforeTestExecutionCallback.beforeTestExecution(MethodExtensionContext)。
                4，执行invokeTestMethod。
                5，执行AfterTestExecutionCallback.afterTestExecution(MethodExtensionContext)。
                6，执行AfterEachMethodAdapter.invokeAfterEachMethod(MethodExtensionContext, ExtensionRegistry)。
                7，执行AfterEachCallback.afterEach(MethodExtensionContext)。

            13，执行DynamicTestExecutor.awaitFinished()。
        
            14，执行TestMethodTestDescriptor.after(JupiterEngineExecutionContext)，没有执行。

            15，执行TestMethodTestDescriptor.cleanUp(JupiterEngineExecutionContext)。
                1，如果是PER_METHOD，执行TestInstancePreDestroyCallback.preDestroyTestInstance(MethodExtensionContext)。
                2，执行MethodExtensionContext.close()。

            16，执行TestMethodTestDescriptor.nodeFinished(JupiterEngineExecutionContext, TestMethodTestDescriptor, TestExecutionResult)。
    
            17，执行ClassTestDescriptor.after(JupiterEngineExecutionContext)。
                1，执行afterAllMethods。
                2，执行AfterAllCallback.afterAll(ClassExtensionContext)。
                3，如果是PER_CLASS，执行TestInstancePreDestroyCallback.preDestroyTestInstance(ClassExtensionContext)。

            18，执行ClassTestDescriptor.cleanUp(JupiterEngineExecutionContext)。
                1，执行ClassExtensionContext.close()。

            19，执行ClassTestDescriptor.nodeFinished(JupiterEngineExecutionContext, ClassTestDescriptor, TestExecutionResult)。
                
            20，执行JupiterEngineDescriptor.after(JupiterEngineExecutionContext)。没有执行。

            21，执行JupiterEngineDescriptor.cleanUp(JupiterEngineExecutionContext)。
                1，执行JupiterEngineExtensionContext.close()。

            22，执行JupiterEngineDescriptor.nodeFinished(JupiterEngineExecutionContext, JupiterEngineDescriptor, TestExecutionResult)。

                