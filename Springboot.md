* [1.SpringBoot模块结构](#1)
* [2.SpringBoot启动过程](#2)
* [3.Spring Bean的扫描与注册](#3)
* [4.SpringBoot自动配置](#4)
* [5.SpringBoot web](#5)

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
&emsp;&emsp; SpringApplication 在refreshContext(context)开始时，会向Runtime中注册shutdownHook Thread，
在System.exit(status)时，会执行Runtime中所有的shutdown线程。

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
    执行prepareContext(bootstrapContext, context, environment, listeners, arguement, banner)
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
	      prepareContext执行完毕
	      		｜
			V
	执行refreshContext(context)(applicationContext.refresh())
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
    			|
			V
	执行applicationContext.onRefresh()
			|
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
				refreshContext执行完毕
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
&emsp;&emsp; ConfigurationClassPostProcessor.postProcessBeanDefinitionRegistry(registry)这一步会扫描出所有的BeanDefinition。
在根据@ComponentScan获取basePackages时，会先根据basePackages、basePackageClasses注解参数获取，如果都没有设置，那么会根据@ComponentScan
注解类所在的package作为backagePackages，并以basePackages作为BeanDefinition扫描包。

    sao扫描BeanDefinition时，会根据includeFilters和excludeFilters来筛选，在ClassPathScanningCandidateComponentProvider中，
    默认的includeFilters包括：
        AnnotationTypeFilter(Component.class)、
        AnnotationTypeFilter(ManagedBean.class)、
        AnnotationTypeFilter(Named.class)
	
    在Spring中判断一个类是不是Configuration类：
        1，类被@Configuration注解。
        2，类被@Component、@ComponentScan、@Import、@ImportResource任一个所注解。
        3，类中含有被@Bean注解的方法。

    ConfigurationClassPostProcessor.postProcessBeanDefinitionRegistry(registry)执行顺序如下:
            解析registry中所有Configuration类型的BeanDefinition集合-----------
     -------------------------------->  | 有Configuration类型类             | 没有Configuration类型类
    |                                   V                                  V
    |                   获取配置类的@ComponentScan注解集合                  执行完毕
    |                                   |
    |                                   V
    |		     根据@ComponentScan构建ComponentScanAnnotationParser
    |                                   |
    |                                   V
    |   执行ClassPathBeanDefinitionScanner.doScan(basePackages)扫描BeanDefinition
    |                                   | 
    |    -----------------------------> V
    |   |      解析BeanDefinition Lazy、Primary、DependsOn、Role、Description
    |   |                               |
    |   |                               V
    |   |                   注册BeanDefinition(@Component)
    |   |  loop                         | loop end
    |    ------------------------------ V
    |              扫描出当前@ComponentScan中全部BeanDefinition集合
    |                                   |
    |                                   V
    |  loop     解析上一步扫描出的所有Configuration类型的BeanDefinition集合
     ---------------------------------- | loop end
     ---------------------------------> V
    |           处理Configuration类型的BeanDefinition的@Import注解
    |                                   |
    |                                   V
    |        处理Configuration类型的BeanDefinition的@ImportResource注解
    |                                   |
    |                                   V
    |  loop      处理Configuration类型的BeanDefinition的@Bean方法注解
     -----------------------------------| loop end
                                        V
                    完成所有Configuration类型的BeanDefinition的处理
     ---------------------------------> |
    |                                   V
    |           注册Configuration @Import注解的对象为BeanDefinition
    |                                   |
    |                                   V
    |           注册Configuration @Bean注解的方法为BeanDefinition
    |                                   |
    |                                   V
    |        注册Configuration @ImportResource注解对象为BeanDefinition
    |                                   |
    |                                   V
    |    注册 Configuration @Import加载的ImportBeanDefinitionRegistrar目标对象为BeanDefinition
    |  loop                             | loop end
     ---------------------------------- V
                                完成BeanDefinition的注册

### @Component和@Configuration的区别
&emsp;&emsp; @Configuration被@Component注解，@Configuration是一种特殊的@Component，
ConfigurationClassPostProcessor.postProcessBeanDefinitionRegistry(registry)
会扫描出BeanDefinition，然后会对BeanDefinition集合依次执行checkConfigurationClassCandidate。

    1，如果BeanDefinition的beanClass被@Configuration所注解，并且@Configuration.proxyBeanMethods为true，
    	那么会给当前BeanDefinition设置attributes：
	org.springframework.context.annotation.ConfigurationClassPostProcessor.configurationClass = full。
    2，BeanDefinition是ConfigurationClassCandidate，但不满足条件1，那么会设置attributes：
    	org.springframework.context.annotation.ConfigurationClassPostProcessor.configurationClass = lite。
   
    Map<String, Object> config = metadata.getAnnotationAttributes(Configuration.class.getName());
    if (config != null && !Boolean.FALSE.equals(config.get("proxyBeanMethods"))) {		
	beanDef.setAttribute(CONFIGURATION_CLASS_ATTRIBUTE, CONFIGURATION_CLASS_FULL);
    }
    else if (config != null || isConfigurationCandidate(metadata)) {
	beanDef.setAttribute(CONFIGURATION_CLASS_ATTRIBUTE, CONFIGURATION_CLASS_LITE);
    }
    else {
	return false;
    }
    
<br>
&emsp;&emsp; 在执行ConfigurationClassPostProcessor.postProcessBeanFactory(beanFactory)时，
会先对所有org.springframework.context.annotation.ConfigurationClassPostProcessor.configurationClass = true
的BeanDefinition集合，使用ConfigurationClassEnhancer进行CGLIB增强，也就意味着BeanDefinition中的beanClass会被
CGLIB增强后的class所代替。

    // 下面这个test，由于@Configuration bean使用了CGLIB增强，因此在bean内部调用createClassB时，
    // 并不会再次创建ClassB对象，而是使用已经存在的ClassB bean对象。如果将@Configuration换成
    // @Component，那么这个test将会失败。
    
    @Configuration
    class DemoConfiguration {
        @Bean
        fun createClassA(): ClassA {
            return ClassA(createClassB())
        }

        @Bean
        fun createClassB(): ClassB {
            return ClassB()
        }
    }

    @SpringBootTest(classes = [KtSpringBootBaseAutoConfiguration::class])
    class ConfigurationEnhancerTest : KtTestBase() {
        @Autowired
        lateinit var classA: ClassA

        @Autowired
        lateinit var classB: ClassB

        @Test
        fun `test configuration bean method enhancer`() {
            Assertions.assertEquals(classA.classB, classB)
        }
    }

<h2 id="4">4.SpringBoot自动配置</h2>
&emsp;&emsp; SpringBoot应用都会被@SpringBootApplication所注解，@SpringBootApplication注解被@EnableAutoConfiguration注解，
@EnableAutoConfiguration注解被@Import(AutoConfigurationImportSelector.class)所注解，因此在SpringBootApplication启动时，
在ConfigurationClassPostProcessor.postProcessBeanDefinitionRegistry(registry)执行过程中，会处理@Import加载的Bean。

    ConfigurationClassPostProcessor.postProcessBeanDefinitionRegistry(registry)处理@Import：
        执行DeferredImportSelector.Group.process(AnnptationMetadata, DeferredImportSelector)方法，
        这个方式就会获取所有spring.factories中org.springframework.boot.autoconfigure.EnableAutoConfiguration配置项，
        并把扫描出来的所有非ImportSelector、ImportBeanDefinitionRegistrar类型的类当作@Configuration类处理。

<h2 id="5">5.SpringBoot web</h2>
&emsp;&emsp; SpringBoot web程序在启动过程中与普通的SpringBoot程序启动流程是一样的，区别在于由于引入了servlet
和springframework web等依赖，SpringApplication 的WebApplicationType为SERVLET，因此创建的ConfigurableEnvironment
具体类型为ApplicationServletEnvironment，创建的ConfigurableApplicationContext具体类型为
AnnotationConfigServletWebServerApplicationContext。SpringBoot 程序启动执行完后，main线程会自然结束退出，
但是在onRefresh过程中WebServer创建的线程会一直存在，并监听web请求。

    ApplicationServletEnvironment和ApplicationEnvironment的区别在于：
    	构造函数会调用customizePropertySources(propertySources)，ApplicationEnvironment会添加
	systemProperties、systemEnvironment，而ApplicationServletEnvironment会先添加另外两个PropertySource，
	分别是servletConfigInitParams、servletContextInitParams。
	
    ServletWebServerApplicationContext和AnnotationConfigApplicationContext的区别在于：
    	两者refresh具体执行过程不同。
	
	ServletWebServerApplicationContext.refresh()执行过程如下：
				执行prepareRefresh()
					|
					V
				执行initPropertySources()
					|
					V
	执行ConfigurableWebEnvironment.initPropertySources(servletContext, servletConfig)
					|
					V
			执行obtainFreshBeanFactory()获取beanFactory
					|
					V
			执行prepareBeanFactory(beanFactory)
					|
					V
			执行postProcessBeanFactory(beanFactory)
					|
					V
	添加WebApplicationContextServletContextAwareProcessor BeanPostProcessor
					|
					V
		添加ServletContextAware ignoredDependencyInterfaces
					|
					V
			postProcessBeanFactory(beanFactory)执行完毕
					|
					V
	执行[BeanDefinitionRegistryPostProcessor].postProcessBeanDefinitionRegistry(registry)
					|
					V
		执行[BeanFactoryPostProcessor].postProcessBeanFactory(beanFactory)
					|
					V
		执行registerBeanPostProcessors(beanFactory)注册BeanPostProcessor集合
					|
					V
				注册MessageSource
					|
					V
				  执行onRefresh()
				  	|
					V
			初始化GenericWebApplicationContext.themeSource
					|
					V
				获取ServletWebServerFactory
					|
					V
	执行ServletWebServerFactory.getWebServer(getSelfInitializer())获取WebServer
					|
					V
	注册webServerGracefulShutdown、webServerStartStop SmartLifecycle 用来管理webserver生命周期
					|
					V
	执行ConfigurableWebEnvironment.initPropertySources(servletContext, servletConfig)
					|
					V
		用传入的值替换掉servletContextInitParams、servletConfigInitParams
					|
					V
		执行registerListeners()注册ApplicationListener集合
					|
					V
		执行finishBeanFactoryInitialization(beanFactory)完成bean的创建
					|
					V
				执行finishRefresh()

### Undertow WebServer创建过程
&emsp;&emsp; ServletWebServerApplicationContext在执行onRefresh时，
会根据ServletWebServerFactory Bean创建WebServer。如果引入了undertow依赖，
那么默认会根据UndertowServletWebServerFactory创建WebServer。

	WebServerFactoryCustomizer Bean有四种：
	    UndertowServletWebServerFactoryCustomizer、
	    ServletWebServerFactoryCustomizer、
	    UndertowWebServerFactoryCustomizer、
	    LocaleCharsetMappingsCustomizer。
	
	执行WebServerFactoryCustomizerBeanPostProcessor.postProcessBeforeInitialization(bean, beanName)
					|
					V
		从beanFactory中获取WebServerFactoryCustomizer类型的Bean集合
					|
					V
		依次执行WebServerFactoryCustomizer.customize(WebServerFactory)，
	会向UndertowWebServerFactoryDelegate中添加UndertowOption配置类UndertowBuilderCustomizer
					|
					V
		执行factory.getWebServer(ServletContextInitializer)获取WebServer
					|
					V
    	执行UndertowWebServerFactoryDelegate.createBuilder(this)创建Undertow.Builder
					|
					V
		创建Builder，设置ssl、address、port、bufferSize、ioThreads、
			workThreads、directBuffers、http2
					|
					V
	根据factory.port和factory.address向Undertow.Builder.listeners中添加HTTP类型的ListenerConfig
					|
					V
	根据传入的ServletContextInitializer设置DeploymentInfo.servletContainerInitializers
					|
					V
	依次执行UndertowBuilderCustomizer.customize(builder)进一步配置UndertowOption
					|
					V
				Undertow.Builder创建完成
					|
					V
	执行UndertowServletWebServerFactory.createManager(initializers)开始创建DeploymentManager
					|
					V
				创建DeploymentInfo
					|
					V
		用initializers设置DeploymentInfo.servletContainerInitializers
					|
					V
	设置DeploymentInfo的：classLoader、contextPath、displayName、deploymentName
					|
					V
		根据factory中的errorpages配置DeploymentInfo.errorPages
					|
					V
	设置DeploymentInfo的：servletStackTraces、resourceManager、tempDir、eagerFilterInit、
		preservePathOnForward、mimeMappings、listeners
					|
					V
	执行factory.deploymentInfoCustomizers[UndertowDeploymentInfoCustomizer].customize(DeploymentInfo)
					|
					V
			设置DeploymentInfo的：localeCharsetMapping
					|
					V
	创建ServletContainer对象，执行ServletContainer.addDeployment(DeploymentInfo)获取DeploymentManager
					|
					V
			执行DeploymentManager.deploy()
					|
					V
		根据DeploymentManager、DeploymentInfo、ServletContainer创建Deployment
					|
					V
	创建ServletContextImpl(servletContainer, deployment)，并赋值给Deployment.servletContext
					|
					V
	根据ServiceLoader、ServletExtensionHolder、DeploymentInfo获取ServletExtension集合，
	执行[ServletExtension].handleDeployment(deploymentInfo, servletContext)
					|
					V
		设置Deployment.threadSetupActions、Deployment.servletPaths.welcomePages
					|
					V
			设置servletContext的SessionCookieConfigImpl各属性
					|
					V
	根据DeploymentInfo.sessionManagerFactory创建SessionManager并赋值给Deployment.sessionManager
					|
					V
		创建一个ThreadSetupHandler.Action，并根据Deployment.threadSetupActions，
	执行[ThreadSetupHandler].create(Action) 获取一个新的Action，最后执行Action.call(exchange, context)
					|
					V
		设置Deployment的：applicationListeners、servlets、filters
					|
					V
		设置ServletContext的javax.servlet.context.tempdir属性
					|
					V
	根据DeploymentInfo.servletContainerInitializers.instanceFactory创建ServletContainerInitializer集合
		执行[ServletContainerInitializer].onStartup(class, servletContext)
					|
					V
		根据factory.initParameters设置ServletContext.deploymentInfo.initParameters
					|
					V
		根据factory.session.cookie设置ServletContext.sessionCookieConfig
					|
					V ServletWebServerApplicationContext.selfInitialize(servletContext) start
	设置ServletContext的org.springframework.web.context.WebApplicationContext.ROOT 为当前ApplicationContext，
		设置当前GenericWebApplicationContext的servletContext为当前ServletContext
					|
					V
			根据ServletContext设置ApplicationContext的scope
					|
					V
	根据ServletContext向beanFactory中注册servletContext、contextParameters、contextAttributes Bean
					|
					V
	获取beanFactory中的ServletContextInitializer集合，执行[ServletContextInitializer].onStartup(servletContext)
					|ServletWebServerApplicationContext.selfInitialize(servletContext) end
					V
		向Deployment.sessionManager中注册SessionListener
					|
					V
		根据DeploymentInfo中的errorPages设置Deployment的errorPages
					|
					V
		根据DeploymentInfo中的mimeMappings设置Deployment的mimeMappings
					|
					V
			发送context initialized事件
					|
					V
	创建ServletDispatchingHandler HttpHandler，根据DeploymentInfo.innerHandlerChainWrappers，
		执行[HandlerWrapper].wrap(ServletDispatchingHandler)得到一个新的wrappedHandler
					|
					V
		创建RedirectDirHandler(wrappedHandler, servletPaths) wrappedHandler
					|
					V
	创建PredicateHandler(Predicate, secureHandler, wrappedHandler)作为wrappedHandler skippable
					|
					V
	根据DeploymentInfo.outerHandlerChainWrappers，执行[HttpWrapper].wrap(wrappedHandler)作为outerHandler
					|
					V
		创建SendErrorPageHandler(outerHandler)作为outerHandler
					|
					V
	创建PredicateHandler(Predicate, outerHandler, wrappedHandler)作为wrappedHandler
					|
					V
		根据DeploymentInfo.sessionPersistenceManager处理wrappedHandler skippable
					|
					V
		根据DeploymentInfo.metricsCollector处理wrappedHandler skippable
					|
					V
	根据DeploymentInfo.crawlerSessionManagerConfig处理wrappedHandler skippable
					|
					V
	根据Deployment.servletPaths、wrappedHandler、Deployment、ServletContext创建ServletInitialHandler
					|
					V
		根据Deployment.deploymentInfo.initialHandlerChainWrappers，
		执行[HandlerWrapper].wrap(ServletInitialHandler)结果作为initialHandler
					|
					V
		创建HttpContinueReadHandler(initialHandler)作为initialHandler
					|
					V skippable
	根据DeploymentInfo.urlEncoding创建URLDecodingHandler(urlEncoding, initialHandler)作为initialHandler
					|
					V
	设置Deployment的initialHandler(initialHandler)、servletHandler(ServletInitialHandler)
					|
					V
			执行ServletContext.initDone()
					|
					V
			执行Deployment.servletPaths.initData()
					|
					V
	根据Deployment.deploymentInfo.filterUrlMappings计算pathMatches、extensionMatches
					|
					V
	根据Deployment.servlets.managedServletMap计算pathMatches(Set<String>)、
	extensionMatches(Set<String>)、defaultServlet(ServletHandler)、
	pathServlets(Map<String, ServletHandler>)、extensionServlets(Map<String, ServletHandler>)
					|
					V
	如果Deployment.servlets没有"default" ServletHandler，则新建一个作为"default"添加到servlets里
					|
					V
			创建ServletPathMatchesData.Builder对象
					|
					V
	根据pathMatches和DeploymentInfo.getFilterMappings()计算noExtension(Map<DispatcherType, List<ManagedFilter>>)、
		extension(Map<String, Map<DispatcherType, List<ManagedFilter>>>)
					|
					V
	根据pathMatches(pathMatch以"/*"结尾)设置ServletPathMatchesData.Builder.prefixMatches，
		并设置PathMatch的defaultHandler、requireWelcomeFileMatch
					|
					V
	根据pathMatches(pathMatch以"/*"结尾)和extension设置ServletPathMatchesData.Builder.prefixMatches，
			并设置PathMatch的extensionMatches
					|
					V
	根据pathMatches(pathMatch为空)，设置ServletPathMatchesData.Builder.exactPathMatches
					|
					V
	根据pathMatches(pathMatch其他)，设置ServletPathMatchesData.Builder.exactPathMatches
					|
					V
	根据Deployment.getServletHandlers()和DeploymentInfo.getFilterMappings()，
		设置ServletPathMatchesData.Builder.nameMatches
					|
					V
	执行ServletPathMatchesData.Builder.build()获取ServletPathMatchesData并赋值给Deployment.servletPaths.data
					|
					V
			Deployment.servletPaths.initData()执行完毕
					|
					V
	执行[DeploymentInfo.deploymentCompleteListeners].contextInitialized(ServletContextEvent(ServletContext))
					|
					V
			DeploymentManager.deploy()执行完毕
					|
					V
		设置DeploymentManager中DeploymentInfo的mimeExtensionMappings
					|
					V
	设置DeploymentManager.deployment.sessionManager中的defaultSessionTimeout为factory.session.timeout
					|
					V
				DeploymentManager创建完毕
					|
					V
	执行getUndertowWebServer(Builder, DeploymentManager, factory.port)创建UndertowServletWebServer
					|
					V
	执行UndertowWebServerFactoryDelegate(webServerFactory, DeploymentManagerHttpHandlerFactory(DeploymentManager))
				获取HttpHandlerFactory集合
					|
					V
	创建UndertowServletWebServer(Builder, [HttpHandlerFactory], webServer.contextPath, autoPort)
					
					
### Undertow WebServer启动过程
&emsp;&emsp; 在执行LifecycleProcessor.onRefresh()时，会启动WebServer。

	Map<Integer, LifecycleGroup> phases 由于采用phase为key，且采用TreeMap，
	TreeMap会对key进行排序，因此会按照phase顺序执行LifecycleGroup.start()
	
	Lifecycle Bean有两种:
	    WebServerStartStopLifecycle、
	    WebServerGracefulShutdownLifecycle。
		
				执行DefaultLifecycleProcessor.onRefresh()
						|
						V
				执行getLifecycleBeans()获取LifeCycle Bean集合
						|
						V
		根据LifeCycle的getPhase()值不同，创建LifecycleGroup，一个phase值对应一个LifecycleGroup
						|
						V
		根据Lifecycle Bean的phase值的不同，将Lifecycle添加到不同的LifecycleGroup中
						|
						V
					执行[LifecycleGroup].start()
						|
						V
				执行WebServerStartStopLifecycle.start()
						|
						V
					执行WebServer.start()
						|
						V
		执行UndertowWebServer.createUndertowServer()创建UndertowWebServer.undertow
						|
						V
	执行UndertowWebServer.httpHandlerFactories(DeploymentManagerHttpHandlerFactory).getHandler(null),
			创建DeploymentManagerHandler(DeploymentManager) HttpHandler
						|
						V
			设置Undertow.Builder.handler为上一步创建的HttpHandler
						|
						V
		创建Undertow(Undertow.Builder)，将Undertow.Builder中的属性赋值给Undertow
						|
						V
					执行Undertow.start()
						|
						V
		根据ServiceLoader获取XnioProvider，执行XnioProvider.getInstance()获取Xnio
						|
						V
				执行Xnio.createWorker(OptionMap)创建XnioWorker
						|
						V
				根据Xnio创建XnioWorker.Builder(Xnio)
						|
						V
			根据OptionMap将各配置项赋值给XnioWorker.Builder
						|
						V
			执行Xnio.build(XnioWorker.Builder)创建NioXnioWorker
						|
						V
	设置NioXnioWorker的：xnio、terminationTask、name、bindAddressTable、taskPool、workerStackSize
						|
						V 
	根据XnioWorker.Builder.workerIoThreads数值，创建WorkerThread集合，赋值给NioXnioWorker.workerThreads
       --------------------------------------->	|
      |						V
      |			使用Xnio.mainSelectorCreator.open创建一个Selector threadSelector
      |						|
      |						V
      |  创建WorkerThread(NioXnioWorker, threadSelector, name, ThreadGroup, workerStackSize, number)
       -----------------------------------------|
						V
			使用Xnio.mainSelectorCreator.open创建一个Selector threadSelector
						|
						V
	创建一个WorkerThread(NioXnioWorker, threadSelector, name, ThreadGroup, workerStackSize, number)
				赋值给NioXnioWorker.acceptThread
						|
						V
			创建NioWorkerMetrics，并执行NioWorkerMetrics.register()
						|
						V
					NioXnioWorker创建完成
						|
						V
					执行NioXnioWorker.start()
						|
						V
		启动NioXnioWorker.workerThreads线程集，启动NioXnioWorker.acceptThread
						|
						V
			Xnio.createWorker(OptionMap)创建XnioWorker执行完成
						|
						V
			如果Undertow.byteBufferPool为空，那么创建ByteBufferPool
						|
						V
				循环处理Undertow.listeners ListenerConfig
						|
						V
	如果ListenerConfig.rootHandler不为空，那么将其作为rootHandler，否则取Undertow.rootHandler
						|
						V
			根据ByteBufferPool和OptionMap创建HttpOpenListener
						|
						V
	如果允许使用http2，那么handler为Http2UpgradeHandler(rootHandler)，否则为rootHandler
						|
						V
			将handler赋值给HttpOpenListener.rootHandler
						|
						V
			执行ChannelListeners.openListenerAdapter(HttpOpenListener)，
		创建ChannelListener<AcceptingChannel<StreamConnection>> acceptListener
						|
						V
			根据ListenerConfig的host和port创建InetSocketAddress
						|
						V
	执行NioXnioWorker.createStreamConnectionServer(InetSocketAddress, acceptListener, OptionMap)，
					创建AcceptingChannel server
						|
						V
	执行NioXnioWorker.createTcpConnectionServer(InetSocketAddress, acceptListener, OptionMap)
						|
						V
			执行ServerSocketChannel.open()获取一个ServerSocketChannel
						|
						V
			根据OptionMap设置ServersocketChannel的：receiveBufferSize、reuseAddress、backlog
						|
						V
	执行ServerSocketChannel.socket.bind(InetSocketAddress, backlog)，将socket绑定到address上，
			如果这个address是无效的，那么将会绑定到本地任意一个有效的端口上。
						|
						V
		根据NioXnioWorker、ServerSocketChannel、OptionMap创建QueuedNioTcpServer2
						
						
						
						|
						V
			ApplicationContext发布ServletWebServerInitializedEvent
						|
						V
					UndertowWebServer启动成功
					
				
				
