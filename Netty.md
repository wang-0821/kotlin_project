## Netty notes.

* [1.Netty结构](#1)
* [2.Netty内存管理](#2)

<h2 id="1">1.Netty结构</h2>
&emsp;&emsp; Netty启动过程中，涉及到ServerBootstrap、EventLoopGroup(group、childGroup)、Channel、
ChannelHandler(childHandler、handler)。

### EventLoopGroup
&emsp;&emsp; ServerBootstrap需要设置group，通常使用NioEventLoopGroup。在创建NioEventLoopGroup时，会创建一个EventExecutor数组(children)，
数组大小默认为处理器的两倍。在使用NioEventLoopGroup时，EventExecutor数组中的元素都是NioEventLoop。

### Server端流程
&emsp;&emsp; Server端流程，涉及到：EventLoopGroup、Channel、EventLoop、ChannelHandler、ChannelFuture等。

### 1，创建并初始化Channel
&emsp;&emsp; 先根据ServerBootstrap中配置的Channel，newInstance实例化一个Channel对象。然后初始化Channel对象，
这一步会向Channel中设置options、attrs，向channelPipeline中添加handler。最后向channelPipeline中添加一个ChannelHandler(ServerBootstrapAcceptor)。
            
    // 每个Channel，都会包含一个ChannelPipeline。每个Channel都会有一个eventLoop，只有当channel注册时，才会赋值。
    private volatile PausableChannelEventLoop eventLoop;
    protected AbstractChannel(Channel parent) {
        this.parent = parent;
        id = DefaultChannelId.newInstance();
        unsafe = newUnsafe();
        pipeline = new DefaultChannelPipeline(this);
    }
    
    // pipeline中包含一个队列，维护了一系列ChannelHandler。并且已经预先定义了队列头部和尾部。
        DefaultChannelPipeline(AbstractChannel channel) {
            if (channel == null) {
                throw new NullPointerException("channel");
            }
            this.channel = channel;
    
            tail = new TailContext(this);
            head = new HeadContext(this);
    
            head.next = tail;
            tail.prev = head;
        }
    
### 2，注册Channel
&emsp;&emsp; 在创建并初始化Channel后，会向ServerBootstrap.group中注册该Channel。实际是向ServerBootstrap.NioEventLoopGroup 中
children数组的某个元素EventLoop(NioEventLoop)中注册该Channel。在注册Channel时，会创建一个ChannelPromise。

    public ChannelFuture register(Channel channel) {
        return register(channel, new DefaultChannelPromise(channel, this));
    }

&emsp;&emsp; 然后给Channel.eventLoop赋值为PausableChannelEventLoop，也就是将ServerBootstrap.group.children数组中的某个EventLoop，
绑定在了当前Channel上。然后在当前eventLoop(也是一个Executor)中异步执行注册当前ChannelPromise。返回的ChannelFuture即该ChannelPromise。

    AbstractChannel.this.eventLoop = new PausableChannelEventLoop(eventLoop);
    
    if (eventLoop.inEventLoop()) {
        register0(promise);
    } else {
        try {
            eventLoop.execute(new OneTimeTask() {
                @Override
                public void run() {
                    register0(promise);
                }
            });
        ......

&emsp;&emsp; 在注册Channel时，使用eventLoop.execute(Runnable)方法，如果是首次使用这个方法，那么在添加注册Channel任务之前会调用
startExecution()方法，这个方法会异步执行NioEventLoop Runnable对象。最后在NioEventLoop run方法中执行了注册Channel的任务。

    public void execute(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task");
        }

        boolean inEventLoop = inEventLoop();
        if (inEventLoop) {
            addTask(task);
        } else {
            startExecution();
            addTask(task);
            if (isShutdown() && removeTask(task)) {
                reject();
            }
        }

        if (!addTaskWakesUp && wakesUpForTask(task)) {
            wakeup(inEventLoop);
        }
    }

        
&emsp;&emsp; register0(promise)，会使用channel中的SelectableChannel来注册当前NioEventLoop的Selector。并将NioServerSocketChannel
作为attachment参数。注册完成后会产生一个SelectionKey作为当前NioServerSocketChannel.selectionKey的值。
    
     protected void doRegister() throws Exception {
        boolean selected = false;
        for (;;) {
            try {
                selectionKey = javaChannel().register(((NioEventLoop) eventLoop().unwrap()).selector, 0, this);
                return;
            } catch (CancelledKeyException e) {
                if (!selected) {
                    ((NioEventLoop) eventLoop().unwrap()).selectNow();
                    selected = true;
                } else {
                    throw e;
                }
            }
        }
     }

### 3，Channel绑定socket端口
&emsp;&emsp; Channel注册完成后，会执行pipeline.fireChannelRegistered()，然后会再次异步执行ServerSocket.bind，监听端口。
绑定完端口，执行ChannelPipeline.fireChannelActive()。如果channel.config.autoRead == 1，那么会执行ChannelPipeline.fireChannelActive。
fireChannelActive会最终调用Channel.doBeginRead()来处理SelectionKey。

    boolean wasActive = isActive();
    try {
        doBind(localAddress);
    } catch (Throwable t) {
        safeSetFailure(promise, t);
        closeIfClosed();
        return;
    }

    // fireChannelActive 在执行时，实际会使用Channel的selectionKey
    if (!wasActive && isActive()) {
        invokeLater(new OneTimeTask() {
            @Override
            public void run() {
                pipeline.fireChannelActive();
            }
        });
    }
    
    // doBeginRead
    protected void doBeginRead() throws Exception {
        // Channel.read() or ChannelHandlerContext.read() was called
        if (inputShutdown) {
            return;
        }
    
        final SelectionKey selectionKey = this.selectionKey;
        if (!selectionKey.isValid()) {
            return;
        }
    
        readPending = true;
    
        final int interestOps = selectionKey.interestOps();
        if ((interestOps & readInterestOp) == 0) {
            selectionKey.interestOps(interestOps | readInterestOp);
        }
    }

### 4，启动完成，阻塞当前线程
&emsp;&emsp; 在启动过程中，先是异步注册了Channel，然后给注册Channel异步任务，添加了一个Listener，用来异步执行ServerSocket绑定端口。
最终返回了绑定端口的ChannelFuture对象。
    
    // 这里sync，用来等待绑定端口异步任务结束。
    val channelFuture = serverBootstrap.bind(PORT).sync()
    // 这里用Channel的closeFuture来阻塞线程。
    channelFuture.channel().closeFuture().sync()
    
### 5，EventLoop Socket事件监听
&emsp;&emsp; 在Netty中，使用EventLoop异步循环执行任务，对于EventLoop，在启动过程中，先后执行了：注册Channel、绑定端口。在完成启动后，
会无限循环，监听SelectionKey事件。在NioEventLoop Runnable中，run()方法，会不断的监听并执行事件，在函数结尾会调用scheduleExecution()
函数，这个函数会提交任务到ForkJoinPool中，而提交的任务就是这个NioEventLoop Runnable，因此实际上实现了无限循环事件监听。
group只会监听连接，当有新的连接创建时，group会将任务交给childGroup来处理。Netty可以通过设置group、childGroup，使用主从多线程模型。

    protected void run() {
        boolean oldWakenUp = wakenUp.getAndSet(false);
        try {
            if (hasTasks()) {
                selectNow();
            } else {
                select(oldWakenUp);
                if (wakenUp.get()) {
                    selector.wakeup();
                }
            }

            cancelledKeys = 0;
            needsToSelectAgain = false;
            final int ioRatio = this.ioRatio;
            if (ioRatio == 100) {
                processSelectedKeys();
                runAllTasks();
            } else {
                final long ioStartTime = System.nanoTime();

                processSelectedKeys();

                final long ioTime = System.nanoTime() - ioStartTime;
                runAllTasks(ioTime * (100 - ioRatio) / ioRatio);
            }

            if (isShuttingDown()) {
                closeAll();
                if (confirmShutdown()) {
                    cleanupAndTerminate(true);
                    return;
                }
            }
        } catch (Throwable t) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // Ignore.
            }
        }

        scheduleExecution();
    }

### 6，数据读取
&emsp;&emsp; group的EventLoop在获取到SelectionKey事件后，会将事件转交给childGroup的EventLoop处理，childGroup也会进行注册Channel，
childGroup使用NioSocketChannel，使用config.getAllocator()来获取ByteBufAllocator，可以通过配置来自定义ByteBufAllocator，
config.getRecvByteBufAllocator()也可以用来自定义RecvByteBufAllocator，最终通过RecvByteBufAllocator.Handle + ByteBufAllocator
来构建读取数据的ByteBuf。 

    final ChannelPipeline pipeline = pipeline();
    final ByteBufAllocator allocator = config.getAllocator();
    final int maxMessagesPerRead = config.getMaxMessagesPerRead();
    RecvByteBufAllocator.Handle allocHandle = recvBufAllocHandle();
    
    byteBuf = allocHandle.allocate(allocator);
    int writable = byteBuf.writableBytes();
    int localReadAmount = doReadBytes(byteBuf);
    
    pipeline.fireChannelRead(byteBuf);
    
    ByteBufAllocator + RecvByteBufAllocator.Handle -> ByteBuf
    
&emsp;&emsp; config可以配置maxMessagesPerRead，表示每次读取最多能读取的次数，超过次数会丢弃数据。每次读取都会调用fireChannelRead(ByteBuf),
在读取数据完毕后，会调用fireChannelReadComplete()。
    
    fireChannelRead(ByteBuf) ---> channelRead(ChannelHandlerContext ctx, Object msg)
    fireChannelReadComplete() ---> channelReadComplete(ChannelHandlerContext ctx)

### Netty 关系图

    ServerBootstrap --------> EventLoopGroup group、childGroup (NioEventLoopGroup) -------> EventExecutor[] children(NioEventLoop)
                       包含                               包含                        包含
                       
    ServerBootstrap --------> ChannelFactory -------> Channel  ------> ChannelPipeline ----> ChannelHandler队列
                       包含                     产生       |       包含                    包含
                                                     ----- ------
                                                    |           |
                                                    |           |
                                               包含  V           V 包含
                                             SelectionKey   SelectableChannel(ServerSocketChannelImpl)
                                                    
    
    
    // Channel中包含一个eventLoop属性，这个属性包含了一个EventLoop，即上面NioEventLoopGroup中的某个EventExecutor。
    Channel ----> eventLoop (PausableChannelEventLoop) -----> EventLoop
       |    包含                                         包含      |
       |                                                          |
       V 包含                                                      V 包含
    SelectionKey                                               Selector
    
    SelectionKey ServerSocketChannelImple.register(Selector, 0, Channel)   
            
    ChannelPromise -----> Channel
           |        包含
           | 包含
           V
     EventExecutor(NioEventLoop) 
     
<h2 id="2">2.Netty内存管理</h2>
&emsp;&emsp; Netty采用引用计数的方式来实现内存的复用，这需要正确的使用引用计数，否则可能导致内存泄漏。
在非安卓平台，并且存在Cleaner来释放直接内存，且io.netty.noPreferDirect没有被设置为false，
那么此时PooledByteBufAllocator.DEFAULT会默认使用堆外内存。对于堆内存，如果使用PooledByteBufAllocator，
那么会将创建的字节数组放在FastThreadLocal中。堆内数组由于会被JVM回收，可以自己管理，感觉可以不需要Netty
提供的丰富的功能。

            DEFAULT_PAGE_SIZE:
                如果没有配置io.netty.allocator.pageSize，默认为8192，pageSize不能小于4096，
                且pageSize必须是2的幂。
            DEFAULT_MAX_ORDER:
                如果没有配置io.netty.allocator.maxOrder，默认为11，maxOrder不能大于14，
                DEFAULT_PAGE_SIZE * (2^maxOrder)，不能大于Int.MAX_VALUE。
            DEFAULT_NUM_HEAP_ARENA:
                如果没有配置io.netty.allocator.numHeapArenas，取min(处理器数 * 2, 
                Runtime.maxMemory() / (DEFAULT_PAGE_SIZE << DEFAULT_MAX_ORDER) / 6)。
            DEFAULT_NUM_DIRECT_ARENA:
                如果没有配置io.netty.allocator.numDirectArenas，取min(处理器数 * 2, 
                PlatformDependent.maxDirectMemory() / (DEFAULT_PAGE_SIZE << DEFAULT_MAX_ORDER) / 6)。
            DEFAULT_SMALL_CACHE_SIZE:
                如果没有配置io.netty.allocator.smallCacheSize，默认为256.
            DEFAULT_NORMAL_CACHE_SIZE:
                如果没有配置io.netty.allocator.normalCacheSize，默认为64.
            DEFAULT_MAX_CACHED_BUFFER_CAPACITY:
                如果没有配置io.netty.allocator.maxCachedBufferCapacity，默认为32 * 1024。
            DEFAULT_CACHE_TRIM_INTERVAL:
                如果没有配置io.netty.allocator.cacheTrimInterval，默认为8192。
            DEFAULT_CACHE_TRIM_INTERVAL_MILLIS:
                如果没有配置io.netty.allocator.cacheTrimIntervalMillis，默认为0。
            DEFAULT_USE_CACHE_FOR_ALL_THREADS:
                如果没有配置io.netty.allocator.useCacheForAllThreads，默认为true。
            DEFAULT_DIRECT_MEMORY_CACHE_ALIGNMENT:
                如果没有配置io.netty.allocator.directMemoryCacheAlignment，默认为0。
            DEFAULT_MAX_CACHED_BYTEBUFFERS_PER_CHUNK:
                如果没有配置io.netty.allocator.maxCachedByteBuffersPerChunk，默认为1023。
            DEFAULT_SAMPLING_INTERVAL:
                如果没有配置io.netty.leakDetection.samplingInterval，默认为128。
            DEFAULT_LEVEL:
                如果没有配置io.netty.leakDetection.level，默认为Level.SIMPLE。
            
            
                        PooledByteBufAllocator.DEFAULT.buffer(capacity)创建ByteBuf
                                                |
                                                V
            创建PooledByteBufAllocator(preferDirect, nHeapArena, nDirectArena, pageSize, 
        maxOrder, smallCacheSize, normalCacheSize, useCacheForAllThreads, directMemoryCacheAlignment)
                                                |
                                                V
            根据nHeapArena创建PoolArena<byte[]>数组赋值给PooledByteBufAllocator.heapArenas，
        数组中的对象类型为PoolArena.HeapArena，设置heapArenaMetrics为heapArenas中的对象集合
                                                |
                                                V
                    根据nDirectArena创建PoolArena<ByteBuffer>数组，赋值给directArenas，
            数组中的对象类型为PoolArena.DirectArena，设置directArenaMetrics为directArenas中的对象集合
                                                |
                                                V
                    设置PooledByteBufAllocator.metric为PooledByteBufAllocatorMetric(this)
                                                |
                                                V
                                 PooledByteBufAllocator对象创建完毕
                                                |
                                                V
                       执行PooledByteBufAllocator.directBuffer(capacity)获取ByteBuf
                                                |
                                                V
                   执行PooledByteBufAllocator.newDirectBuffer(capacity, Int.MAX_VALUE)
                                                |
                                                V
                           执行PoolThreadLocalCache.get()获取PoolThreadCache
                                                |
                                                V
                      执行InternalThreadLocalMap.get()获取InternalThreadLocalMap，
                    这一步首先从FastThreadLocal或者ThreadLocal中获取，没有则创建并设置进去。
                InternalThreadLocalMap维护一个object数组indexedVariables，数组中初始值都为UNSET
                                                |
                                                V
                先根据FastThreadLocal.index从InternalThreadLocalMap.indexedVariables中获取对象
                                                |
                                                V
          如果获取到的对象为UNSET，则执行FastThreadLocal.initialize(InternalThreadLocalMap)，否则返回对象
                                                |
                                                V
              执行initialValue()获取对象，将对象设置到InternalThreadLocalMap.indexedVariables中，
                    并将当前FastThreadLocal放到InternalThreadLocalMap index为0的位置，
                        这个位置存放了当前线程中所有FastThreadLocal的实例对象
                                                |
                                                V
          默认的initialValue()会返回null，这里执行PoolThreadLocalCache.initialValue()获取PoolThreadCache
                                                |
                                                V
           执行leastUsedArena(heapArenas)，获取其中HeapArena.numThreadCaches最小的HeapArena heapArena
                                                |
                                                V
           执行leastUsedArena(directArenas)，获取其中DirectArena.numThreadCaches最小的DirectArena directArena
                                                |
                                                V
                 如果useCacheForAllThreads或者当前线程为FastThreadLocalThread，
                 创建PoolThreadCache(heapArena, directArena, smallCacheSize, normalCacheSize, 
                     DEFAULT_MAX_CACHED_BUFFER_CAPACITY, DEFAULT_CACHE_TRIM_INTERVAL)，
                 并且如果DEFAULT_CACHE_TRIM_INTERVAL_MILLIS > 0 ThreadExecutorMap.currentExecutor()有值，
                 启动异步定时任务executor.scheduleAtFixedRate(trimTask, DEFAULT_CACHE_TRIM_INTERVAL_MILLIS,
                     DEFAULT_CACHE_TRIM_INTERVAL_MILLIS, TimeUnit.MILLISECONDS)
                                                |
                                                V
                      否则创建PoolThreadCache(heapArena, directArena, 0, 0, 0, 0)
                                                |
                                                V
                        PoolThreadLocalCache.get()执行完毕，获取到PoolThreadCache
                                                |
                                                V
        根据PoolThreadCache.directArena，执行DirectArena.allocate(PoolThreadCache, initialCapacity, maxCapacity)
                                                |
                                                V
                 执行PooledUnsafeDirectByteBuf.newInstance(maxCapacity)获取PooledByteBuf
                                                |
                                                V
                           执行RECYCLER.get()获取PooledUnsafeDirectByteBuf
                                                |
                                                V
               根据Recycler中的FastThreadLocal<Stack<PooledUnsafeDirectByteBuf>>，获取Stack
                                                |
                                                V
              如果Stack.pop() DefaultHandle<T>为空，则执行Stack.newHandle()创建DefaultHandle(Stack)，
              并创建PooledUnsafeDirectByteBuf(DefaultHandle, 0)，将其赋值给DefaultHandle.value。
                        返回DefaultHandle.value PooledUnsafeDirectByteBuf。
                                                |
                                                V
                                       RECYCLER.get()执行完毕
                                                |
                                                V
                 执行PooledUnsafeDirectByteBuf.reuse(maxCapacity)，这一步会设置maxCapacity，
                 执行updater.resetRefCnt(this)，将当前PooledUnsafeDirectByteBuf的引用次数设置为2，
                 设置readerIndex和writerIndex为0，设置markedReaderIndex、markedWriterIndex为0。
                                                |
                                                V
           PooledUnsafeDirectByteBuf.newInstance(maxCapacity)执行完毕，得到PooledUnsafeDirectByteBuf对象
                                                |
                                                V
              执行DirectArena.allocate(PoolThreadCache, PooledUnsafeDirectByteBuf, reqCapacity)
                                                |
                                                V
              使用size2SizeIdx(reqCapacity)获取sizeIdx，如果 sizeIdx <= smallMaxSizeIdx，
              执行tcacheAllocateSmall(cache, buf, reqCapacity, sizeIdx)；如果sizeIdx < nSizes，
         执行tcacheAllocateNormal(cache, buf, reqCapacity, sizeIdx)，否则执行allocateHuge(buf, normCapacity)
                                                |
                                                V
          DirectArena.allocate(PoolThreadCache, initialCapacity, maxCapacity)执行完毕，获得PooledUnsafeDirectByteBuf
                                                |
                                                V
                执行PooledByteBufAllocator.toLeakAwareBuffer(PooledUnsafeDirectByteBuf)
                                                |
                                                V
                执行AbstractByteBuf.leakDetector.track(PooledUnsafeDirectByteBuf)
                                                |
                                                V
              根据samplingInterval为界限获取随机数，只有0该PooledUnsafeDirectByteBuf才会被进行内存泄漏检测，
              被检测的ByteBuf会创建DefaultResourceLeak(ByteBuf, ReferenceQueue, allLeaks)并返回。
                                                |
                                                V
         AbstractByteBuf.leakDetector.track(PooledUnsafeDirectByteBuf)执行完毕，获取ResourceLeakTracker leak
                                                |
                                                V
              如果ByteBuf会进行内存泄露检测，那么会根据内存检测的Level，创建SimpleLeakAwareByteBuf(buf, leak)或者
                        AdvancedLeakAwareByteBuf(buf, leak)，返回ByteBuf。
                                                |
                                                V
                PooledByteBufAllocator.newDirectBuffer(capacity, Int.MAX_VALUE)执行完毕
                                                |
                                                V
                           PooledByteBufAllocator.directBuffer(capacity)执行完毕
                                                |
                                                V
             PooledByteBufAllocator.DEFAULT.buffer(capacity)执行完毕，获取到PooledUnsafeDirectByteBuf

### DirectArena.allocate(PoolThreadCache, PooledUnsafeDirectByteBuf, reqCapacity)
&emsp;&emsp; 在执行分配直接内存时，根据requestCapacity，会有small、normal、huge三种不同的分配方式。
对于Netty分配单位，每4个为一组，每组以2的幂次进行增长，只有第一组比较特殊。reqCapacity在28672(3.5KB index <= 38)及以下为small，
28672(> 3.5KB index >= 39) - 16777216(<= 2MB index <= 75)为normal，大于2MB归为huge，
对于huge类型Netty会直接分配堆外内存，不会进行池化处理。Netty默认的一个page的大小为8192bit(1KB)，默认的一个chunk的大小为1677721bit(2MB)。

   Netty PoolArena包含一个smallSubPagePools，一个smallSubPagePools包含39个PoolSubpage，负责维护small内存块。
   PoolArena中包含qInit、q000、q025、q050、q075、q100六个PoolChunkList，根据内存使用率的不同，维护PoolChunk。
   Netty每次分配内存时，由PoolChunk向操作系统申请内存，PoolSubpage需要从PoolChunk中分配，small级别的内存从PoolSubpage中分配。
   
   尝试在线程缓存上分配内存 -> 根据sizeIdx获取PoolSubpage -> 如果没有可用的PoolSubpage，需要申请一个normal级的内存块
   -> 依次从q050、q025、q000、qinit、q075上分配内存。

    Netty内存分配策略：
        根据reqCapacity也就是size会计算出一个index，index对应的isSubPage为true，则为small(4KB)，
        如果reqCapacity大于2MB，则为huge，其余为normal。
        index           Dsize       size        isSubPage
        0               0           16          1
        1               16          32          1
        2               16          48          1
        3               16          64          1
        4               16          80          1
        5               16          96          1
        6               16          112         1
        7               16          128         1
        8               32          160         1
        9               32          192         1
        10              32          224         1
        11              32          256         1
        12              64          320         1
        13              64          384         1
        14              64          448         1
        15              64          512         1
        16              128         640         1
                       ......
        38              4096        28672(3.5KB)1
        39              4096        32768(4KB)  0
        40              8192        40960(5KB)  0
                       ......
        75             2097152     16777216     0
                                                
      PoolThreadCache.allocateSmall(DirectArena, PooledUnsafeDirectByteBuf, reqCapacity, sizeIdx)
                                                |
                                                V
      根据PooledThreadCache.smallSubPageDirectCaches数组，用sizeIdx作为索引，获取SubPageMemoryRegionCache
                                                |
                                                V
         执行PoolThreadCache.allocate(MemoryRegionCache, PooledUnsafeDirectByteBuf, reqCapacity)
                                                |
                                                V
       执行SubPageMemoryRegionCache.allocate(PooledUnsafeDirectByteBuf, reqCapacity, PoolThreadCache)
                                                |
                                                V
                     如果通过SubPageMemoryRegionCache，根据线程small级内存缓存分配内存失败
                                                |
                                                V
                     根据sizeIdx，在DirectArena.smallSubpagePools中获取一个PoolSubpage，
                  如果PoolSubpage.next指向自身，说明没有创建过PoolSubpage，此时需要分配一个normal
                                                |
                                                V
                 如果需要分配normal级内存，执行 allocateNormal(buf, reqCapacity, sizeIdx, cache)
                                                |
                                                V
                        依次从q050、q025、q000、qInit、q075 PoolChunkList中分配内存
                                                |
                                                V
              如果分配失败，执行DirectArena.newChunk(pageSize, nPSize, pageShifts, chunkSize)创建PoolChunk
                                                |
                                                V
                    执行PlatformDependent.allocateDirectNoCleaner(capacity)创建ByteBuffer
                                                |
                                                V
          利用Unsafe.allocateMemory(capacity)分配堆外内存，获取地址address，然后创建DirectByteBuffer(address, capacity)
                                                |
                                                V
           创建PoolChunk(DirectArena, DirectByteBuffer, pageSize, pageShifts, chunkSize, maxPageIdx, offset)
                                                |
                                                V
                DirectArena.newChunk(pageSize, nPSize, pageShifts, chunkSize)执行完毕，返回PoolChunk
                                                |
                                                V
           执行PoolChunk<DirectByteBuffer>.allocate(PooledUnsafeDirectByteBuf, reqCapacity, sizeIdx, PoolThreadCache)
                                                |
                                                V
                                 执行PoolChunk.allocateSubpage(sizeIdx)
                                                |
                                                V
                         根据sizeIdx，从DirectArena.smallSubpagePools中获取Poolsubpage head
                                                |
                                                V
                     创建一个新的PoolSubpage，并将新的PoolSubpage设置为head的next节点，
                PoolSubpage(head, PoolChunk, pageShifts, runOffset, runSize, elemSize)
                                                |
                                                V
              对新创建的PoolSubpage，将其放入到PoolChunk.subpages中，并执行PoolSubpage.allocate() 
                                                |
                                                V
        执行PoolChunk.initBuf(PooledUnsafeDirectByteBuf, bytebuffer, handle, reqCapacity, PoolThreadCache)
                                                |
                                                V
        执行PooledUnsafeDirectByteBuf.init(PoolChunk, nioBuffer, handle, offset, length, maxLength, PoolThreadCache),
        设置PooledUnsafeDirectByteBuf参数：chunk = PoolChunk, memory = chunk.memory, allocator = arena.allocator,
            设置handle、offset、length、maxLength，并根据memory的address + offset设置memoryAddress
                                                |
                                                V
           PoolChunk.initBuf(PooledUnsafeDirectByteBuf, bytebuffer, handle, reqCapacity, PoolThreadCache)执行完毕
                                                |
                                                V
         PoolChunk<DirectByteBuffer>.allocate(PooledUnsafeDirectByteBuf, reqCapacity, sizeIdx, PoolThreadCache)执行完毕
                                                |
                                                V
                        执行PoolChunkList qInit.add(PoolChunk)将创建的PoolChunk放置到PoolChunkList中，
                        每当PoolChunk分配过内存后，都会计算使用率，进而将其分配到对应的PoolChunkList中
                                                |
                                                V
                        allocateNormal(buf, reqCapacity, sizeIdx, cache)执行完毕
                                                |
                                                V
                                  DiretArena.allocationsSmall自增
                                                |
                                                V
          DirectArena.tcacheAllocateSmall(PoolThreadCache, PooledUnsafeDirectByteBuf, reqCapacity)执行完毕
                                                
