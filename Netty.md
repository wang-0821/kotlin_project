## Netty notes.

### Server端
&emsp;&emsp; Netty启动过程中，涉及到ServerBootstrap、EventLoopGroup(group、childGroup)、Channel、
ChannelHandler(childHandler、handler)。

### EventLoopGroup
&emsp;&emsp; ServerBootstrap需要设置group，通常使用NioEventLoopGroup。在创建NioEventLoopGroup时，会创建一个EventExecutor数组(children)，
数组大小默认为处理器的两倍。在使用NioEventLoopGroup时，EventExecutor数组中的元素都是NioEventLoop。

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
            包含                                         包含
            
    ChannelPromise -----> Channel
           |        包含
           | 包含
           V
     EventExecutor(NioEventLoop) 
     