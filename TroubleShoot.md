* [1.CPU占用率高问题排查](#1)
* [2.内存占用率高问题排查](#2)

<h2 id="1">1.CPU占用率高问题排查</h2>
&emsp;&emsp; 1，可以根据top命令来查看各进程的CPU占用率。2，找到CPU占用率高的进程后，可以根据进程id(PID)，来查看进程中的所有线程。

    // 1，查看进程CPU占用率
    // mac
    top -o cpu 根据cpu desc 排序。也可以先执行top，然后输入o，再输入cpu，此时也会按照cpu desc排序。
    // linux
    top，然后使用shift + M 根据memory desc排序，使用shift + P 根据CPU排序排序。
    
    // 2，查看进程中所有线程的CPU占用率(linux only)。
    top -H -p [pid] 用来查看进程(pid)中的各线程情况。此时获取到的pid也就是线程id，因为linux将线程作为轻量级的进程，也分配了pid，
                    而mac并没有这么处理。因此这一步可以获取到linux下的进程的各线程pid。
                    
    echo "obase=16; [pid]" | bc 这一步将上一步获取到的线程pid转换为16进制，因为jstack打印出的nid为16进制。可以通过这个16进制id找到
                                线程快照中的对应线程。
    
    // 3，查看进程的线程情况。
    jstack pid(第1步拿到的进程pid) 此时会打印出该进程的线程快照，然后可以根据第2步获取的高CPU使用率的线程16进制id，来查看对应线程的情况。
    
    // jstack (pid) 打印出的进程的线程如下所示：
    Full xiao.base.thread dump Java HotSpot(TM) 64-Bit Server VM (25.172-b11 mixed mode):
    
    "Attach Listener" #15 daemon prio=9 os_prio=31 tid=0x00007f8fa325e000 nid=0x1207 waiting on condition [0x0000000000000000]
       java.lang.Thread.State: RUNNABLE
    
    "ForkJoinPool-1-worker-1" #14 daemon prio=5 os_prio=31 tid=0x00007f8fa3239000 nid=0x5403 runnable [0x0000700008200000]
       java.lang.Thread.State: RUNNABLE
    	at CpuUsageTest.make cpu high usage(CpuUsageTest.kt:11)
    	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
    	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    	at java.lang.reflect.Method.invoke(Method.java:498)
    	at org.junit.platform.commons.xiao.base.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:688)
    	
<h2 id="2">2.内存占用率高问题排查</h2>
&emsp;&emsp; 1，先使用top查看各进程的内存占用率。2，找到内存占用率高的进程。

    // 1.1，找到当前java进程。
    jps -l 输出完全的包名，应用主类名，jar的完全路径名。这一步可以看到进程id。
    
    // 打印内容如下：
    ps -l
    48773 org.gradle.launcher.daemon.bootstrap.GradleDaemon
    49926 org.jetbrains.idea.maven.server.RemoteMavenServer36
    48457 org.jetbrains.kotlin.daemon.KotlinCompileDaemon
    51130 OutOfMemoryTest
    
    // 1.2 找出内存占用高的进程。
    // linux
    先进入top, 然后shift + M，按照进程memory desc排序。
    // mac
    先进入top，然后输入o + rsize。或者 top -o rsize。
    
    // 2，找出进程中内存占用高的线程(linux only)。
    top -H -p [pid] 这一步同cpu占用率高的排查。
    
    // 3，查看进程的gc情况。
    jstat -gcutil [pid] [interval] 例如：jstat -gcutil 51130 1000 表示查看pid为51130的进程gc情况，刷新频率为1000ms。
    
    // 打印内容如下：
    S0     S1     E      O      M     CCS    YGC     YGCT    FGC    FGCT     GCT
    6.72   0.00 100.00 100.00  54.19  55.72    12    2.634    17   18.965   21.600
    
    S0: 新生代第一个Survivor分区使用容量百分比。
    S1：新生代第二个Survivor分区使用容量百分比。
    E: 新生代Eden区使用容量百分比。
    O：老年代使用容量百分比。
    M：方法区使用容量百分比。
    CCS：压缩类空间使用百分比。
    YGC：新生代垃圾回收次数。
    YGCT：新生代垃圾回收所花时间。
    FGC：full gc垃圾回收次数。
    FGCT：full gc垃圾回收所花时间。
    GCT：GC总共所花时间。(s)
    
    // 4，找出进程中存活的对象。
    jmap -histo:live [pid]，通过这一步可以看到进程中存活的对象类名、对象数量、所占字节大小。