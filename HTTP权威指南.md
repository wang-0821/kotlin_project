## Http权威指南

* [1.概述](#1)
* [2.URL与资源](#2)
* [3.HTTP报文](#3)


<h2 id="1">1.概述</h2>
### URI
&emsp;&emsp; URI是统一资源标识符(Uniform Resource Identifier)，URI像因特网上的邮政地址一样，在世界范围唯一标识并定位信息资源。
URI有两种形式，分别为URL和URN。

### URL
&emsp;&emsp; URL是统一资源定位符(Uniform Resource Locator)。URL描述了一台特定服务器上某些资源的特定位置。它们可以明确的说明如何从一个精确、固定的位置获取资源。
URL格式包含三个部分：URL第一部分称为方案(scheme)，说明了访问资源所使用的协议类型，通常就是HTTP协议(http://)。第二部分给出了服务器的地址。
其余部分指定了web服务器上的某个资源。现在几乎所有的URI都是URL。

### URN
&emsp;&emsp; URI第二种形式是统一资源名(Uniform Resource Name)。URN是作为特定内容的唯一名称使用的，与目前的资源所在地无关。使用这些与位置无关的URN，
就能将资源四处搬移。通过URN可以用同一个名字通过多种网络访问协议来访问资源。目前处于实验阶段。

### 事务
&emsp;&emsp; 一个HTTP事务由一条请求命令和一个响应结果组成，这种通信是通过名为HTTP报文的格式化数据进行的。常见的HTTP方法有：GET、PUT、
DELETE、POST、HEAD。每条HTTP响应报文返回时，都会携带一个状态码(200)，一个描述性的原因短语(OK)，告知客户端请求是否成功或者是否需要采取其他动作。

### 报文
&emsp;&emsp; HTTP报文由一行行简单的字符串组成。请求报文和响应报文格式很类似。包括三个部分：起始行、首部字段、主体。
起始行：报文第一行为起始行，在请求报文中用来说明要做什么，在响应报文说明出现了什么情况。首部字段：起始行后有零个或多个首部字段。
每个首部字段包含一个名字和一个值，两者间用(:)分隔。首部以一个空行结束。添加一个首部字段和添加新行一样简单。空行之后是可选的报文主体，
包含了所有类型的数据，请求主体中包含了要发给web服务器的数据，响应主体中装载了要返回给客户端的数据。

    请求：
        GET /tools.html HTTP/1.0
        User-agent: Mozilla/4.75...
        Host: www.xxx.com
        Accept: text/html, image/gif
        Accept-language: en
        
    响应：
        HTTP/1.0 200 OK
        Content-length: 403
        Content-type: text/html
        
        <HTML>
            ...
        </HTML>

### 连接
&emsp;&emsp; HTTP采用传输控制协议(Transmission Control Protocol) TCP，HTTP是个应用层协议。TCP提供了：无差错的数据传输；
按序传输(数据总会按照发送的顺序到达)；未分段的数据流(可以在任意时刻以任意尺寸将数据发送出去)。

### 协议版本
&emsp;&emsp; HTTP/0.9 有严重缺陷，只支持GET方法，不支持多媒体内容。HTTP/1.0 添加了各种HTTP首部，一些额外的方法，以及对多媒体对象的处理。
HTTP/1.0+ 添加了很多特性，包括keep-alive连接、虚拟机支持，代理连接支持，成为非官方的事实标准。HTTP/1.1 校正HTTP设计中的结构性缺陷，
明确语义，引入性能优化措施，删除一些不好的特性，是当前使用的HTTP版本。HTTP/2.0 关注性能优化以及更强大的服务逻辑远程执行框架。

### 代理
&emsp;&emsp; 代理位于客户端和服务器之间，接受所有客户端的HTTP请求，并将请求转发给服务器(可能会对请求进行修改)。

### 缓存
&emsp;&emsp; Web缓存(Web cache)或代理缓存(proxy cache)是特殊的HTTP代理服务，可以将经过代理传送的常用文档复制保存起来。

### 网关
&emsp;&emsp; 网关(gateway)是一种特殊的服务器，作为其他服务器的中间实体使用。通常用于将HTTP流量转换成其他的协议。

### 隧道
&emsp;&emsp; 隧道(tunnel)是建立起来后，在两条连接之间对原始数据进行盲转发的HTTP应用程序。HTTP隧道通常用来在一条或多条HTTP连接上转发非HTTP数据，
转发时不会窥探数据。HTTP隧道常见用途是通过HTTP连接承载加密的安全套接字层(SSL)流量。这样SSL流量就可以穿过只允许Web流量通过的防火墙了。

### Agent代理
&emsp;&emsp; 用户Agent代理，是代表用户发起HTTP请求的客户端程序。所有发布Web请求的应用程序都是HTTP Agent代理。Web浏览器就是一种HTTP Agent代理。

<h2 id="2">2.URL与资源</h2>
&emsp;&emsp; URL第一部分是URL方案(scheme)，如HTTPS，第二部分是服务器位置，如www.baidu.com，第三部分是资源路径。

### 密码
&emsp;&emsp; URL可以带用户名和密码，如http://lix:lix_password@www.xxx.com/index.html 字符@将用户名和密码组件与URL其余的部分分隔开来，
用户名密码之间用":"分隔。

### 参数
&emsp;&emsp; URL参数由字符";"，将其与URL的其余部分分隔开来。为应用程序提供了访问资源所需的所有附加信息。如：ftp://prep.ai.mit.edu/pub/gnu;type=d
每段路径都可以有自己的参数，如：http://aaa.com/hammers;sale=false/index.html;graphics=true

### 查询字符串
&emsp;&emsp; "?" 作为查询组件。名值对之间使用字符"&"分隔。

### 片段
&emsp;&emsp; 为了引用部分资源或者资源的一个片段，URL支持使用片段(frag)组件来表示一个资源内部的片段，比如URL可以指向HTML文档中一个特定的图片或小节。
片段用"#"表示，如：http://www.xxx.com/index.html#drills。HTTP服务器通常只处理整个对象，而不是对象的片段，客户端不能将片段传给服务器，
浏览器从服务器获得了整个资源后，会根据片段显示感兴趣的部分资源。

### 字符
&emsp;&emsp; 人们可能希望URL中包含除通用的安全字母表之外的二进制数据或字符，因此需要有一种转义机制，能将不安全的字符编码为安全字符，再进行传输。

<br>
&emsp;&emsp; 通过一种转义表示法来表示不安全字符，这种转义表示法包含一个百分号后面跟着两个表示字符ASCII码的十六进制数。


<h2 id="3">3.HTTP报文</h2>
&emsp;&emsp; HTTP报文是在HTTP应用程序之间发送的数据块。这些数据块以一些文本形式的元信息(meta-information)开头，这些信息描述了报文的内容和含义，
后面跟着可选的数据部分。

<br>
&emsp;&emsp; 报文的起始行和首部是由行分隔的ASCII文本，每行以一个由两个字符组成的行终止序列作为结束，其中包含一个回车符和一个换行符，称为CRLF。
稳健的应用程序应该接受单个换行符作为行的终止。有些老的不完整的HTTP应用程序并不总是既发送回车符又发送换行符。报文的主体可以包含文本或者二进制数据，也可以为空。

### 报文的语法
&emsp;&emsp; 请求报文和响应报文的格式只有起始行语法有所不同。
    
        这是请求报文格式：注意空行
        <method> <request-url> <version>
        <headers>
        
        <entity-body>
        
        这是响应报文格式：注意空行
        <version> <status> <response-phrase>
        <headers>
        
        <entity-body>
        
<br>
&emsp;&emsp; 方法(method): GET、HEAD、PUT等。请求URL(request-url): 命名来所请求资源，或者URL路径组件的完整URL。版本(version)：报文所使用的HTTP版本。
状态码(status-status): 描述了请求过程中所发生的情况。原因短语(reason-phrase)：数字状态码的可读版本。首部(header)：每个首部包含一个名字，
后面跟一个冒号(:)，然后是一个可选的空格，接着是一个值，最后是一个CRLF。

    
    请求报文：
    GET /text/hi-there.txt HTTP/1.1
    Accept: text/*
    Host: www.xxx.com
    
    
    响应报文：
    HTTP/1.1 200 OK
    Content-type: text/plain
    Content-length: 19
    
    Hi,I'm a message!
    
### 方法
&emsp;&emsp; GET、HEAD(只从服务器获取文档的首部)、POST、PUT、TRACE(对可能经过代理服务器传送到服务器上的报文进行追踪)、
OPTIONS(决定可以在服务器上执行哪些方法)、DELETE。

### 状态码
&emsp;&emsp; 200-299表示成功，300-399表示资源已经被移走了，400-499表示客户端请求出错了，500-599表示服务器出错了。