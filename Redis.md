* [1.Lettuce执行流程](#1)

<h2 id="1">1.Lettuce执行流程</h2>
&emsp;&emsp; Lettuce是目前最流行的Redis客户端，基于Netty实现，连接实例可以在多个线程间兵法访问，
支持同步、异步、响应式的方式，是SpringBoot 2 默认的Redis客户端。

        RedisClient对象的创建需要两个参数：ClientResources 和 RedisURI。
        
        Lettuce 支持的 RedisClient默认实现为DefaultClientResources，DefaultClientResource构造参数为
        DefaultClientResources.Builder。
        
        Luttuce RedisURI配置：
            1，单点配置：redis://[username:password@]host[:port][/databaseNumber]，如：redis://localhost:6379。
            2，哨兵配置：redis-sentinel://[password@]host[:port][,host2[:port2]][/databaseNumber]#sentinelMasterId，
                例如：redis-sentinel:// localhost:6378,localhost:6379/0#myMaster。
            3，集群配置：节点配置为 redis://[username:password@]host[:port]。
        
                      执行DefaultClientResources(Builder)构建ClientResources对象
                                              |
                                              V
        设置sharedEventLoopGroupProvider(boolean)和eventLoopGroupProvider(EventLoopGroupProvider)
                                              |
                                              V
                如果Builder.eventLoopGroupProvider为空，设置sharedEventLoopGroupProvider = false，
        设置eventLoopGroupProvider = DefaultEventLoopGroupProvider(max(Builder.ioThreadPoolSize, 2))；
            如果不为空，设置sharedEventLoopGroupProvider = Builder.sharedEventLoopGroupProvider,
                设置eventLoopGroupProvider = Builder.eventLoopGroupProvider。
                                              |
                                              V
              设置sharedEventExecutor(boolean)和eventExecutorGroup(EventExecutorGroup)
                                              |
                                              V
                如果Builder.eventExecutorGroup为空，设置eventExecutorGroup = 
                    DefaultEventLoopGroupProvider.createEventLoopGroup(DefaultEventExecutorGroup.class, 
                        max(Builder.computationThreadPoolSize, 2))，设置sharedEventExecutor = false；
                如果不为空，设置sharedEventExecutor = Builder.sharedEventExecutor，
                设置eventExecutorGroup = Builder.eventExecutorGroup。
                                              |
                                              V
                              设置sharedTimer(boolean)和timer(Timer)
                                              |
                                              V
                如果Builder.timer为空，设置timer = HashedWheelTimer(DefaultThreadFactory("lettuce-timer"))，
              设置sharedTimer为false；如果不为空，设置timer = Builder.timer，sharedTimer = Builder.sharedTimer。
                                              |
                                              V
                                       设置eventBus(EventBus)
                                              |
                                              V
            如果Builder.eventBus为空，设置eventBus = DefaultEventBus(Schedulers.fromExecutor(eventExecutorGroup))，
                            如果不为空，设置eventBus = Builder.eventBus。
                                              |
                                              V
              设置sharedCommandLatencyRecorder(boolean)和commandLatencyRecorder(CommandLatencyRecorder)
                                              |
                                              V
              如果commandLatencyRecorder为空，根据Builder.commandLatencyCollectorOptions创建CommandLatencyRecorder，
              设置sharedCommandLatencyRecorder = false；如果不为空，设置commandLatencyRecorder = Builder.commandLatencyRecorder，
                        设置sharedCommandLatencyRecorder = Builder.sharedCommandLatencyRecorder。
                                              |
                                              V
              设置commandLatencyPublisherOptions = Builder.commandLatencyPublisherOptions
                                              |
                                              V
          如果commandLatencyRecorder可用，commandLatencyPublisherOptions不为空，创建metricEventPublisher，否则设为空。
                                              |
                                              V
                设置dnsResolver(DnsResolver)为Builder.dnsResolver，或者为DnsResolvers.UNRESOLVED
                                              |
                                              V
          设置socketAddressResolver为Builder.socketAddressResolver，或者为SocketAddressResolver(dnsResolver)
                                              |
                                              V
          设置reconnectDelay为Builder.reconnectDelay，nettyCustomizer为Builder.nettyCustomizer，tracing为Builder.tracing
                                              |
                                              V
                如果sharedTimer = false 且 timer == HashedWheelTimer，执行timer.start()
                                              |
                                              V
                              DefaultClientResources对象创建完毕
                         
                                             
                                              
