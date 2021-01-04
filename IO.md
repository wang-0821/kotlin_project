* [1.Linux零拷贝](#1)
* [2.Java零拷贝](#2)

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