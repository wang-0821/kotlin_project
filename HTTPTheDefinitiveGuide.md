## Http权威指南

* [1.概述](#1)
* [2.URL与资源](#2)
* [3.HTTP报文](#3)
* [4.连接管理](#4)
* [5.Web服务器](#5)
* [6.代理](#6)
* [7.缓存](#7)
* [8.集成点：网关、隧道及中继](#8)
* [9.Web机器人](#9)
* [10.基本认证机制](#10)
* [11.摘要认证](#11)
* [12.安全HTTP](#12)
* [13.实体和编码](#13)


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
    Host       提供了接收请求的服务器的主机名和端口号。 如果HTTP请求报文中URL是绝对的，就忽略Host首部的值。
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
&emsp;&emsp; 实体信息性首部。
    
    Allow        列出了可以对此实体执行的请求方法
    Location     告知客户端实体实际位于何处，用于将接收端定向到资源的位置上去
    
### 内容首部
&emsp;&emsp; 内容首部提供了与实体内容有关的特定信息。

    Content-Base       解析主体中的相对URL时使用的基础URL
    Content-Encoding   对主体执行的任意编码方式
    Content-Language   理解主体时最适宜使用的自然语言
    Content-Length     主体的长度或尺寸
    Content-Location   资源实际所处的位置
    Content-MD5        主体的MD5检验和
    Content-Range      在整个资源中此实体表示的字节范围
    Content-Type       这个主体的对象类型
    
### 实体缓存首部
&emsp;&emsp; 通用的缓存首部说明了如何或什么时候进行缓存，实体缓存首部提供了与被缓存实体有关的信息。
    
    ETag           与此实体相关的实体标记
    Expire         实体不再有效，要从原始的源端再次获取此实体的日期和时间
    Last-Modified  这个实体最后一次被修改的日期和时间
    
<h2 id="4">4.连接管理</h2>
&emsp;&emsp; HTTP传送一条报文时，会以流的形式将报文数据的内容通过TCP按序传输。TCP接到数据后，会将数据流切分成被称为段的小数据块，
并将段封装在IP分组中，通过因特网传输。每个TCP段都是由IP分组承载，从一个IP地址发送到另一个IP地址的。
每个IP分组中包含：一个IP分组首部(通常为20字节)；一个TCP段首部(通常为20字节);一个TCP数据块(0或多个字节)。
    
    source-IP-address, source-port, destination-IP-address, destination-port 四个参数构成唯一的一条连接。

<br>
&emsp;&emsp; IP首部包含了源和目的IP地址、长度和其他的一些标记。TCP段的首部包含了TCP端口号、TCP控制标记、以及用于数据排序和完整性检查的一些数字值。

<h3>4.1HTTP时延</h3>
&emsp;&emsp; HTTP事务的时延
    
    服务器     ^ |     ^  |
       _______| |___  |  |
      | DNS查询     |  |  |
      |            V  |  V
    客户端          请求  响应
    
&emsp;&emsp; 与建立TCP连接，以及传输请求和响应报文的时间相比，事务处理时间可能是很短的，除非客户端或服务器超载，或正在处理复杂的动态资源，
否则HTTP时延就是由TCP网络时延构成的。

### HTTP时延
&emsp;&emsp; HTTP时延的原因主要有几种。
    
    1.客户端首先要根据URI确定web服务器的IP地址和端口号。如果最近没有对URI中主机名进行访问，通过DNS解析系统将URI中的主机名转换成一个IP地址，
    可能要花费数十秒的时间。
    2.接下来，客户端会向服务器发送一条TCP连接请求，并等待服务器返回一个请求接受应答。每条新的TCP连接都会有连接建立时延。这个值通常只有一两秒，
    但如果有数百个HTTP事务，这个值会快速叠加。
    3.一旦连接建立，客户端会通过新建立的TCP管道来发送HTTP请求。数据到达时，Web服务器会从TCP连接中读取请求报文，并对请求进行处理。
    传输请求报文以及服务器处理请求报文都需要时间。
    4.随后，Web服务器会返回HTTP响应，这也需要时间。
    
&emsp;&emsp; 最常见的TCP时延包括：1，TCP连接建立握手。2，TCP慢启动拥塞控制。3，数据聚集的Nagle算法。4，用于捎带确认的TCP延迟确认算法。
5，TIME_WAIT时延和端口耗尽。

### TCP连接的握手时延
&emsp;&emsp; 建立一条新的TCP连接时，甚至在发送任意数据之前，TCP软件之间会交换一系列的IP分组，对连接的有关参数进行沟通。
如果连接只用来传送少量数据，这些交换过程就会严重降低HTTP性能。

    TCP连接握手步骤
        1，请求新的TCP连接时，客户端要向服务器发送一个小的TCP分组(通常40-60字节)，这个分组中设置了一个特殊的SYN标记，说明是一个连接请求。
        2.如果服务器接受了连接，就会对连接参数进行计算，并向客户端返回一个TCP分组，这个分组中的SYN和ACK标记都被置位，说明连接请求已被接受。
        3，客户端向服务器发送一条确认信息，通知它连接已经成功建立，现在的TCP栈都允许客户端在这个确认分组中发送数据。
        
&emsp;&emsp; 路由器超负荷可以随意丢弃分组，TCP实现了自己的确认机制来确保数据的成功传输，每个TCP段都有一个序列号和数据完整性校验和。
每个接收者收到完好的段时，都会向发送者回送小的确认分组，如果发送者没有在指定窗口时间内收到确认信息，发送者就会认为分组已经被损坏或损毁，重发数据。

### TCP慢启动
&emsp;&emsp; TCP传输的性能还取决于TCP连接的使用期。TCP会随着时间进行自我调节，最开始会限制连接的最大速度。如果数据传输成功，会提高传输的速度，
这种调节被称为TCP慢启动，用于防止网络突然过载和拥塞。TCP慢启动，必须在发送一个分组，确认后才可以发送两个分组。因此一个新的TCP连接，
传输性能不如持久的连接。

### Nagle算法与TCP_NODELAY
&emsp;&emsp; TCP有一个数据流接口，应用程序可以通过它将任意尺寸的数据放入TCP栈中，但是每个TCP段中，至少包含了40个字节的标记和首部，
所以如果发送大量包含小数据的分组，网络性能会有下降。Nagle算法试图在发送一个分组前，将大量TCP数据绑定在一起，提高网络效率。Nagle算法鼓励全尺寸的段。
LAN上最大尺寸的分组约是1500字节，因特网上是几百字节。只有当其他分组都被确认后，Nagle才允许发送非全尺寸的分组。如果其他分组仍在传输过程中，
就将那部分数据缓存起来。Nagle算法存在几种HTTP性能问题，小的HTTP报文可能无法填满一个分组，其次Nagle算法与延迟确认之间的交互存在问题，
Nagle算法会阻止数据的发送，直到有确认分组抵达为止，但确认分组自身会被延迟确认算法延迟100-200毫秒。

<br>
&emsp;&emsp; HTTP应用程序常常会自己在栈中设置参数TCP_NODELAY，禁止Nagle算法提高性能，这么做要确保向TCP中写入大块的数据，这样就不会产生一堆小分组。

### TIME_WAIT累积与端口耗尽
&emsp;&emsp; TIME_WAIT端口耗尽是很严重的性能问题，当TCP端点关闭TCP连接时，会在内存中维护一个控制块，用来记录最近关闭的IP地址和端口号，
这种信息会维持一段时间，通常是最大分段使用期的两倍，称为2MSL(Max Segment Lifetime，通常2分钟)，以此确保这段时间内不会创建具有相同IP地址和端口的新连接。

    客户端每次连接到服务器时，只有源端口是可以变得，因此每次连接都会获得一个新的源端口号，以实现连接的唯一性。
    
<h3>4.2HTTP连接的处理</h3>
&emsp;&emsp; Connection首部字段中有一个逗号分隔的连接标签列表，这些标签为此连接指定了一些不会传播到其他连接中去的选项。close用来说明
发送完下一条报文后必须关闭的连接。

    HTTP/1.1 200 OK
    Cache-control:max-age=3600
    Connection:meter, close, bill-my-credit-card
    Meter:max-uses=3, max-refuses=6, dont-report
    
    首部说明：不应该转发Meter首部，要用假想的bill-my-credit-card选项，且本次事务结束后应关闭持久连接。
    
### 串行事务处理时延
&emsp;&emsp; 串行的建立多个连接，那么连接时延和慢启动时延就会叠加起来。目前有几种可以提升HTTP性能的方式：1，并行连接；通过多条TCP连接发起并发的HTTP请求。
2，持久连接；重用TCP连接，以消除连接及关闭时延。3，管道化连接；通过共享的TCP连接发起并发的HTTP连接。4，复用的连接；交替传送请求和响应报文（实验阶段）。

### 并行连接
&emsp;&emsp; 并行连接的时延是重叠的，并行连接的速度可能会更快，但不宜采用大量连接。

### 持久连接
&emsp;&emsp; 在事务处理结束之后仍然保持在打开状态的TCP连接，称为持久连接。重用持久连接可以避免缓慢的连接建立状态。而且已经打开的连接，
还可以避免慢启动的拥塞适应阶段，以便更快速地进行数据的传输。

#### HTTP/1.0 Keep-Alive
<br>
&emsp;&emsp; 支持HTTP／1.0 keep-alive连接的客户端可以通过包含Connection:Keep-Alive首部请求将一条连接保持在打开状态。
如果服务器愿意为下一条请求将连接保持在打开状态，就在响应中包含相同的首部。如果响应中没有Connection: Keep-Alive首部，客户端就认为服务器不支持keep-alive，
会在响应报文之后关闭连接。

<br>
&emsp;&emsp; 可以用Keep-Alive通用首部中指定的、由逗号分隔的选项来调节Keep-Alive的行为。参数timeout是在Keep-Alive响应首部发送的，
估计了服务器希望将连接保持在活跃状态的时间，这并不是一个承诺值。参数max是Keep-Alive响应首部发送的，估计了服务器还希望为多少个事务保持此连接的活跃状态。
Keep-Alive首部还支持任意未经处理的属性，这些属性主要用于诊断和调试，语法为name [=value]。

<br>
&emsp;&emsp; 盲中继代理只是将字节从一个连接转发到另一个连接中去，不对Connection首部进行特殊的处理。这种情况客户端拿到的Keep-Alive实际是无效的。
为避免此类代理问题的产生，现代代理不能转发Connection首部和所有名字出现在Connection值中的首部。可以通过Proxy-Connection来解决盲中继问题。

#### HTTP/1.1 Keep-Alive
&emsp;&emsp; HTTP/1.1默认情况下是持久连接，要在事务处理后关闭，必须向报文中显式的添加Connection：close首部。不发送Connection：close，
不意味服务器永远将连接保持在打开状态。

### 管道化连接
&emsp;&emsp; HTTP/1.1允许在持久连接上使用请求管道，在响应到达之前，可以将多条请求放入队列。1，如果HTTP客户端无法确认连接是持久的，就不应该使用管道。
2，必须按照与请求相同的顺序回送HTTP响应。3，HTTP客户端必须做好连接会在任意时刻关闭的准备，并准备好重发所有未完成的管道化请求。
4，HTTP客户端不应该用管道化的方式发送会产生副作用的请求(比如POST)。

#### Content-Length及截尾操作
&emsp;&emsp; 每条HTTP响应都应该有精确的Content-Length首部。客户端或代理收到一条随连接关闭而结束的HTTP响应，且实际传输的实体长度与Content-Length
并不匹配（或没有Content-Length）时，接收端就应该质疑长度的正确性。

<h2 id="5">5.Web服务器</h2>
&emsp;&emsp; Web服务器会执行几项任务：

    1，建立连接，接受一个客户端连接，或者不希望这个客户端连接，直接关闭。
    2，接收请求，从网络中读取一条HTTP请求报文。
    3，处理请求，对请求进行解释，并采取行动。
    4，访问资源，访问报文中指定的资源。
    5，构建响应，创建带有正确首部的HTTP响应报文。
    6，发送响应，将响应回送给客户端。
    7，记录事务处理过程，将与已完成事务有关的内容记录在一个日志文件中。
    
### 重定向
&emsp;&emsp; 重定向返回3XX状态码，Location响应首部包含了内容的新地址或优选地址的URI。重定向包含以下几种情况：
    
    1,永久删除的文件；资源已经被移动到了新的位置，或者被重新命名，有了一个新的URL。
    2，临时删除的文件；资源临时被移走或者重命名了，服务器希望将客户端重定向到新的位置上去。
    3，URL增强；服务器通常用重定向来重写URL，用于嵌入上下文。状态码303 See Other或者307 Temporary Redirect用于此类重定向。
    4，负载均衡；一个超载的服务器，在收到一条请求时，服务器可以重定向到一个负载不太重的服务器上，303、307用于此类重定向。
    5，服务器关联；Web服务器可能有某些用户的本地信息，服务器可以将客户端重定向到包含了那个客户端信息的服务器上去。303、307可用于此类重定向。
    6，规范目录名称；客户端请求的URI是一个不带尾部斜线的目录名称时，大多数Web服务器会将客户端重定向到一个加了斜线的URI上，这样相对链接就能正常工作了。

<h2 id="6">6.代理</h2>
&emsp;&emsp; 代理服务器可以是某个客户端专用的，也可以是多个客户端共享的。单个客户端专用的代理被称为私有代理，众多客户端共享的代理被称为公共代理。
代理连接的是两个或多个使用相同协议的应用程序，而网关连接的则是两个或多个使用不同协议的端点。网关扮演的是协议转换器的角色。

### 反向代理
&emsp;&emsp; 反向代理可以假扮web服务器，接收发送给web服务器的真实请求，但与web服务器不同的是，它们可以发起与其他服务器的通信，以便按需定位所请求的内容。
可以用这些反向代理来提高访问慢速web服务器上公共内容时的性能。在这种配置中，通常将这些反向代理称为服务器加速器。反向代理通常会直接冒用Web服务器的名字和IP地址，
这样所有的请求就会被发送给代理而不是服务器了。

### 匿名者
&emsp;&emsp; 匿名者代理会主动从HTTP报文中删除身份特性，从而提供高度的私密性和匿名性。匿名代理会对用户进行下列修改以增加私密性。
    
    1，从User-Agent首部删除用户的计算机与OS类型。
    2，删除From首部以保护用户的E-mail地址。
    3，删除Referer首部来掩盖用户访问过的其他站点。
    4，删除Cookie首部以删除概要信息和身份的数据。
    
### Via首部
&emsp;&emsp; Via首部字段列出了与报文途经的每个中间节点(代理或网关)有关的信息。报文每经过一个节点，都必须将这个中间节点添加到Via列表的尾部。
每个Via最多包含4个组件：一个可选的协议名(默认HTTP)、一个必须的协议版本、一个必须的节点名、一个可选的描述性注释。如果不是HTTP协议，
需要在版本前加上协议名，中间用"/"分隔。Via响应报文会沿着请求报文相同的路径回传，因此响应Via首部基本上总是与请求Via首部相反。

    Via: 1.1 proxy.aaa.net, 1.0 cache.bbb.com
    
### TRACE方法
&emsp;&emsp; HTTP/1.1的TRACE方法会跟踪经代理链传输的请求报文，当TRACE请求到达目的服务器时，整条请求报文会被封装在一条HTTP响应的主体中
回送给发送端。当TRACE响应到达时，客户端可以检查服务器收到的确却报文，以及它所经过的代理列表(Via首部)。
TRACE响应的Content-Type为message/http，状态为200。

    // TRACE请求
    TRACE /index.html HTTP/1.1
    Host: www.aaa.com
    Accept: text/html
    
    // TRACE响应
    HTTP/1.1 200 OK
    Content-Type: message/http
    Content-Length: 269
    Via: 1.1 proxy1.com, 1.1 proxy2.com, 1,1 proxy3.com
    
    TRACE /index.html HTTP/1.1
    Host: www.aaa.com
    Via: 1.1 proxya.net, 1.1 proxyb.net, 1.1 proxyc.net
    Client-ip: 202.134.1.1

<br>
&emsp;&emsp; 可以使用Max-Forwards首部来限制TRACE和OPTIONS请求所经过的代理跳数，在测试代理链是否是在无限循环中转发报文，
或者查看链中特定代理服务器的效果时，是很有效的。如果Max-Forwards的值为零，那么即使接收者不是原始服务器，它也必须将TRACE报文返回给客户端，
而不应该继续转发。

<h2 id="7">7.缓存</h2>
&emsp;&emsp; 服务器可以通过HTTP的几种首部来指定在文档过期之前可以将其缓存多长时间。按照优先级递减的排序，可以分以下几种方式。

    1，添加一个Cache-Control: no-store 首部
    2，添加一个Cache-Control: no-cache 首部
    3，添加一个Cache-Control: must-revalidate 首部
    4，添加一个Cache-Control: max-age首部
    5，添加一个Expires 日期首部到响应中去。
    6，不添加过期信息，让缓存自己确定自己的过期时间。
    
### no-store 与 no-cache
&emsp;&emsp; no-store会禁止对响应进行复制。缓存通常会想非缓存代理服务器一样，向客户端发送一条no-store响应，然后删除对象。
no-cache实际上是可以缓存在本地缓存区中的，只是在与原始服务器进行新鲜度再验证之前，缓存不能将其提供给客户端使用。

### must-revalidate
&emsp;&emsp; 一旦资源过期(比如已经超过max-age)，在成功向原始服务器验证之前，缓存不能用该资源响应后续请求。

### max-age
&emsp;&emsp; 表示从服务器将文档传来之时起，可以认为文档将处于新鲜状态的秒数，还有一个s-maxage首部，其行为与max-age类似，但仅适用于共享缓存。

    Cache-Control: max-age=3600
    Cache-Control: a-maxage=3600
    
### Expires
&emsp;&emsp; 不推荐使用Expires首部，它实际指定的是实际的过期时间，而不是秒数。

### 客户端的新鲜度限制
&emsp;&emsp; 客户端可以用Cache-Control请求首部来强化或放松对过期时间的限制。客户端可以用Cache-Control使过期时间更加严格，也可能会放松新鲜度要求。

    Cache-Control请求指令
    Cache-Control: max-stale
    Cache-Control: max-stale = <s>  缓存可以随意提供过期时间。如果指定了参数<s>，在这段时间内，文档就不能过期。这条指令放松了缓存的规则。
    
    Cache-Control: min-fresh=<s>  至少在未来<s>秒内文档要保持新鲜。这就使缓存规则更加严格了。
    
    Cache-Control: max-age=<s>   缓存无法返回缓存时间长于<s>秒的文档，这条指令会使缓存规则更严格，除非同时还发送了max-stale指令，这种情况下使用期可能超过过期时间。
    
    Cache-Control: no-cache   除非资源进行了再验证，否则这个客户端不会接受已缓存的资源。
    
    Cache-Control: no-store  缓存应该尽快从存储器中删除文档的所有痕迹，因为其中可能会包含敏感信息。
    
    Cache-Control: only-if-cached  只有当缓存中有副本存在时，客户端才会获取一份副本。
    
<h2 id="8">8.集成点：网关、隧道及中继</h2>
### 网关
&emsp;&emsp; 可以用一个斜杠来分隔客户端和服务器端的协议，并以此对网关进行描述：

    <客户端协议>/<服务器端协议>
    服务器端网关通过HTTP与客户端对话，通过其他协议与服务器通信(HTTP/*)
    客户端网关通过其他协议与客户端对话，通过HTTP与服务器通信(*/HTTP)
   
### 隧道
&emsp;&emsp; Web隧道允许用户通过HTTP连接发送非HTTP流量，使用Web隧道最常见的原因就是要在HTTP连接中嵌入非HTTP流量，
这样这类流量就能穿过只允许Web流量通过的防火墙了。

<br>
&emsp;&emsp; 用CONNECT可以建立HTTP隧道

    1，客户端发送一条CONNECT请求给隧道网关。客户端的CONNECT方法请求隧道网关打开一条TCP连接。
    2，网关与服务器建立TCP连接。
    3，建立完TCP连接，网关会发送一条HTTP 200 Connection Established 响应来通知客户端。
    4，此时隧道就建立起来了，客户端通过HTTP隧道发送的所有数据都会被直接转发给输出TCP连接，服务器端发送的所有数据都会通过HTTP隧道转发给客户端，
    
    CONNECT orders.aaa.com:443 HTTP/1.0
    User-agent: xxx
    
<br>
&emsp;&emsp; SSL隧道。当加密SSL无法通过传统的代理服务器转发时，可以通过一条HTTP连接来传输SSL流量，以穿过端口80的HTTP防火墙。

<h2 id="9">9.Web机器人</h2>
&emsp;&emsp; Web机器人是能够在无需人类干预的情况下自动进行一系列web事务处理的软件程序。Web爬虫是一种web机器人，爬虫开始访问的URL初始集合
被称作根集。一个好的根集会包括一些大的流行Web站点、一个新创建页面的列表和一个不经常被链接的无名页面列表。机器人在Web上爬行时，要避免环路的出现，
环路会造成机器人陷阱，这些陷阱会暂停或减缓机器人的爬行进程。

    1，环路会使爬虫陷入可能将其困住的循环中。循环会使爬虫消耗带宽，可能完全无法获取其他页面。
    2，爬虫爬取相同页面时，另一端的Web服务器也在遭受打击，可能会击垮Web站点，阻止所有真实用户访问这个站点。
    3，循环自身爬虫应用程序会被重复内容所充斥，这样应用程序会变得毫无用处。返回数百份完全相同页面的因特网搜索引擎就是一个这样的例子。
    
<br>
&emsp;&emsp; 大规模Web爬虫对其访问过的地址进行管理时使用的一些有用的技术如下。

    1，树和散列表。复杂的机器人可能会用搜索树或散列表来记录已访问的URL。这些是加速URL查找的软件数据结构。
    2，有损的存在位图。为了减少空间，一些大型爬虫会使用有损数据结构，比如存在位数组，用一个散列函数将每一个URL都转换成一个定长的数字，
        这个数字在数组中有个相关的存在位。爬过一个URL时，就将响应的存在位置位，如果存在位已经置位了，爬虫就认为已经爬过那个URL了。
    3，检查点。一定要将已访问URL列表保存到硬盘上，以防机器人程序崩溃。
    4，分类。大型Web机器人会使用机器人集群，每个独立的计算机是一个机器人，为每个机器人fenpeiyigetedingdeURl片，让这些机器人配合工作。
    
### 规范化URL
&emsp;&emsp; 由于URL别名的存在，如是否转义的区别，会导致不同的URL指向的是相同的资源。大多数Web服务器都试图通过将URL规范化为标准格式来消除别名。
经过这些步骤可以消除部分别名问题，

    1，如果没有指定端口的话，就向主机名中添加80。
    2，将所有转义符%xx转换为等价字符。
    3，删除#标签。

<br>
&emsp;&emsp; 文件系统中的符号连接会造成特定的潜在环路，因为它们会在目录层次深度有限的情况下，造成深度无限的假象。通常使用下列方式来避免循环和重复。
    
    1，规范化URL。将URL转换为标准形式以避免语法上的别名。
    2，广度优先的爬行。以广度优先的方式来调度URL去访问Web站点，就可以将环路的影响最小化。即使遇到了陷阱也可以在回到环路中获取下一个页面前，
        从其他Web站点获取成百上千的页面。
    3，节流。限制一段时间内机器人可以从一个Web站点获取的页面数量。
    4，限制URL的大小。机器人拒绝爬行超出特定长度(通常1KB)的URL。如果环路使URL长度增加，长度限制会终止这个环路。
    5，URL／站点黑名单。
    6，模式检测。文件系统的符号连接和类似的错误配置所造成的环路会遵循某种模式。比如URL会随着组件的复制逐渐增加。有些机器人会将具有重复组件的URL
        当作潜在的环路，拒绝爬行带有多于两或三个重复组件的URL。
    7，内容指纹。使用内容指纹的机器人会获取页面内容中的字节，计算出一个校验和。通过检验和来判断是否爬过这个页面。
    8，人工监视。设计所有产品级机器人，都要有诊断和日志功能，对机器人进展进行监视。
    
<h2 id="10">10.基本认证机制</h2>
&emsp;&emsp; HTTP定义了两个官方的认证协议：基本认证和摘要认证。

    认证的四个步骤
    1，客户端发送第一条请求，没有认证信息。
    2，服务器用401状态码拒绝了请求，说明需要用户提供用户名和密码。服务器可能会分为不同的区域，每个区域都有自己的密码，
        所以服务器会在www-Authenticate首部对保护区域进行描述。同样认证算法也是在www-Authenticate首部中指定的。
    3，客户端重新发出请求，但这次会附加一个Authorization首部，用来说明认证算法、用户名和密码。
    4，如果授权证书是正确的，服务器会将文档返回。有些算法会在可选的Authentication-Info首部返回一些与授权对话相关的附加信息。
    
<br>
&emsp;&emsp; HTTP基本认证使用Base-64进行编码，基本认证使用冒号将用户名和密码打包在一起。
    
    GET /index.jpg HTTP/1.0
    Authorization: Basic xsdsdsdsferewrer      实际为经过Base-64编码后的字符串，原本为：xxx:yyy

<h2 id="11">11.摘要认证</h2>
&emsp;&emsp; 基本认证便捷灵活，用户名密码都是明文传送，也没有措施防止对报文的篡改，安全使用基本认证的唯一方式是将其与SSL配合使用。
摘要认证与基本认证兼容，但是更安全。

    摘要认证不会以明文方式发送密码。
    可以防止恶意用户捕获并重放认证的握手过程。
    可以有选择地防止对报文内容的篡改。
    防范其他几种常见的攻击方式。
    
<h2 id="12">12.安全HTTP</h2>
&emsp;&emsp; HTTPS是最流行的HTTP安全形式。使用HTTPS时，所有的HTTP请求和响应数据在发送到网络之前，都要进行加密。
HTTPS在HTTP下面提供了一个传输级的密码安全层，可以使用SSL也可以使用其后继者--传输层安全(TLS)。

### 对称密钥加密技术
&emsp;&emsp; 发送端和接收端共享相同的密钥进行通信。常见的对称密钥加密算法包括：DES、Triple-DES、RC2和RC4。通常认为128位DES密钥实际上
是任何人以任何代价都无法通过暴力攻击破解的。

### 公开密钥加密技术
&emsp;&emsp; 公开密钥加密技术使用两个非对称密钥：一个用来对主机报文编码，一个用来对主机报文解码。编码密钥是众所周知的，
但只有主机才知道私有的解密密钥。公开密钥加密算法计算会比较慢，常见的做法是两个点之间先通过便捷的公开密钥加密技术建立安全通信，
然后再用安全通道产生并发送临时的随机对称密钥，通过更快的对称加密技术对其余的数据进行加密。

#### RSA
&emsp;&emsp; RSA算法是一个公开密钥加密系统，是MIT发明的。

### 数字签名
&emsp;&emsp; 除了加／解密报文外，还可以用加密系统对报文进行签名(sign)，以说明是谁编写的报文，同时证明报文未被篡改过。这种技术被称为数字签名。
数字签名通常使用非对称公开密钥技术产生，只有所有者才知道私有密钥。

    节点A向节点B发送一条报文，并对其进行签名。
    1，节点A将变长报文提取为一个定长的摘要。
    2，节点A对摘要应用了一个签名函数，这个函数会将用户的私有密钥作为参数。
    3，一旦计算出签名，节点A将其附加在报文的末尾，并将报文和签名都发给B。
    4，节点B对签名进行检查，节点B接收经私有密钥扰码的签名，并应用了使用公开密钥的反函数，如果拆包后的摘要与节点B自己的摘要版本不匹配，
        说明要么报文在传输过程中被篡改了，要么发送端没有节点A的私有密钥。
        
### 数字证书
&emsp;&emsp; 数字证书中包含了由某个受信任组织担保的用户或公司的相关信息。证书通常还包含对象的公开密钥，以及对象和所用签名算法的描述性信息。
    
    基本的数字证书通常包含一些常见内容，如：
    对象的名称(人、组织、服务器等)
    过期时间
    证书发布者
    来自证书发布者的数字签名
    
### X.509 v3证书
&emsp;&emsp; 目前使用的证书大多数都使用一种标准格式--X.509 v3，来存储它们的信息。

    X.509证书字段
    版本               这个证书的X.509证书版本号，现在使用的通常都是版本3。
    序列号              证书颁发机构(CA Certificate Authority)生成的唯一整数。CA生成的每个证书都要有一个唯一的序列号。
    签名算法ID          前所使用的加密算法。例如："用RSA加密的MD2摘要"。
    证书颁发者          发布并签署这个证书的组织名称，以X.500格式表示。
    有效期              此证书何时有效，由一个起始日期和一个结束 日期来表示。
    对象名称            证书中描述的实体，比如一个人或一个组织。对象名称是以X.500格式表示的。
    对象的公开密钥信息    证书对象的公开密钥，公开密钥使用的算法，以及所有附加参数。
    发布者唯一的ID(可选)  可选的证书发布者唯一标识符，这样就可以重用相同的发布者名称。
    对象唯一的ID(可选)    可选的证书对象唯一标识符，这样就可以重用相同的对象名称了。
    扩展                 可选的扩展字段集，每个扩展字段都被标识为关键或非关键的。关键扩展很重要，证书使用者一定要能理解。
                         如果证书使用者无法识别出关键扩展字段，就必须拒绝这个证书。目前在使用的常见扩展字段包括：
                         基本约束(对象与证书颁发机构的关系)；证书策略(授予证书的策略)；密钥的使用(对公开密钥使用的限制)；
     
    证书的颁发机构签名     证书颁发机构用指定的签名算法对上述所有字段进行的数字签名。

<br>
&emsp;&emsp; HTTPS中，客户端首先打开一条到服务器端口443的连接，一旦建立连接，客户端和服务器会初始化SSL层，对加密参数进行沟通，并交换密钥。
握手完成后，SSL初始化就完成了，客户端就可以将请求报文发送给安全层了。在将报文发送给TCP之前，要先对其进行加密。

### SSL握手
&emsp;&emsp; 在发送加密HTTP报文前，客户端和服务器要进行一次SSL握手，在握手过程中要完成以下工作。
    
    1，交换协议版本号。
    2，选择一个两端都了解的密码。
    3，对两端的身份进行认证。
    4，生成临时的会话密钥，一般加密信道。
    
<br>
&emsp;&emsp; 站点证书的有效性通常使用如下步骤进行验证：
    
    1，日期检测，检查证书的起始日期和结束日期。
    2，签名颁发者可信度检测。
    3，签名检测。一旦判定签名授权是可信的，就要对签名使用签名颁发机构的公开密钥，并将其与校验码进行比较，以查看证书的完整性。
    4，站点身份检测。校验证书中的域名与对话的服务器的域名是否匹配。
    
<h2 id="13">13.实体和编码</h2>
&emsp;&emsp; 报文实体由实体首部和实体主体组成。HTTP/1.1规范建议对带有主体，但是没有Content-Length首部的请求，服务器如果无法确定报文的
长度，就应该发送400 Bad Request响应或411 Length Required响应，后一种情况表面服务器要求收到正确的Content-Length首部。
    
    HTTP/1.1定义了10个基本字体首部字段
    Content-Type        实体中承载对象的类型
    Content-Length      所传送实体主体的长度或大小
    Content-Language    与所传送对象最相配的人类语言
    Content-Encoding    对象数据所做的任意变换(比如，压缩)
    Content-Location    一个备用位置，这个首部说明它是整体的哪个部分。
    Content-MD5         实体主体内容的校验和。
    Last-Modified       所传输内容在服务器上创建或最后修改的日期时间。
    Expires             实体数据将要失效的日期时间。
    Allow               改资源所允许的各种请求方法，如GET、HEAD
    ETag                这份文档特定实例的唯一验证码。ETag首部没有正式被定义为实体首部。
    Cache-Control       指出该如何缓存该文档。和ETag类似也没被正式定义为实体首部。
    
<br>
&emsp;&emsp; Content-Encoding首部用来说明编码时使用的算法。gzip、compress、deflate编码都是无损压缩算法，用于减少传输报文的大小，
不会导致信息损失。gzip通常效率是最高的，使用最广泛。

    内容编码代号
    gzip            表明实体采用GNU zip编码
    compress        表明实体采用Unix的文件压缩程序
    deflate         表明实体是用zlib的格式压缩的
    identity        表明没有对实体进行编码，当没有Content-Encoding首部时，就默认为这种情况。

<br>
&emsp;&emsp; 为了避免服务器使用客户端不支持的编码方式，客户端就把自己支持的内容编码方式列表放在请求的Accept-Encoding首部中。
如果HTTP请求中没有包含Accept-Encoding首部，服务器就可以假设客户端能够接受任何编码方式(等价于Accept-Encoding:*)。客户端可以给每种编码
附带Q值参数来说明编码的优先级。Q值范围从0.0到1.0，identity编码代号只能在Accept-Encoding首部中出现，客户端用它来说明相对于其他内容编码算法的优先级。

    Accept-Encoding: compress, gzip
    Accept-Encoding：gzip;q=1.0, identity;q=0.5, *;q=0
    
### 传输编码和分块编码
&emsp;&emsp; 内容编码是对报文主体进行的可逆变换。内容编码是和内容具体格式细节紧密相关的。比如用gzip压缩文本文件，但JPEG文件用gzip压缩不好。
传输编码也是作用在实体主体上的可逆变换，但使用它们是由于架构方面的原因，同内容的格式无关。传输编码是为了改变报文中的数据在网络上传输的方式。
在HTTP/1.1中，只定义了一种传输编码，就是分块编码(chunked)。传输编码是HTTP/1.1引入的新特性。

<br>
&emsp;&emsp; HTTP协议中只定义了两个首部来描述和控制传输编码。
    
    Transfer-Encoding           告知接收方为了可靠地传输报文，已经对其进行了何种编码。
    TE                          用在请求首部中，告知服务器可以使用哪些传输编码扩展。
    
### 分块编码
&emsp;&emsp; 分块编码就是把报文分割成若干个已知的块，块之间紧挨着发送，这样就不需要在发送前知道整个报文的大小了。分块编码是一种传输编码，
因此是报文的属性而不是主体的属性。

<br>
&emsp;&emsp; 使用持久连接时，服务器在写主体前，必须知道主体大小，并在Content-Length首部中发送，如果动态创建内容，就无法在发送前知道主体长度。
分块编码只要发送每块内容和其大小，在主体发送完前重复这个过程，服务器可以用大小为0的块作为主体结束的信号，这样就可以继续保持连接，为下一个响应准备。

<br>
&emsp;&emsp; 每个分块包含一个长度值和该块的数据，长度值是十六进制并将CRLF与数据分隔开。分块中数据的大小以字节计算，不包括长度值与数值之间的CRLF
序列以及分块结尾的CRLF序列。最后一个块有点特别，长度值为0，表示主体结束。

                    HTTP/1.1 200 OK<CR><LF>
                    Content-type: text/plain<CR><LF>
    HTTP响应         Transfer-encoding: chunked<CR><LF>
                    Trailer: Content-MD5<CR><LF>
                    <CR><LF>
    第一块           27<CR><LF>
                    We hold these truths to be self-evicent<CR><LF>
    ...             ...
    最后一块         0<CR><LF>
    拖挂             Content-MD5:xxxxxx<CR><LF>       拖挂可选，仅当报文首部中有Trailer首部时才出现
    
### 范围请求
&emsp;&emsp; 可以用Range首部来请求多个范围。服务器可以通过在响应中包含Accept-Range首部的形式向客户端说明可以接受的范围请求，
这个首部的值是计算范围的单位，通常是以字节计算的。
    
    Range: bytes=4000-          开头4000字节之后
    Accept-Ranges: bytes
    