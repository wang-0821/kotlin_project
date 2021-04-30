* [1.Linux零拷贝](#1)
* [2.Java零拷贝](#2)
* [3.Reactor模型和Proactor](#3)
* [4.select,poll,epoll](#4)

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

<h2 id="3">3.Reactor模型和Proactor</h2>
&emsp;&emsp; Reactor模型是基于同步I/O的，而Proactor是基于异步I/O操作的。Netty就是基于主从Reactor多线程模型。

<h2 id="4">4.select,poll,epoll</h2>
&emsp;&emsp; select、poll、epoll都是IO多路复用机制，监视多个描述符，一旦某个描述符就绪，就能够通知程序进行相应的读写操作。
select、poll、epoll都是同步IO，因为都需要在读写事件就绪后负责进行读写。

### Select
&emsp;&emsp; select步骤：1，创建所关注事件的描述符集合(fd_set)，对于一个描述符可以关注read、write、exception事件，所以通常要创建三个描述符集合。
2，调用select()等待事件发生。3，轮询所有文件描述符集合中的文件描述符，检查是否有对应的事件发生，如果有，则处理事件。

    Select的优点：单线程，占用资源少，不消耗太多CPU。
    Select的缺点：
        1，每次select都需要把fd_set从用户态拷贝到内核态，这个复制开销在fd很多时，开销会很大。
        2，每次select都需要在内核遍历所有的fd，fd很多时，这个开销也很大。
        3，单个进程能监控的fd数量存在最大限制，32位机器默认是1024，数量超过后，select性能会急剧下降。
        4，select触发方式是水平触发，应用程序如果没有完成对一个已经就绪的文件描述符进行IO操作，
            那么之后每次select调用还是会将这些文件描述符通知进程。
        5，该模型将事件探测和事件响应夹杂在一起，一旦事件响应的执行体庞大，则对整个模型是灾难性的。

### Poll
&emsp;&emsp; poll是linux中引入的，poll与select本质上没有太大区别。poll流程：1，创建描述符集合，设置关注的事件。2，调用poll，等待事件发生。
3，轮询描述符集合，检查事件，处理事件。poll和select的区别在于，poll只有一个描述符集合，每个描述符上分别设置读、写、异常事件，最后轮询的时候，
可以同时检查三种事件。
    
    poll没有最大连接数限制，因为它是基于链表存储的。

### Epoll
&emsp;&emsp; epoll将fd_set描述符列表交给内核，一旦有事件发生，内核把发生事件的描述符列表通知给进程，这样避免了轮询整个描述符列表。
epoll支持水平触发和边缘触发，边缘触发只告诉进程哪些fd刚刚变为就绪状态，并且只会通知一次。epoll提供了三个函数：epoll_create、epoll_ctl、
epoll_wait。
    
    epoll_create：创建一个epoll句柄。
    epoll_ctl：注册要监听的事件类型。
    epoll_wait：等待事件的产生。

    epoll步骤：
        1，使用epoll_create(int size)，创建一个有size个描述符的事件列表。
        2，使用epoll_ctl，给描述符设置所关注的事件，并将它添加到内核的事件列表中。 
        3，使用epoll_wait，等待内核通知事件，得到发生事件的描述符的结构列表。

### Epoll的LT和ET
&emsp;&emsp; 水平触发和边缘触发的区别在于：只要句柄满足某种状态，水平触发就会发出通知。而只有当句柄状态改变时，边缘触发才会发出通知。
LT(Level trigger)是水平触发，大并发情况下，效率低于ET，但编码要求低，不用担心事件丢失。ET(Edge trigger)是边缘触发，并发效率高，对编码的要求高，
容易丢失事件。

    epoll的优点：
        1，没有最大并发连接限制。
        2，通过内核事件监听回调的方式，避免对文件描述符进行轮询，提高效率。
        3，内核和用户空间共享一块内存，使用mmp实现内存映射，确保fd在全过程中只进行一次拷贝。