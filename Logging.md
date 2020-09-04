* [1.slf4j概述](#1)
* [2.日志的工作机制](#2)
* [3.Slf4j适配原理](#3)


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
