* [1.SpringBoot模块结构](#1)
* [2.SpringBoot启动过程](#2)
* [3.Spring Bean的扫描与注册](#3)

<h2 id="1">1.SpringBoot模块结构</h2>
&emsp;&emsp; SpringBoot项目下主要有：buildSrc、spring-boot-project、spring-boot-tests三大模块。
buildSrc主要实现了一些构建工具和插件。spring-boot-tests包含集成测试、测试部署。spring-boot-project包含框架所有功能。

    spring-boot-projects中包含以下模块：
        1，spring-boot，是SpringBoot项目的核心，包含SpringApplication启动类，可以处理SpringBoot启动时的生命周期事件和容器初始化，
            执行SpringBoot初始化逻辑。
        2，spring-boot-actuator，是开箱即用的SpringBoot监控。
        3，spring-boot-actuator-autoconfigure，用来为监控模块提供自动配置的功能。
        4，spring-boot-autoconfigure，提供了各种框架的自动配置，包含所有的框架，但都是用optional的方式引入。
        5，spring-boot-cli，是一个命令行工具，允许运行Groovy脚本。
        6，spring-boot-dependencies，没有代码，配置了所有SpringBoot可能用到的框架，并指定了版本。
        7，spring-boot-devtools，跟SpringBoot的热部署有关。
        8，spring-boot-docs，SpringBoot文档相关。
        9，spring-boot-parent，在spring-boot-dependencies之外定义了一些依赖。
        10，spring-boot-properties-migrator，高版本很多配置属性已经被重命名或者删除，用这个模块可以打印诊断信息，
            并在运行时临时为项目迁移属性，可以用于应用程序升级迁移。
        11，spring-boot-starters，由于spring-boot-autoconfigure包含的依赖都是optional引入的，因此这个模块用来引入依赖。
        12，spring-boot-test，测试相关的模块。
        13，spring-boot-test-autoconfigure，用来自动配置测试模块。
        14，spring-boot-tools，提供了SpringBoot的各种工具。
        
<h2 id="2">2.SpringBoot启动过程</h2>
&emsp;&emsp; spring-boot-starter模块没有代码，引入了spring-boot、spring-boot-autoconfigure、
spring-boot-starter-logging、spring-core、snakeyaml，我们可以直接使用spring-boot-starter模块来引入SpringBoot。

### 1，构造SpringApplication
&emsp;&emsp; 首先会根据某些类是否存在来判断当前webApplicationType，分为：REACTIVE、NONE、SERVLET三种类型。
然后根据"META-INF/spring.factories"文件，设置SpringApplication的bootstrapRegistryInitializers、initializers、
listeners。最后根据异常栈的mian方法，获取到当前SpringApplication启动类。

    SpringApplication属性：
        bootstrapRegistryInitializers = org.springframework.boot.Bootstrapper 
            + org.springframework.boot.BootstrapRegistryInitializer(来自spring.factories) SpringBoot中没有配置。
	    
#### ApplicationContextInitializer
&emsp;&emsp; 在spring.factories中，共配置了8种ApplicationContextInitializer。

    spring-boot-autoconfigure中：
        SharedMetadataReaderFactoryContextInitializer：创建一个在ConfigurationClassPostProcessor
            和SpringBoot之间共享的CachingMetaDataReaderfactory。
        ConditionEvaluationReportLoggingListener：将ConditionEvaluationReport写入到日志。

    spring-boot中：
	ConfigurationWarningsApplicationContextInitializer：用来报告Spring容器的一些常见的错误配置。
        ContextIdApplicationContextInitializer：根据spring.application.name配置项作为context的id。
        DelegatingApplicationContextInitializer：根据context.initializer.classes配置项，获取ApplicationContextInitializer集合，
	        然后使用配置的initializers执行initialize方法。
        RSocketPortInfoApplicationContextInitializer：注册一个RSocketServerInitializedEvent事件listener，
	        用来向environment server.ports配置源中添加local.rsocket.server.port配置。
        ServerPortInfoApplicationContextInitializer：注册一个WebServerInitializedEvent ApplicationListener，
	        用来向environment server.ports配置源中添加web server port配置。
       
    spring-boot-devtools中：
	estartScopeInitializer：向beanFactory中注册restart scope。
        
#### ApplicationListener
&emsp;&emsp; 在各个模块的spring.factories中，共配置了11中ApplicationListener。

    spring-boot-autoconfigure中：
        BackgroundPreinitializer：用一个并行线程来执行一些耗时的初始化任务，当收到ApplicationReadyEvent或者
	    ApplicationFailedEvent时，会阻塞当前并行线程等待完成。包括执行类型转换初始化器、验证初始化器、消息转换初始化器、jackson初始化器、字符集初始化器。
            
    spring-boot-properties-migrator中：
        PropertiesMigrationListener：接收ApplicationPreparedEvent处理配置项转换，当接收到ApplicationReadyEvent或者ApplicationFailedEvent时打印出过期的配置报告。
            
    spring-boot-devtools中：
	    RestartApplicationListener：用来初始化Restarter。
        DevToolsLogFactory.Listener：接收ApplicationPreparedEvent，转换log。
            
    spring-boot中：
	    ClearCachesApplicationListener：接收ContextRefreshedEvent，清理ClassLoader缓存。
	    ParentContextCloserApplicationListener：监听ParentContextAvailableEvent，注册ContextClosedEvent listener，
            如果listener监听到事件，表明listener当前的context对应的父类context已经关闭，此时需要关闭当前listener context。
        FileEncodingApplicationListener：监听ApplicationEnvironmentPreparedEvent，如果environment中
            的spring.mandatory-file-encoding配置项和系统中file.encoding配置项不同，将抛异常。
	    AnsiOutputApplicationListener：监听ApplicationEnvironmentPreparedEvent，将spring.output.ansi.enabled配置项
            绑定到AnsiOutput.Enabled中，并设置consoleAvailable属性。
        DelegatingApplicationListener：监听ApplicationEnvironmentPreparedEvent，根据context.listener.classes配置项
            找到ApplicationListener集合，并添加到multicaster中。
        LoggingApplicationListener：配置日志系统。
        EnvironmentPostProcessorApplicationListener：监听ApplicationEnvironmentPreparedEvent，
            执行spring.factories中的EnvironmentPostProcessor。

#### EnvironmentPostProcessor
&emsp;&emsp; 在SpringBoot各模块spring.factories中，配置了9种EnvironmentPostProcessor。

    spring-boot-autoconfigure中：
        IntegrationPropertiesEnvironmentPostProcessor：向environment中添加配置META-INF/spring.integration.properties。
    
    spring-boot-devtools中：
        DevToolsHomePropertiesPostProcessor：向environment中添加开发工具配置项。
	    DevToolsPropertyDefaultsPostProcessor：开发时，向environment中添加配置。
	
    spring-boot中：
        CloudFoundryVcapEnvironmentPostProcessor：用于设置CloudFoundry框架vcap配置，没有开启不会执行。
	    ConfigDataEnvironmentPostProcessor：用来解析application.properties。
	    RandomValuePropertySourceEnvironmentPostProcessor：向environment中添加RandomValuePropertySource。
	    SpringApplicationJsonEnvironmentPostProcessor：根据spring.application.json或者SPRING_APPLICATION_JSON，
            解析json并向environment中设置配置。
	    SystemEnvironmentPropertySourceEnvironmentPostProcessor：对systemEnvironment环境配置做一个修正。
	    DebugAgentEnvironmentPostProcessor：如果没有显式的disable配置项spring.reactor.debug-agent.enabled，
            那么执行ReactorDebugAgent.init方法。
	
### 2，创建ConfigurableBootstrapContext
&emsp;&emsp; 首先创建ConfigurableBootstrapContext，具体类型为DefaultBootstrapContext。
然后使用SpringApplication中的bootstrapRegistryInitializers初始化该ConfigurableBootstrapContext。
SpringBoot中并没有配置bootstrapRegistryInitializers，因此实际不会执行initialize。
    
    private DefaultBootstrapContext createBootstrapContext() {
        DefaultBootstrapContext bootstrapContext = new DefaultBootstrapContext();
        this.bootstrapRegistryInitializers.forEach((initializer) -> initializer.initialize(bootstrapContext));
        return bootstrapContext;
    }
    
    // 在执行listeners.starting时，实际是用筛选出目标ApplicationListener，执行ApplicationStartingEvent事件，
    // 这里会筛选出三个listener： LoggingApplicationListener、BackgroundPreinitializer、DelegatingApplicationListener，
    // 其中只有LoggingApplicationListener会真实执行，其他的不会执行。
    DefaultBootstrapContext bootstrapContext = createBootstrapContext();
    ConfigurableApplicationContext context = null;
    configureHeadlessProperty();
    SpringApplicationRunListeners listeners = getRunListeners(args);
    listeners.starting(bootstrapContext, this.mainApplicationClass);
        
### 3，SpringApplicationRunListeners对ApplicationEvent事件的分发
&emsp;&emsp; 这一步会去获取spring.factories中获取org.springframework.boot.SpringApplicationRunListener，
SpringBoot项目中只配置了一种SpringApplicationRunListener：EventPublishingRunListener。
这个类主要用来分发各种事件给目标ApplicationListener，让目标ApplicationListener来处理事件。

    private SpringApplicationRunListeners getRunListeners(String[] args) {
    	Class<?>[] types = new Class<?>[] { SpringApplication.class, String[].class };
	    return new SpringApplicationRunListeners(logger,
		    getSpringFactoriesInstances(SpringApplicationRunListener.class, types, this, args),
		    this.applicationStartup);
    }
    
    // 在处理ApplicationEvent时，会先获取支持该ApplicationEvent的ApplicationListener集合，
    // 然后依次执行listener.onApplicationEvent(E extends ApplicationEvent)。
    // 获取listener代码如下，实际上这里的listeners就是前面SpringApplication中的listeners属性。
    for (ApplicationListener<?> listener : listeners) {
    	if (supportsEvent(listener, eventType, sourceType)) {
	        if (retriever != null) {
		        filteredListeners.add(listener);		
	        }
	        allListeners.add(listener);
	    }
    }
    
    listener必须同时满足支持该ApplicationEvent并且支持该ApplicationEvent的来源，
    那么该ApplicationListener才能处理该ApplicationEvent。
    判断是否支持event类型：
        1，如果该listener是GenericApplicationListener类型，那么直接执行listener.supportsEventType(eventType)。
        2，如果listener是SmartApplicationListener类型，执行listener.supportsEventType(eventClass)来判断。
	    3，如果该listener声明的ApplicationEvent类型为空或者是该event的父类或同类，那么就支持该ApplicationEvent。
    判断是否支持event来源：
        1，该listener不是SmartApplicationListener。
        2，该listener是SmartApplicationListener，执行listener.supportsSourceType(sourceType)判断。
    protected boolean supportsEvent(
	    ApplicationListener<?> listener, ResolvableType eventType, @Nullable Class<?> sourceType) {
	    GenericApplicationListener smartListener = (listener instanceof GenericApplicationListener ?
		    (GenericApplicationListener) listener : new GenericApplicationListenerAdapter(listener));
	    return (smartListener.supportsEventType(eventType) && smartListener.supportsSourceType(sourceType));
    }
    
### 4，ConfigurableEnvironment准备
&emsp;&emsp; 1，根据webApplicationType创建ConfigurableEnvironment，创建StandardEnvironment时，
会自动读取System properties和System env。2，配置环境，根据main函数的args和SpringApplication的defaultProperties，
来配置Environment中的propertySources属性。3，Environment除了systemEnvironment、systemProperties
新增configurationProperties。4，使用SpringApplicationRunListeners执行ApplicationEnvironmentPreparedEvent任务。
5，将spring.main下面的配置绑定到SpringApplication同名属性下，例如将spring.main.banner-mode绑定到SpringApplication
bannerMode属性上。
    
    1，webApplicationType不同对应的ConfigurableEnvironment不同，SERVLET对应ApplicationServletEnvironment，
        REACTIVE对应ApplicationReactiveWebEnvironment，其他默认为ApplicationEnvironment。
    
    4，执行SpringApplicationRunListeners.environmentPrepared(ConfigurableBootstrapContext, ConfigurableEnvironment)时，
        会分发ApplicationEnvironmentPreparedEvent事件，根据ApplicationEnvironmentPreparedEvent事件，
        筛选出的ApplicationListener集合有6种，这6种ApplicationListener都会执行：
            EnvironmentPostProcessorApplicationListener、
            AnsiOutputApplicationListener、
            LoggingApplicationListener、
            BackgroundPreinitializer、
            DelegatingApplicationListener、
            FileEncodingApplicationListener。

### 5，打印Banner
&emsp;&emsp; 1，如果SpringApplication bannerMode为OFF，则不会处理Banner。2，会根据environment的属性来获取banner位置，
Banner可以为image或者txt，可以通过spring.banner.image.location设置image Banner位置，通过spring.banner.location
设置txt Banner的位置。默认的banner位置为：banner.${suffix}，suffix可以为：gif、jpg、png、txt。3，获取到Banner列表后，
会打印出所有的Banner。

### 6，ConfigurableApplicationContext准备
&emsp;&emsp; 1，根据webApplicationType创建ConfigurableApplicationContext，context中包含beanFactory属性。
2，准备ConfigurableApplicationContext，先将准备好的environment赋值给context。3，使用SpringApplication 
initializers执行初始化。4，使用SpringApplicationRunListener执行ApplicationContextInitializedEvent任务。
5，注册部分Bean，包括springApplicationArguments、springBootBanner、SpringApplication中的primarySources和sources。
6，使用SpringApplicationRunListener执行contextLoaded。

    1，不同的webApplicationType对应不同的ConfigurableApplicationContext：
        SERVLET对应AnnotationConfigServletWebServerApplicationContext。
        REACTIVE对应AnnotationConfigReactiveWebServerApplicationContext。
        默认为AnnotationConfigApplicationContext。所有context中的beanFactory默认都是DefaultListableBeanFactory。
    
    3，使用initializers执行初始化，依次使用initializer.initialize(context)执行初始化。如果initializer实现了
        ApplicationContextInitializer<T>，但T不是context的父类或者同类，那么将抛异常。
	
    6，获取SpringApplication中的listeners，给实现了ApplicationContextAware接口的listener设置ApplicationContext，
        将SpringApplication中的listeners都添加到ApplicationContext中。然后使用SpringApplicationRunListener执行
        pplicationPreparedEvent。
	
	筛选出4中listener执行ApplicationPreparedEvent, 其中只有EnvironmentPostProcessorApplicationListener、
    LoggingApplicationListener会执行：
        EnvironmentPostProcessorApplicationListener、
	    LoggingApplicationListener、
        BackgroundPreinitializer、
        DelegatingApplicationListener。
	
	public void contextLoaded(ConfigurableApplicationContext context) {
	    for (ApplicationListener<?> listener : this.application.getListeners()) {
            if (listener instanceof ApplicationContextAware) {
                ((ApplicationContextAware) listener).setApplicationContext(context);
            }
            context.addApplicationListener(listener);
	    }
	    this.initialMulticaster.multicastEvent(new ApplicationPreparedEvent(this.application, this.args, context));
	}
	
### 7，refresh ConfigurableApplicationContext
&emsp;&emsp; 这一步将会执行ConfigurableApplicationContext.refresh方法来刷新context。1，准备刷新，先初始化配置源，检查environment中
需要的配置是否都准备好。2，让具体的context执行refreshBeanFactory。3，准备beanFactory，这一步会设置beanFactory的classLoader，
设置EL表达式解析器，设置属性注册解析器，设置忽略自动装配的接口，注册可以解析的自动装配，注册environment Bean，注册systemProperties Bean，
注册systemEnvironment Bean，注册applicationStartup Bean。4，使用postProcessBeanFactory方法让具体的context来处理beanFactory。
5，执行context、beanFactory中的BeanDefinitionRegistryPostProcessor和BeanFactoryPostProcessor。6，注册messageSource Bean。
7，注册applicationEventMulticaster Bean。8，由具体的context执行onRefresh方法来初始化一些特殊的Bean。9，向context中注册ApplicationListener，
包括beanFactory中的ApplicationListener，然后利用context的ApplicationEventMulticaster发布所有的earlyApplicationEvents，
默认earlyApplicationEvents为空。10，完成beanFactory的初始化，冻结所有的beanDefinition，初始化所有剩余的非lazy的单例Bean。
11，完成context的刷新，这一步会先清理context层级的资源缓存，然后初始化LifecycleProcessor并执行lifecycleProcessor的刷新，
接着会发布ContextRefreshedEvent。

    5，执行顺序：
        context.beanFactoryPostProcessors[BeanDefinitionRegistryPostProcessor].postProcessBeanDefinitionRegistry()
        -> beanFactory[BeanDefinitionRegistryPostProcessor](PriorityOrdered、Ordered、else).postProcessBeanDefinitionRegistry()
        -> (context + beanFactory)[BeanDefinitionRegistryPostProcessor].postProcessBeanFactory() ->
        -> context[!BeanDefinitionRegistryPostProcessor].postProcessBeanFactory() ->
        -> beanFactory[BeanFactoryPostProcessor](PriorityOrdered、Ordered、else).postProcessBeanFactory()
	
    10，在创建Bean时：
        1，如果beanFactory instantiationAware中有InstantiationAwareBeanPostProcessor，那么会先执行所
            InstantiationAwareBeanPostProcessor.postProcessBeforeInstantiation用来创建Bean，
            再执行所有beanFactory beanPostProcessors中BeanPostProcessor.postProcessAfterInitialization。
	    2，如果前一步没能创建Bean，那么首先判断该BeanDefinition有没有Supplier，如果有则从Supplier中获取Bean。
	    3，如果没有Supplier，那么会判断是否有factoryMethodName，如果有factoryMethodName那么会根据factoryMethodName和
	        factoryBeanName来创建Bean。
	    4，如果beanFactory有instantiationAware(List<InstantiationAwareBeanPostProcessor>)，
	        并且smartInstantiationAware(List<SmartInstantiationAwareBeanPostProcessor>)不为空，那么会依次
	        调用SmartInstantiationAwareBeanPostProcessor.determineCandidateConstructors(beanClass, beanName)，
	        直到第一个返回的Constructors[]不为空，以此结果作为beanClass的构造器集合。然后根据构造器和传入的构造参数创建Bean。
	        创建完Bean后会根据beanFactory mergedDefinition(List<MergedBeanDefinitionPostProcessor>)，依次执行
	        MergedBeanDefinitionPostProcessor.postProcessMergedBeanDefinition(mbd, beanType, beanName)。
        5，Bean创建完后会根据name或者type来进行AutoWire赋值，然后对当前Bean执行aware方法，包括：BeanNameAware.setBeanName、
	        BeanClassLoaderAware.setBeanClassLoader、BeanFactoryAware.setBeanFactory。 
	    6，然后根据beanFactory的beanPostProcessors，执行BeanPostProcessor.postProcessBeforeInitialization(bean, beanName)。
	    7，执行Bean的InitializingBean.afterPropertiesSet()方法，也可以在BeanDefinition中自定义initMethodName。
	    8，执行beanFactory的BeanPostProcessor.postProcessAfterInitialization(bean, beanName)方法。
	    9，创建完Bean后，如果bean是FactoryBean，那么通过factoryBean.getObject()来获取真正的Bean。
	    10，在创建完所有的Bean后，依次执行Bean的SmartInitializingSingleton.afterSingletonsInstantiated方法。

### 8，启动完成
&emsp;&emsp; 在ConfigurableApplicationContext refresh完成后，会先发布一条ApplicationStartedEvent，
然后会获取所有ApplicationRunner、CommandLineRunner类型的Bean，并执行这些runners。最后会发布一条ApplicationReadyEvent。

### SpringBoot启动流程图
&emsp;&emsp; 包括SpringBoot启动时主要执行的逻辑及。

					SpringApplication初始化
    (加载spring.factories BootstrapRegistryInitializer、ApplicationListener、ApplicationContextInitializer)
                        |
                        V
        创建ConfigurableBootstrapContext
                        |
                        V
    执行[BootstrapRegistryInitializer].initialize(bootstrapContext)
                        |
                        V
    根据spring.factories加载[SpringApplicationRunListener]
                        |
                        V
        发布ApplicationStartingEvent事件
                        |
                        V
            创建ConfigurableEnvironment
                        |
                        V
            配置ConfigurableEnvironment
                        |
                        V
    发布ApplicationEnvironmentPreparedEvent事件
                        |
                        V
        绑定spring.main配置到SpringApplication中
                        |
                        V
                    打印Banner
                        |
                        V
        创建ConfigurableApplicationContext
                        |
                        V
            context设置environment
                        |
                        V
    执行[ApplicationContextInitializer].initialize(context)
                        |
                        V
    发布ApplicationContextInitializedEvent事件
                        |
                        V
        发布BootstrapContextClosedEvent事件
                        |
                        V
    注册springApplicationArguments、springBootBanner Bean, sources BeanDefinistion
                        |
                        V
        发布ApplicationPreparedEvent事件
                        |
                        V
        预处理ConfigurableListableBeanFactory
                        |
                        V
    执行beanFactory[BeanDefinitionRegistryPostProcessor].postProcessBeanDefinitionRegistry(registry) 这一步会扫描出BeanDefinition
                        |
                        V
    执行beanFactory[BeanFactoryPostProcessor].postProcessBeanFactory(beanFactory)
                        |
                        V
    向beanFactory中注册[BeanPostProcessor] Beans
    ------------------->| finishBeanFactoryInitialization(beanFactory)开始，创建所有Beans。LABEL(getBean)
    |     loop(创建失败) V  循环执行LABEL(getBean - getBeanEnd)
	--beanFactory[InstantiationAwareBeanPostProcessor].postProcessBeforeInstantiation(beanClass, beanName)创建Bean
                        | 创建Bean失败                                      | 任一创建成功
                        V                                                  V
    执行BeanDefinition.Supplier.get()创建Bean   执行beanFactory[BeanPostProcessor].postProcessAfterInitialization(bean, beanName)
                        | 创建Bean失败                                      | LABEL(createdBeanByAware)
                        V                                                  V
    根据BeanDefinition factoryBeanName、factoryMethod创建Bean
                        | 创建Bean失败
                        V
    beanFactory[SmartInstantiationAwareBeanPostProcessor].determineCandidateConstructors(beanClass, beanName)确定构造器
                        |
                    -----------------
        没有构造参数  |               | 有构造参数
                    V               V
        直接newInstance创建Bean  先解析构造参数值，再创建Bean
                    |               |
                    -----------------
                        | 这三种创建Bean方式只执行一种
                        V
    执行beanFactory[MergedBeanDefinitionPostProcessor].postProcessMergedBeanDefinition(mbd, beanType, beanName)
                        |
                        V
        向SingletonBeanRegistry中添加ObjectFactory
                        |
                        V
    执行beanFactory[InstantiationAwareBeanPostProcessor].postProcessAfterInstantiation(bean, beanName)
                        |
                        V
                解析Bean中属性的注入
                        |
                        V
    执行beanFactory[InstantiationAwareBeanPostProcessor].postProcessProperties(pvs, bean, beanName)
                        | (如果前一步返回为空才会执行下一步，否则跳过下一步)
                        V
    执行beanFactory[InstantiationAwareBeanPostProcessor].postProcessPropertyValues(pvs, pds, bean, beanName)
                        |
                        V
    根据Bean类型，执行BeanNameAware、BeanClassLoaderAware、BeanFactoryAware
                        |
                        V
    执行beanFactory[BeanPostProcessor].postProcessBeforeInitialization(bean, beanName)
                        |
                        V
    执行Bean(InitializingBean).afterPropertiesSet() 或自定义的init方法
                        |
                        V
    执行beanFactory[BeanPostProcessor].postProcessAfterInitialization(bean, beanName)
                        |
                        -----------------               ------------
                                        |               | from LABEL(createdBeanByAware)
                                        V               V
        如果Bean是FactoryBean，且不以"&"开头，那么从FactoryBean.getObject()获取真正的Bean。LABEL(getBeanEnd)
                                        | 此时循环 LABEL(getBean - getBeanEnd)结束
                                        V
    根据Bean类型执行 SmartInitializingSingleton.afterSingletonsInstantiated()
                                        |
                                        V
                    执行context.LifecycleProcessor.onRefresh()
                                        |
                                        V
                            发布ContextRefreshedEvent事件
                                        |
                                        V
                            发布ApplicationStartedEvent事件
                                        |
                                        V
            执行context中ApplicationRunner、CommandLineRunner Beans
                                        |
                                        V
                            发布ApplicationReadyEvent事件

### ApplicationStartingEvent
&emsp;&emsp; 被3种ApplicationListener监听：LoggingApplicationListener、BackgroundPreinitializer、DelegatingApplicationListener。

<h2 id="3">3.Spring Bean的扫描与注册</h2>
&emsp;&emsp; SpringBoot扫描BeanDefinition的方式：1，类注解，@Configuration、@Controller、@Repository、
@Service、@Component。2，方法注解，@Bean。3.@Import注册。

### ConfigurationClassPostProcessor
&emsp;&emsp; ConfigurationClassPostProcessor会扫描出所有的BeanDefinition。
