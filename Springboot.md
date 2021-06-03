* [1.SpringBoot模块结构](#1)
* [2,SpringBoot启动过程](#2)

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
        
<h2 id="1">2,SpringBoot启动过程</h2>
&emsp;&emsp; spring-boot-starter模块没有代码，引入了spring-boot、spring-boot-autoconfigure、
spring-boot-starter-logging、spring-core、snakeyaml，我们可以直接使用spring-boot-starter模块来引入SpringBoot。

### 1，构造SpringApplication
&emsp;&emsp; 首先会根据某些类是否存在来判断当前webApplicationType，分为：REACTIVE、NONE、SERVLET三种类型。
然后根据"META-INF/spring.factories"文件，设置SpringApplication的bootstrapRegistryInitializers、initializers、
listeners。最后根据异常栈的mian方法，获取到当前SpringApplication启动类。

    SpringApplication属性：
        bootstrapRegistryInitializers = org.springframework.boot.Bootstrapper 
            + org.springframework.boot.BootstrapRegistryInitializer(来自spring.factories) SpringBoot中没有配置。
            
        initializers = org.springframework.context.ApplicationContextInitializer(来自spring.factories)
            SpringBoot项目中ApplicationContextInitializer有8种：
            spring-boot-autoconfigure中：SharedMetadataReaderFactoryContextInitializer、
                ConditionEvaluationReportLoggingListener
            spring-boot中：ConfigurationWarningsApplicationContextInitializer、
                ContextIdApplicationContextInitializer、DelegatingApplicationContextInitializer、
                RSocketPortInfoApplicationContextInitializer、ServerPortInfoApplicationContextInitializer
            spring-boot-devtools中：RestartScopeInitializer
        
        listeners = org.springframework.context.ApplicationListener(来自spring.factories)
            SpringBoot项目中有11种ApplicationListener：
            spring-boot-autoconfigure中：BackgroundPreinitializer
            spring-boot-properties-migrator中：PropertiesMigrationListener
            spring-boot-devtools中：RestartApplicationListener、DevToolsLogFactory.Listener
            spring-boot中：ClearCachesApplicationListener、ParentContextCloserApplicationListener、
                FileEncodingApplicationListener、AnsiOutputApplicationListener、
                DelegatingApplicationListener、LoggingApplicationListener、
                EnvironmentPostProcessorApplicationListener
          
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
    
    在处理ApplicationEvent时，会先获取支持该ApplicationEvent的ApplicationListener集合，
    然后依次执行listener.onApplicationEvent(E extends ApplicationEvent)。
    获取listener代码如下，实际上这里的listeners就是前面SpringApplication中的listeners属性。
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
    判断是否支持event类型：1，如果该listener是GenericApplicationListener类型，那么直接执行listener.supportsEventType(eventType)。
    	2，如果listener是SmartApplicationListener类型，执行listener.supportsEventType(eventClass)来判断。
	3，如果该listener声明的ApplicationEvent类型为空或者是该event的父类或同类，那么就支持该ApplicationEvent。
    判断是否支持event来源：1，该listener不是SmartApplicationListener。2，该listener是SmartApplicationListener，
    	执行listener.supportsSourceType(sourceType)判断。
    protected boolean supportsEvent(
	    ApplicationListener<?> listener, ResolvableType eventType, @Nullable Class<?> sourceType) {
	GenericApplicationListener smartListener = (listener instanceof GenericApplicationListener ?
		(GenericApplicationListener) listener : new GenericApplicationListenerAdapter(listener));
	return (smartListener.supportsEventType(eventType) && smartListener.supportsSourceType(sourceType));
    }
    
### 4，环境准备
&emsp;&emsp; 1，根据webApplicationType创建ConfigurableEnvironment，创建StandardEnvironment时，
会自动读取System properties和System env。2，配置环境，根据main函数的args和SpringApplication的defaultProperties，
来配置Environment中的propertySources属性。3，Environment除了systemEnvironment、systemProperties
新增configurationProperties。4，使用SpringApplicationRunListeners执行ApplicationEnvironmentPreparedEvent任务。
5，将spring.main下面的配置绑定到SpringApplication同名属性下，例如将spring.main.banner-mode绑定到SpringApplication
bannerMode属性上。
        
    执行SpringApplicationRunListeners.environmentPrepared(ConfigurableBootstrapContext, ConfigurableEnvironment)时，
    会分发ApplicationEnvironmentPreparedEvent事件，根据ApplicationEnvironmentPreparedEvent事件，
    筛选出的ApplicationListener集合有6种：EnvironmentPostProcessorApplicationListener、AnsiOutputApplicationListener、
    	LoggingApplicationListener、BackgroundPreinitializer、DelegatingApplicationListener、
	FileEncodingApplicationListener。这6种ApplicationListener都会执行。
