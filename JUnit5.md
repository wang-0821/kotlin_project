* [1.JUnit5组成](#1)
* [2.注解](#2)
* [3.测试类与测试方法](#3)


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

    Junit5各Extension执行顺序：
        获取rootTestTask(NodeTestTask)，执行rootTestTask
                           |
                           V
                 创建ThrowableCollector
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
                           V                                                        isSkipped = true
    执行Node(JupiterEngineDescriptor).shouldBeSkipped(JupiterEngineExecutionContext) -------------> NodeTestTask执行完毕
                           | isSkipped = false
                           V
    根据NodeTestTask children([ClassTestDescriptor])构建子NodeTestTask集合
                           |
                           V
    执行Node(JupiterEngineDescriptor).before(JupiterEngineExecutionContext)
                           |
                           V
    执行Node(JupiterEngineDescriptor).execute(JupiterEngineExecutionContext, DynamicTestExecutor)
                           |
                           V
    执行Node(ClassTestDescriptor).prepare(JupiterEngineExecutionContext)
                           |
                           V
       根据当前测试类上的@ExtendWith注解获取Extension集合
                           |
                           V
       根据parent MutableExtensionRegistry和当前Extensions集合，创建MutableExtensionRegistry，并注入Extension
                           |
                           V
        根据测试类中被@RegisterExtension注解的静态Field的值，获取Extension集合，并注入到registry
                           |
                           V
         根据registry中已注册的TestInstanceFactory，确定当前ClassTestDescriptor的 testInstanceFactory
                           |
                           V
        将测试类中被@BeforeEach注解的方法，注册为Extension到registry
                           |
                           V
        将测试类中被@AfterEach注解的方法，注册为Extension到registry
                           |
                           V
         找到测试类中被@BeforeAll注解的静态方法，赋值给ClassTestDescriptor beforeAllMethods
                           |
                           V
         找到测试类中被@AfterAll注解的静态方法，赋值给ClassTestDescriptor afterAllMethods
                           |
                           V
         创建JupiterEngineExecutionContext(prepare完成，赋值给当前NodeTestTask context)
                           |
                           V                                                        isSkipped = true
         执行Node(ClassTestDescriptor),shouldBeSkipped(JupiterEngineExecutionContext)---------> NodeTestTask执行完毕
                           | isSkipped = false
                           V
        根据NodeTestTask children([TestMethodTestDescripter])构建子NodeTestTask集合
                           |
                           V
        执行Node(ClassTestDescriptor).before(JupiterEngineExecutionContext)
                           | is Lifecycle.PER_CLASS
                           V
          如果ClassTestDescriptor.testInstanceFactory不为空，则据此创建testInstance
                           | testInstanceFactory为空
                           V
            获取测试类构造器，并根据构造器解析构造参数列表，构造ConstructorInvocation
                           |
                           V
            执行ConstructorInvocation.proceed() 创建testInstance
                           |
                           V
           执行[TestInstancePostProcessor].postProcessTestInstance(instance, ClassExtensionContext)
                           |
                           V
          注册testInstance中被@RegisterExtension注解的Field Extension
                           |
                           V
            执行[BeforeAllCallback].beforeAll(context)
                           |
                           V
            执行ClassTestDescriptor beforeAllMethods
                           |
                           V
       执行TestMethodTestDescriptor.prepare(JupiterEngineExecutionContext)
                           |
                           V
       根据parent MutableExtensionRegistry和当前TestMethod @ExtendWith，创建MutableExtensionRegistry，并注入Extension
                           |
                           V
       获取testInstance，构建JupiterEngineExecutionContext给NodeTestTask context赋值
                           |
                           V
                       检查是否skip
                           |
                           V
           执行[BeforeEachCallback].beforeEach(context)
                           |
                           V
         执行[BeforeEachMethodAdaptar].invokeBeforeEachMethod(context, registry)
                           |
                           V
         执行[BeforeTestExecutionCallback].beforeTestExecution(context)
                           |
                           V
                    执行testMethod
                           |
                           V
          执行[AfterTestExecutionCallback].afterTestExecution(context)
                           |
                           V
        执行[AfterEachMethodAdaptar].invokeAfterEachMethod(context, registry)
                           |
                           V
            执行[AfterEachCallback].afterEach(context)
                           |
                           V
            执行ClassTestDescriptor afterAllMethods
                           |
                           V
           执行[AfterAllCallback].afterAll(context)
                           |
                           V
       执行[TestInstancePreDestroyCallback].preDestroyTestInstance(context)
                           |
                           V
                           
