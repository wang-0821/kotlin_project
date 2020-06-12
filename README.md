## kotlin 基础

[读书笔记](ReadNotes.md)

* [1.项目概述](#1)

<h2 id="1">1.项目概述</h2>
&emsp;&emsp; 本项目主要用来学习Kotlin，Kotlin完全兼容Java，可以使用Java的各种框架，Java也可以直接调用Kotlin方法。Kotlin写起来很简洁，
更像是java的升级版。

<br>
<br>
&emsp;&emsp; 本项目目前参考Spring IOC的机制，实现了资源扫描，并根据不同的注解，进行不同的处理。
rpc-framework主要是对之前项目中rpc框架的改进，使用了Kotlin进行实现。参考springboot装配式的思想，在rpc-framework中，
可以通过@Component注解注入自定义的bean，来替换掉框架中对应Class的实例，这种方式更支持后续自定义功能。参考执行链的设计方式，
支持多个Handler，可以自定义组合多层handler来进行不同处理。参考kotlin coroutine中Element的设计方式，实现了根据不同的Context Key，
注册不同的context，比如通用的BeanRegistry就是一个context，SocketContext是另一个context，根据Key就能对不同的context进行隔离。
参考ApplicationContextAware方式，设计了多个ContextAware，来实现不同上下文的访问。

<br>
<br>
&emsp;&emsp; 在这个项目中支持扫描自定义注解，并且支持自定义includeTypeFilter， excludeTypeFilter，只要自定义的注解被@AnnotationScan所注解，
并且满足扫描条件的，那么该资源就会被ContextScanner扫描处理。只需要后续改进ContextScanner.handleResourceProcessors 方法，就能实现所有自定义的注解都被执行。

<br>
<br>
&emsp;&emsp; 在SocketContext、RouteContext中，都采用不同的Key，表示这是不同的上下文，但在BeanRegistry接口中，我指定Key为BeanRegistry，
这表明类型为BeanRegistry的上下文只能有一个，通过Key的不同及放置位置就能实现上下文是否为单例模式，以及实现上下文的隔离。
