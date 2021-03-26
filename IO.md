* [1.Linux零拷贝](#1)
* [2.Java零拷贝](#2)
* [3.五种IO模型](#3)

<h2 id="1">1.Linux零拷贝</h2>
&emsp;&emsp; 零拷贝通常是指计算机执行操作时，CPU不需要先将数据从某处内存复制到另一个特定区域。

### Linux IO流程
&emsp;&emsp; 1，DMA(Direct Memory Access，直接内存存取)先将磁盘文件读取到操作系统内核缓冲区。2，将内核缓冲区中的数据，拷贝到应用程序的缓冲区。
3，将应用程序缓冲区中的数据，拷贝到socket网络发送缓冲区(属于操作系统内核缓冲区)。4，将socket缓冲区中的数据，拷贝到网卡，由网卡进行网络传输。
这种传输方式需要涉及到两次DMA拷贝，两次CPU拷贝，四次上下文切换。

     Application Context                 Application Buffer
                                            ^           |
                                CPU Copy    |           |
                                            |           V
     Kernel Context                    Read Buffer  Socket Buffer
                                            ^           |
                                DMA Copy    |           |
                                            |           V
                                           Disk     Network Card
                                           
&emsp;&emsp; 1，在应用程序调用read()方法时，会进行一次上下文切换，从用户态切换到内核态。2，应用程序无法访问内核地址空间的数据，
如果应用程序要操作这些数据，得把内容从内核缓冲区读到用户缓冲区，read()调用方法的返回会引发一次上下文切换，从内核态切换到用户态。
3，使用Socket send()把数据传入到Socket发送缓冲区，会进行一次上下文切换，从用户态切换到内核态。4，send()返回会引发一次上下文切换，
从内核态切换到用户态。

### Linux支持的零拷贝
&emsp;&emsp; 1，mmap内存映射。2，sendfile。3，sendfile + DMA scatter/gather copy。4，splice。

#### mmap
&emsp;&emsp; DMA加载磁盘数据到内核缓冲区后，应用程序缓冲区和内核缓冲区进行映射。内核缓冲区到socket缓冲区，进行一次CPU拷贝。
总共经历一次CPU拷贝，两次DMA拷贝，四次上下文切换。

#### sendfile
&emsp;&emsp; 当调用sendfile时，DMA将磁盘数据复制到内核缓冲区，然后将内核缓冲区的数据直接拷贝到socket缓冲区。会经历一次CPU拷贝，
两次DMA拷贝，两次上下文切换。相比mmp，少了两次上下文切换，但是应用程序不能修改数据，只是单纯进行了一次数据传输。

#### sendfile + DMA scatter/gather copy
&emsp;&emsp; 相比于sendfile，少了内核缓冲区到socket缓冲区的一次CPU数据拷贝。但是需要CPU将内核缓冲区的文件描述符发送到socket缓冲区。
会经历0次CPU拷贝，2次DMA拷贝，两次上下文切换。

            kernel buffer ------> socket buffer  使用append descriptor
            
#### splice
&emsp;&emsp; 从磁盘加载数据到内核缓冲区后，在内核空间直接与socket buffer建立pipeline管道。与sendfile不同，splice不需要硬件支持。
会经历0次CPU拷贝，2次DMA拷贝，2次上下文切换。
    
            kernel buffer --------> socket buffer  
                         set up pipe

<h2 id="2">2.Java零拷贝</h2>
&emsp;&emsp; Java中支持mmap、sendfile。Java中sendfile使用FileChannel.transferTo()方法，transferTo()实现方式也是通过系统调用sendfile()，
Netty是使用FileChannel.transferTo()实现文件传输。

<h2 id="3">3.五种IO模型</h2>
&emsp;&emsp; IO模型有：1，同步阻塞IO。2，同步非阻塞IO。3，多路复用IO。4，信号驱动IO，实际很少用，Java不支持。5，异步IO。
对于一个IO read，会经历两个阶段：1，等待数据准备。2，将数据从内核拷贝到进程中。

### 同步阻塞IO
&emsp;&emsp; Linux中，默认情况下所有的Socket都是阻塞的。对于用户进程，一开始数据还没到达，那么用户进程就会阻塞，
当kernel等到数据准备好了，那么会将数据从kernel拷贝到用户内存，然后用户进程才解除阻塞，此时等待数据、拷贝数据两个阶段都是阻塞的。

### 同步非阻塞IO
&emsp;&emsp; Linux下可以通过设置socket，将其变为非阻塞的。当用户进程read时，如果kernel中的数据还没准备好，那么不会阻塞用户进程，
而是立刻返回一个error，此时用户进程就能知道数据还没准备好。在非阻塞IO中，用户进程需要不断的主动询问kernel数据是否准备好。

### 多路复用IO
&emsp;&emsp; 多路复用的IO使用select/epoll，select/epoll单个进程就可以同时处理多个网络连接的IO，它的原理是select/epoll会不断的轮询所有socket，
当某个socket有数据到达了，就通知用户线程。当进程调用了select，整个进程会被block，同时kernel会监视所有select负责的socket，当任何一个socket中
的数据准备好了，select就会返回，这时候用户进程再调用read操作，将数据从kernel拷贝到用户进程。select/epoll优势在于能处理更多的连接。

### 异步IO
&emsp;&emsp; 用户进程发起read操作后，就可以立刻做其他事情，kernel会等待数据准备完成，然后将数据拷贝到用户内存，当这一切完成后，
kernel会给用户进程发送一个signal，告诉它read操作完成了。

### Reactor模型和Proactor模型
&emsp;&emsp; 在高性能IO中，有两个著名的模型：Reactor模型和Proactor模型，其中Reactor模型运用于同步IO，Proactor模型运用于异步IO。
对于支持多连接的服务器，一般可以总结为2种fd和3种事件。2种fd为：1，listenfd一般情况下只有一个，用来监听一个特定的端口。2，connfd每个连接都有
一个connfd，用来收发数据。3种事件为：1，listenfd进程accept阻塞监听，创建一个connfd，用来收发数据。2，用户态/内核态copy数据，
每个connfd对应着2个应用缓冲区：readbuf和writebuf。3，处理connfd发来的数据，业务逻辑处理，准备response到writebuf。

### Reactor模型
&emsp;&emsp; 无论是C++还是java编写的网络框架，大多数都是基于Reactor模型，Reactor模型基于事件驱动，特别适合处理海量的I/O事件。
Netty就是使用了Reactor模型，使用一个NioEventLoopGroup作为bossGroup，先绑定端口，使用NioEventLoop异步线程循环监听端口事件。
当监听到连接事件后，将socket在childGroup中进行注册，childGroup中的NioEventLoop会异步的循环监听socket事件，当NioEventLoop监听到事件后，
会利用ChannelHandler读取数据，在读取完数据后，会将数据交由Worker线程池来处理，Worker线程池用来处理具体的业务逻辑，当Worker线程处理完数据后，
会将返回数据交由ChannelHandler来发送。

    主从Reactor多线程模型消息处理流程：
        1，从主线程池中随机选择一个Reactor线程作为acceptor线程，用于绑定监听端口，接收客户端的连接。
        2，acceptor线程接收客户端连接请求之后创建新的SocketChannel，并将其注册到主线程池中的其他Reactor线程上，
            由其负责介入认证、IP黑白名单、握手等操作。
        3，此时业务层链路正式建立，将SocketChannel从主线程池的Reactor线程的多路复用器上摘除，重新注册到Sub线程池的线程上，
            并创建一个Handler用于处理各种事件。
        4，当有新的事件发生时，SubReactor会调用对应的Handler进行响应。
        5，Handler通过Read读取数据后，发送给后面的Worker线程池进行业务处理。
        6，Worker线程池会分配独立的线程完成真正的业务处理，然后将响应结果发送给Handler处理。
        7，Handler收到响应结果后通过send将响应结果返回给Client。

