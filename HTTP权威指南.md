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


### 首部
&emsp;&emsp; HTTP规范定义了几种首部字段，应用程序也可以随意发明自己所用的首部。首部分为：通用首部、请求首部、响应首部、实体首部、扩展首部。
长的首部可以分为多行，多出来的每行前面至少要有一个空格或制表符。

    常见的首部实例
    Date:Tue,3Oct 1997 02:16:03 GMT  服务器产生响应的时间
    Content-length:15040             实体的主体部分包含了15040字节的数据
    Content-type:image/gif           实体的主体部分是一个GIF图片
    Accept:image/gif, image/jpeg, text/html 客户端可以接收GIF、JPEG、HTML
    
    表示Server的值为：Test Server Version 1.0
    HTTP/1.1 200 OK
    Content-Type:image/gif
    Content-Length:8572
    Server:Test Server
        Version 1.0
        
<h3>通用首部</h3>
&emsp;&emsp; 有些首部只提供了与报文相关的最基本的信息，被称为通用首部，不论报文是什么类型，都为其提供一些有用的信息。
    
    Connection  允许客户端和服务器指定与请求／响应连接有关的选项
    Date        提供日期和时间标志，说明报文是什么时间创建的
    MIME-Version 给出发送端使用的MIME版本
    Trailer      如果报文采用分块传输编码方式，就可以用这个首部列出位于报文拖挂(trailer)部分的首部集合
    Transfer-Encoding  告知接收端为了保证报文的可靠传输，对报文采用了什么编码方式
    Update       给出了发送端可能想要升级使用的新版本或协议
    Via          显示了报文经过的中间节点
    
### 通用缓存首部
&emsp;&emsp; HTTP/1.0引入了第一个允许HTTP应用程序缓存对象本地副本的首部。
    
    Cache-Control  用于随报文传送缓存指示
    Pragma         另一种随报文传送指示的方式，但并不专用于缓存
    
<h3>请求首部</h3>
&emsp;&emsp; 请求首部是只在请求报文中有意义的首部。
    
    Client-IP  提供了运行客户端的机器的IP地址
    From       提供了客户端用户的E-mail地址
    Host       提供了接收请求的服务器的主机名和端口号
    Referer    提供了包含当前请求URI的文档的URL
    UA-Color   提供了与客户端显示器的显示颜色有关的信息
    UA-CPU     给出了客户端CPU的类型或制造商
    UA-Disp    提供了与客户端显示器能力有关的信息
    UA-OS      给出了运行在客户端机器上的操作系统名称及版本
    UA-Pixels  提供了客户端显示器的像素信息
    User-Agent 将发起请求的应用程序名称告知服务器
    
### Accept首部
&emsp;&emsp; Accept首部为客户端提供一种将其喜好和能力告知服务器的方式。
    
    Accept          告诉服务器能发送哪些媒体类型
    Accept-Charset  告诉服务器能发送哪些字符串
    Accept-Encoding 告诉服务器能发送哪些编码方式
    Accept-Language 告诉服务器能发送哪些语音
    TE              告诉服务器可以使用哪些扩展传输编码
    
### 条件请求首部
&emsp;&emsp; 有时客户端希望为请求加上限制。

    Expect         允许客户端列出某请求所要求的服务器行为
    If-Match       如果实体标记与文档当前的实体标记相匹配，就获取这份文档
    If-Modified-Since 除非在某个指定的日期后资源被修改过，否则限制这个请求
    If-None-Match  如果提供的实体标记与当前文档的实体标记不相符，就获取文档
    If-Range       允许对文档的某个范围进行条件请求
    If-Unmodified——Since  除非在某个日期后资源没被修改过，否则限制这个请求
    Range          如果服务器支持范围请求，就请求资源的指定范围
    
### 安全请求首部
&emsp;&emsp; HTTP支持对请求进行质询／响应认证。这种机制要求客户端在获取特定资源之前，先对自身进行认证。

    安全请求首部：
    Authorization：   包含了客户端提供给服务器，以便对其自身进行认证的数据
    Cookie：          客户端用它向服务器传送一个令牌---它不是真正的安全首部，但确实隐含了安全功能
    Cookie2:          用来说明请求端支持的Cookie版本
    
### 代理请求首部
    
    Max-Forward           在通往源端服务器的路径上，将请求转发至其他代理或网关的最大次数，与TRACE方法一同使用
    Proxy-Authorization   与Authorization首部相同，但这个首部是在与代理进行认证时使用的
    Proxy-Connection      与Connection首部相同，但这个首部是在与代理建立连接时使用的
    
<h3>响应首部</h3>
&emsp;&emsp; 响应首部为客户端提供了一些额外信息，甚至与响应相关的一些特殊指令。
    
    Age           从创建开始，响应持续时间
    Public        服务器为其资源支持的请求方法列表
    Retry-After   如果资源不可用的话，在此日期或时间重试
    Server        服务器应用程序软件的名称和版本
    Title         对HTML文档来说，就是HTML文档的源端给出的标题
    Warning       比原因短语更详细一些的警告报文
    
### 协商首部
&emsp;&emsp; 如果资源有多种表示方法，服务器可以用它们来传递与协商资源有关的信息。

    Accept-Range    对此资源来说，服务器可接受的范围类型
    Vary            服务器查看的其他首部的列表，可能会使响应发生变化，也就是说，这是一个首部列表，服务器会根据这些首部的内容挑选出最适合的资源版本发送给客户端
    
### 安全响应首部
&emsp;&emsp; 这是HTTP的质询／响应认证机制的响应侧。

    Proxy-Authenticate    来自代理的对客户端的质询列表
    Set-Cookie            不是真正的安全首部，但隐含有安全功能，可以在客户端设置一个令牌，以便服务器对客户端进行标识
    WWW-Authenticate      来自服务器的对客户端的质询列表
    
<h3>实体首部<h3>
&emsp;&emsp; 