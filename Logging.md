* [1.slf4j概述](#1)
* [2.日志的工作机制](#2)


<h2 id="1">1.概述</h2>
&emsp;&emsp; slf4j是一个Facade(门面)模式的日志适配框架、它并没有实现具体的日志逻辑，主要作用是适配不同的日志框架，如log4j、logback等。
slf4j所做的事情与commons-logging类似。

<h2 id = "2">2.日志的工作机制</h2>
&emsp;&emsp; 以Log4j2为例。
<br>
![Alt text](https://github.com/wang-0821/kotlin_framework/blob/master/log4j2_classes_pic.jpg "Log4j2 classes")

