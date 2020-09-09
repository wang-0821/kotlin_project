* [1.slf4j概述](#1)
* [2.日志的工作机制](#2)
* [3.Slf4j适配原理](#3)
* [4.Log4j2机制](#4)


<h2 id="1">1.概述</h2>
&emsp;&emsp; slf4j是一个Facade(门面)模式的日志适配框架、它并没有实现具体的日志逻辑，主要作用是适配不同的日志框架，如log4j、logback等。
slf4j所做的事情与commons-logging类似。

<h2 id = "2">2.日志的工作机制</h2>
以Log4j2为例。
<br>
    ![Log4j2_Classes](https://github.com/wang-0821/kotlin_framework/raw/master/log4j2_classes_pic.jpg)

### LoggerContext
&emsp;&emsp; 一个LoggerContext相当于一个缓存区，用来存储Logger集合，一个LoggerContext有一个配置类Configuration。

### Logger
&emsp;&emsp; 一个Logger是执行打印的入口对象，一个Logger包含一个LoggerConfig。

### LoggerConfig
&emsp;&emsp; LoggerConfig是保存了Logger打印相关配置，包括Appender集合，真正执行打印的是LoggerConfig中的Appender集合。
LoggerConfig中还可能包含Filters，对于不符合的打印事件，会直接筛去，无法执行Appender集合打印。

### Appender
&emsp;&emsp; 真正执行打印的对象，包含打印布局、内容、输出位置等信息。 Appender也可能有Filters。Appender包含Layout。

### Layout
&emsp;&emsp; 保存打印格式信息。

### Configuration
&emsp;&emsp; 相当于一个大的缓存区，保存LoggerConfig集合、Appender集合。Configuration也可能有Filters。

<h2 id="3">3.Slf4j适配原理</h2>
&emsp;&emsp; Slf4j会在LoggerFactory.getLogger的时候，找到真正的ILoggerFactory的对象。查找方式分两种，一种静态类加载，一种使用ServiceLoader方式加载。

<br>
&emsp;&emsp; 在1.8之前使用直接找StaticLoggerBinder类进行加载，然后通过这个类找到真正实现ILoggerFactory的对象。在2.XX中，使用ServiceLoader的方式，
加载SLF4JServiceProvider类，然后再找到真正实现了ILoggerFactory的对象。找不到ILoggerFactory具体对象的话会使用NOPLoggerFactory。

<br>
&emsp;&emsp; 在Log4j 2.2等较新的版本，不支持ServiceLoader加载。因此使用Slf4j 2.x之前的版本，兼容性更好。


<h2 id="4">4.Log4j2机制</h2>
&emsp;&emsp; 本质上Log4j2是先获取LoggerContext，然后再从LoggerContext中获取Logger，而LoggerContext来自LoggerContextFactory。

    ／／ LoggerContext获取源码
    public static LoggerContext getContext(final boolean currentContext) {
            // TODO: would it be a terrible idea to try and find the caller ClassLoader here?
            try {
                return factory.getContext(FQCN, null, null, currentContext, null, null);
            } catch (final IllegalStateException ex) {
                LOGGER.warn(ex.getMessage() + " Using SimpleLogger");
                return new SimpleLoggerContextFactory().getContext(FQCN, null, null, currentContext, null, null);
            }
        }

### PropertiesUtil
&emsp;&emsp; 实际上是获取的/META-INF/log4j2.component.properties配置文件，读取了其中的配置信息。

### LoggerContextFactory获取
&emsp;&emsp; 1.读取PropertiesUtil log4j2.loggerContextFactory配置项。

    final PropertiesUtil managerProps = PropertiesUtil.getProperties();
            final String factoryClassName = managerProps.getStringProperty(FACTORY_PROPERTY_NAME);
            if (factoryClassName != null) {
                try {
                    factory = LoaderUtil.newCheckedInstanceOf(factoryClassName, LoggerContextFactory.class);
                } catch (final ClassNotFoundException cnfe) {
                    LOGGER.error("Unable to locate configured LoggerContextFactory {}", factoryClassName);
                } catch (final Exception ex) {
                    LOGGER.error("Unable to create configured LoggerContextFactory {}", factoryClassName, ex);
                }
            }

<br>
&emsp;&emsp; 2.当第一种方式获取不到LoggerContextFactory时，将通过Provider来创建LoggerContextFactory对象，
这里Provider的来源分两部分：1，通过ServiceLoader获取Provider；2，通过META-INF/log4j-provider.properties配置文件，
读取"LoggerContextFactory"配置项，来获取对应的LoggerContextFactory。

    if (ProviderUtil.hasProviders()) {
                    for (final Provider provider : ProviderUtil.getProviders()) {
                        final Class<? extends LoggerContextFactory> factoryClass = provider.loadLoggerContextFactory();
                        if (factoryClass != null) {
                            try {
                                factories.put(provider.getPriority(), factoryClass.newInstance());
                            } catch (final Exception e) {
                                LOGGER.error("Unable to create class {} specified in provider URL {}", factoryClass.getName(), provider
                                        .getUrl(), e);
                            }
                        }
                    }
                    ......
    
<br>
&emsp;&emsp; 3，当第一种方式找不到LoggerContextFactory，且不存在Provider时，使用SimpleLoggerContextFactory。

### LoggerContext获取
&emsp;&emsp; 通常如果不另外配置，Log4j2会通过ServiceLoader找到Log4jProvider，进而产生Log4jContextFactory对象，
这个对象将作为后续使用的实际LoggerContextFactory。在Log4jContextFactory中，会通过ContextSelector来获取LoggerContext。
在LoggerContext产生后，会执行start方法。

    public LoggerContext getContext(final String fqcn, final ClassLoader loader, final Object externalContext,
                                        final boolean currentContext, final URI configLocation, final String name) {
            final LoggerContext ctx = selector.getContext(fqcn, loader, currentContext, configLocation);
            if (externalContext != null && ctx.getExternalContext() == null) {
                ctx.setExternalContext(externalContext);
            }
            if (name != null) {
            	ctx.setName(name);
            }
            if (ctx.getState() == LifeCycle.State.INITIALIZED) {
                if (configLocation != null || name != null) {
                    ContextAnchor.THREAD_CONTEXT.set(ctx);
                    final Configuration config = ConfigurationFactory.getInstance().getConfiguration(ctx, name, configLocation);
                    LOGGER.debug("Starting LoggerContext[name={}] from configuration at {}", ctx.getName(), configLocation);
                    ctx.start(config);
                    ContextAnchor.THREAD_CONTEXT.remove();
                } else {
                    ctx.start();
                }
            }
            return ctx;
        }

<br>
&emsp;&emsp; LoggerContext start方法，主要执行了一个方法：reconfigure。reConfigure主要做了两件事：1，获取Configuration；
2，如果获取到了Configuration，那么把这个Configuration作为LoggerContext真正的Configuration。
获取Configuration时，首先获取了ConfigurationFactory然后通过factory获取到真正的Configuration。
    
    private void reconfigure(final URI configURI) {
            Object externalContext = externalMap.get(EXTERNAL_CONTEXT_KEY);
            final ClassLoader cl = ClassLoader.class.isInstance(externalContext) ? (ClassLoader) externalContext : null;
            LOGGER.debug("Reconfiguration started for context[name={}] at URI {} ({}) with optional ClassLoader: {}",
                    contextName, configURI, this, cl);
            final Configuration instance = ConfigurationFactory.getInstance().getConfiguration(this, contextName, configURI, cl);
            if (instance == null) {
                LOGGER.error("Reconfiguration failed: No configuration found for '{}' at '{}' in '{}'", contextName, configURI, cl);
            } else {
                setConfiguration(instance);
                /*
                 * instance.start(); Configuration old = setConfiguration(instance); updateLoggers(); if (old != null) {
                 * old.stop(); }
                 */
                final String location = configuration == null ? "?" : String.valueOf(configuration.getConfigurationSource());
                LOGGER.debug("Reconfiguration complete for context[name={}] at URI {} ({}) with optional ClassLoader: {}",
                        contextName, location, this, cl);
            }
        }
            
### ContextSelector获取
&emsp;&emsp; 如果在创建Log4jContextFactory时没有指定ContextSelector的话，首先通过读取PropertiesUtil Log4jContextSelector配置项，
如果没有配置，那么将使用ClassLoaderContextSelector作为实际的ContextSelector。
    
    // 获取ContextSelector
    private static ContextSelector createContextSelector() {
            try {
                final ContextSelector selector = Loader.newCheckedInstanceOfProperty(Constants.LOG4J_CONTEXT_SELECTOR,
                    ContextSelector.class);
                if (selector != null) {
                    return selector;
                }
            } catch (final Exception e) {
                LOGGER.error("Unable to create custom ContextSelector. Falling back to default.", e);
            }
            return new ClassLoaderContextSelector();
        }
        
    ／／ 获取LoggerContext。
    // 当currentContext为 true，那么会从ThreadLocal中读取LoggerContext，找不到则返回名为Default的LoggerContext。
    // 当currentContext为false，那么根据ClassLoader获取LoggerContext，如果没找到那么会创建一个LoggerContext。
    public LoggerContext getContext(final String fqcn, final ClassLoader loader, final boolean currentContext,
                final URI configLocation) {
            if (currentContext) {
                final LoggerContext ctx = ContextAnchor.THREAD_CONTEXT.get();
                if (ctx != null) {
                    return ctx;
                }
                return getDefault();
            } else if (loader != null) {
                return locateContext(loader, configLocation);
            } else {
                final Class<?> clazz = StackLocatorUtil.getCallerClass(fqcn);
                if (clazz != null) {
                    return locateContext(clazz.getClassLoader(), configLocation);
                }
                final LoggerContext lc = ContextAnchor.THREAD_CONTEXT.get();
                if (lc != null) {
                    return lc;
                }
                return getDefault();
            }
        }

### ConfigurationFactory获取
&emsp;&emsp; ConfigurationFactory获取方式有两种：1，从PropertiesUtil log4j.configurationFactory 配置项读取。
2，获取插件category为ConfigurationFactory的插件。目前支持：JsonConfigurationFactory、PropertiesConfigurationFactory、
XmlConfigurationFactory、YamlConfigurationFactory。

    final List<ConfigurationFactory> list = new ArrayList<>();
                        PropertiesUtil props = PropertiesUtil.getProperties();
                        final String factoryClass = props.getStringProperty(CONFIGURATION_FACTORY_PROPERTY);
                        if (factoryClass != null) {
                            addFactory(list, factoryClass);
                        }
                        final PluginManager manager = new PluginManager(CATEGORY);
                        manager.collectPlugins();
                        final Map<String, PluginType<?>> plugins = manager.getPlugins();
                        final List<Class<? extends ConfigurationFactory>> ordered = new ArrayList<>(plugins.size());
                        for (final PluginType<?> type : plugins.values()) {
                            try {
                                ordered.add(type.getPluginClass().asSubclass(ConfigurationFactory.class));
                            } catch (final Exception ex) {
                                LOGGER.warn("Unable to add class {}", type.getPluginClass(), ex);
                            }
                        }
                        Collections.sort(ordered, OrderComparator.getInstance());
                        for (final Class<? extends ConfigurationFactory> clazz : ordered) {
                            addFactory(list, clazz);
                        }
        
### Configuration获取
&emsp;&emsp; 首先获取PropertiesUtil的log4j.configurationFile 配置项，获取configuration配置文件的地址，然后根据这些地址，
遍历不同的ConfigurationFactory进行解析，如果有多个配置地址，那么会产生CompositeConfiguration。如果没有配置，
那么会默认去找log4j2+loggerContextName+suffix文件，遍历ConfigurationFactory进行解析，如果没找到会再找log4j2+suffix文件，遍历加载。
一旦找到任意一个Configuration配置文件，那么就将替换掉LoggerContext中的Configuration。

    例如xml类型Configuration文件为：log4j2.xml

### Logger获取
&emsp;&emsp; LoggerContext中含有LoggerRegistry属性用来存储它所包含的Logger，如果找到对应名称的则返回，否则创建一个新的Logger。

    public Logger getLogger(final String name, final MessageFactory messageFactory) {
            // Note: This is the only method where we add entries to the 'loggerRegistry' ivar.
            Logger logger = loggerRegistry.getLogger(name, messageFactory);
            if (logger != null) {
                AbstractLogger.checkMessageFactory(logger, messageFactory);
                return logger;
            }
    
            logger = newInstance(this, name, messageFactory);
            loggerRegistry.putIfAbsent(name, messageFactory, logger);
            return loggerRegistry.getLogger(name, messageFactory);
        }
        
    // 新产生的Logger对象，其privateConfig中的参数来自于当前Logger所属的LoggerContext的Configuration。
    protected Logger(final LoggerContext context, final String name, final MessageFactory messageFactory) {
            super(name, messageFactory);
            this.context = context;
            privateConfig = new PrivateConfig(context.getConfiguration(), this);
        }
        
    // 如果找不到对应名称的LoggerConfig，那么返回Configuration的root LoggerConfig。
    public PrivateConfig(final Configuration config, final Logger logger) {
                this.config = config;
                this.loggerConfig = config.getLoggerConfig(getName());
                this.loggerConfigLevel = this.loggerConfig.getLevel();
                this.intLevel = this.loggerConfigLevel.intLevel();
                this.logger = logger;
                this.requiresLocation = this.loggerConfig.requiresLocation();
            }
            
### Logger打印
&emsp;&emsp; Logger有两种：Logger、AsyncLogger。LoggerContext.getLogger获取到的是Logger，AsyncLoggerContext获取到的是AsyncLogger。
通过配置ContextSelector能够决定不同Logger的产生。以Logger为例，最终会使用对应的loggerConfig进行打印，执行具体打印的为logConfig里面的Appender列表。

