## HttpClient笔记

* [1.执行流程](#1)

<h2 id="1">1.执行流程</h2>
### CloseableHttpAsyncClient execute请求的流程
&emsp;&emsp; 目前我们在执行RPC时，通常使用异步请求避免阻塞，提高RPC性能。

                                    CloseableHttpAsyncClient
                                                |
                                                V
                    execute(SimpleHttpRequest, FutureCallback<SimpleHttpResponse>)
                                                |
                                                V
            execute(SimpleHttpRequest, HttpClientContext, FutureCallback<SimpleHttpResponse>)
                                                |
                                                V
            execute(SimpleRequestProducer, SimpleResponseConsumer, HttpClientContext, FutureCallback<SimpleHttpResponse>)
                                                |
                                                V
            InternalAbstractHttpAsyncClient.doExecute(null, SimpleRequestProducer, SimpleResponseConsumer, null, HttpClientContext, FutureCallback<SimpleHttpResponse>)
                                                |
                                                V
            SimpleRequestProducer.sendRequest(RequestChannel, HttpClientContext)
                                                |
                                                V
            RequestChannel.sendRequest(SimpleRequestProducer.request, null, HttpClientContext)
                                                |
                                                V 设置requestConfig
                    HttpClientContext.setRequestConfig(SimpleHttpRequest.getConfig())
                                                |
                                                V 确定http schema host name port
                    InternalHttpAsyncClient.determineRoute(HttpHost, HttpClientContext)
                                                |
                                                V
                    DefaultRoutePlanner.determineRoute(HttpHost, HttpClientContext)
                                                |
                                                V 
                    InternalHttpAsyncClient.createAsyncExecRuntime(null, HttpRoute)
                                                |
                                                V 设置HttpClientContext配置项
                    InternalAbstractHttpAsyncClient.setupContext(HttpClientContext)
                                                |
                                                V
        InternalAbstractHttpAsyncClient.executeImmediate(BasicHttpRequest, null, AsyncExecChain.Scope, AsyncExecCallback)
                                                |
                                                V chain中handler依次为: AsyncRedirectExec、AsyncProtocolExec、AsyncConnectExec、HttpAsyncMainClientExec
        InternalAbstractHttpAsyncClient.execChain.execute(BasicHttpRequest, null, AsyncExecChain.Scope, AsyncExecCallback)
                                                |
                                                V AsyncRedirectExec
        AsyncExecChainElement.handler.execute(BasicHttpRequest, null, AsyncExecChain.Scope, AsyncExecChain, AsyncExecCallback)
                                                |
                                                V
            AsyncRedirectExec.execute(BasicHttpRequest, null, AsyncExecChain.Scope, AsyncExecChain, AsyncExecCallback)
                                                |
                                                V
                AsyncRedirectExec.internalExecute(AsyncRedirectExec.State, AsyncExecChain, AsyncExecCallback)
                                                |
                                                V
                AsyncExecChain.proceed(BasicHttpRequest, null, AsyncExecChain.Scope, AsyncExecCallback)
                                                |
                                                V
            AsyncExecChainElement.next.execute(BasicHttpRequest, null, AsyncExecChain.Scope, AsyncExecCallback)
                                                |
                                                V AsyncProtocolExec
            AsyncExecChainElement.handler.execute(BasicHttpRequest, null, AsyncExecChain.Scope, AsyncExecCallback)
                                                |
                                                V
            AsyncProtocolExec.execute(BasicHttpRequest, null, AsyncExecChain.Scope, AsyncExecChain, AsyncExecCallback)
                                                |
                                                V
        AuthSupport.extractFromAuthority(BasicHttpRequest.schema(), BasicHttpRequest.getAuthority(), BasicCredentialsProvider)
                                                |
                                                V
        AsyncProtocolExec.internalExecute(AtomicBoolean, BasicHttpRequest, null, AsyncExecChain.Scope, AsyncExecChain, AsyncExecCallback)
                                                |
                                                V
            AsyncProtocolExec.httpProcessor.process(BasicHttpRequest, null, HttpClientContext)
                                                |
                                                V
                    DefaultHttpProcessor.process(BasicHttpRequest, null, HttpClientContext)
                                                |
                                                V requestInterceptors依次为：RequestDefaultHeaders、RequestUserAgent、RequestExpectContinue
                DefaultHttpProcessor.requestInterceptor(BasicHttpRequest, null, HttpClientContext)
                                                |
                                                V EntityDetails就是AsyncEntityProducer
                    RequestDefaultHeaders.process(BasicHttpRequest, EntityDetails, HttpClientContext)
                                                |
                                                V 这一步会把请求中没有，defaultHeaders中有的header加到request中
                    [RequestDefaultHeaders.defaultHeaders] BasicHttpRequest.addHeader(Header)
                                                |
                                                V
                RequestUserAgent.process(BasicHttpRequest, EntityDetails, HttpClientContext)
                                                |
                                                V 这一步会把请求中没有，userAgent有值的话，设置给User-Agent header
                BasicHttpRequest.addHeader("User-Agent", RequestUserAgent.userAgent)
                                                |
                                                V 有EntityDetails时，且requestConfig支持expectContinueEnabled，那么带Expect: 100-continue header
                RequestExpectContinue.process(BasicHttpRequest, EntityDetails, HttpClientContext)
                                                |
                                                V
                AsyncExecChain.proceed(BasicHttpRequest, null, AsyncExecChain.Scope, AsyncExecCallback)
                                                |
                                                V
            AsyncExecChainElement.next.execute(BasicHttpRequest, null, AsyncExecChain.Scope, AsyncExecCallback)
                                                |
                                                V
        AsyncExecChainElement.handler.execute(BasicHttpRequest, null, AsyncExecChain.Scope, AsyncExecChain, AsyncExecCallback)
                                                |
                                                V
        AsyncConnectExec.execute(BasicHttpRequest, null, AsyncExecChain.Scope, AsyncExecChain, AsyncExecCallback)
                                                |
                                                V 这一步判断AsyncExecRuntime中有没有endpointRef，没有需要执行这一步
        InternalHttpAsyncExecRuntime.acquireEndpoint(exchangeId, HttpRoute, userToken, HttpClientContext, FutureCallback)
                                                |
                                                V requestTimeout来自于：requestConfig.getConnectionRequestTimeout()
            InternalHttpAsyncExecRuntime.manager.lease(exchangeId, HttpRoute, userToken, requestTimeout, FutureCallback)
                                                |
                                                V
            PoolingAsyncClientConnectionManager.pool.lease(HttpRoute, null, requestTimeout, FutureCallback)
                                                |
                                                V 根据ConnectionRequestTimeout来对连接池加锁，最多等待这么长时间
                    StrictConnPool.lock.tryLock(requestTimeout.getDuration(), requestTimeout.getTimeUnit())
                                                |
                                                V 这一步如果已经到deadline，那么会直接return false
                        StrictConnPool.processPendingRequest(LeaseRequest)
                                                | HttpHost: lcHostname、port、schemeName、address相同，那么equals
                                                V HttpRoute: secure、tunnelled、layered、targetHost、localAddress、proxyChain相同，那么equals
                        PerRoutePool<HttpRoute, C> pool = this.routeToPool.get(route)
                                                |
                                                V 当pool中没有可用连接时，先获取当前Route最大连接数
                        int maxPerRoute = this.maxPerRoute.get(route) | defaultMaxPerRoute
                                                |
                                                V
                        PoolEntry<HttpRoute, C> entry = pool.createEntry(this.timeToLive)
                                                |
                                                V 将创建的新连接PoolEntry，添加到leased中
                                    StrictConnPool.leased.add(entry)
                                                |
                                                V
                            StrictConnPool.completedRequests.add(LeaseRequest)
                                                |
                                                V 释放锁
                                    StrictConnPool.lock.unlock()
                                                |
                                                V
                                StrictConnPool.fireCallbacks()
                                                |
                                                V 如果没有成功complete，那么会执行StrictConnPool.release(result, true)
                            LeaseRequest.getFuture().completed(PoolEntry)
                                                |
                                                V
        FutureCallback<PoolEntry<HttpRoute, ManagedAsyncClientConnection>>.completed(PoolEntry<HttpRoute, ManagedAsyncClientConnection> poolEntry)
                                                |
                                                V
            leaseCompleted(PoolEntry<HttpRoute, ManagedAsyncClientConnection> poolEntry)
                                                |
                                                V
                AsyncConnectionEndpoint endpoint = new InternalConnectionEndpoint(poolEntry)
                                                |
                                                V
                    FutureCallback<AsyncConnectionEndpoint>.completed(endpoint)
                                                |
                                                V
                    BasicFuture.callback.completed(InternalConnectionEndpoint)
                                                |
                                                V
            FutureCallback<AsyncExecRuntime> callback.completed(InternalHttpAsyncExecRuntime.this)
                                                |
                                                V
    AsyncConnectExec.proceedToNextHop(State, BasicHttpRequest, AsyncEntityProducer, AsyncExecChain.Scope, AsyncExecChain, AsyncExecCallback)
                                                |
                                                V
                        AsyncConnectExec.routeDirector.nextStep(HttpRoute, null)
                                                |
                                                V 如果有proxys，那么step为n+1，没有proxy step为1
                            BasicRouteDirector.nextStep(HttpRoute, null)
                                                |
                                                V
    ComplexFuture.setDependency(InternalHttpAsyncExecRuntime.connectEndpoint(HttpClientContext, FutureCallback<AsyncExecRuntime>))
                                                |
                                                v connectTimeout来自于requestConfig.getConnectTimeout()
        InternalHttpAsyncExecRuntime.manager.connect(InternalConnectionEndpoint, DefaultConnectingIOReactor, 
            connectTimeout, HttpVersionPolicy, HttpClientContext, FutureCallback<AsyncConnectionEndpoint>)
                                                |
                                                V
        PoolingAsyncClientConnectionManager.connectionOperator.connect(DefaultConnectingIOReactor, HttpHost, null, 
            connectTimeout, HttpVersionPolicy, FutureCallback<ManagedAsyncClientConnection>)
                                                |
                                                V
        DefaultAsyncClientConnectionOperator.connect(DefaultConnectingIOReactor, HttpHost, null, connectTimeout, 
            HttpVersionPolicy attachment, FutureCallback<ManagedAsyncClientConnection> callback)
                                                |
                                                V
        DefaultAsyncClientConnectionOperator.sessionRequester.connect(DefaultConnectingIOReactor, HttpHost, null, null,
            connectTimeout, HttpVersionPolicy attachment, FutureCallback<IOSession> callback)
                                                |
                                                V dns域名解析获取ip www.xxx.com
                        MultihomeIOSessionRequester.dnsResolver.resolve(hostName)
                                                |
                                                V
            DefaultConnectingIOReactor.connect(HttpHost, InetSocketAddress remoteAddress, null, timeout, 
                    HttpVersionPolicy, FutureCallback<IOSession> callback)
                                                |
                                                V
            DefaultConnectingIOReactor.workerSelector.next().connect(HttpHost, InetSocketAddress remoteAddress, 
                    null, timeout, HttpVersionPolicy, FutureCallback<IOSession> callback)
                                                |
                                                V
    new IOSessionRequest(HttpHost, InetSocketAddress remoteAddress, null, timeout, HttpVersionPolicy, FutureCallback<IOSession> callback)
                                                | 这一步将请求添加到了SingleCoreIoReactor的队列中
                        SingleCoreIOReactor.requestQueue.add(IOSessionRequest)   
                                                |
                                                V
                                SingleCoreIOReactor.selector.wakeup()
                                                |
                                                V
                                




                                                |
                                                V
                            new Future<AsyncConnectionEndpoint>()
                                                |
                                                V
        AsyncExecChain.Scope.cancellableDependency.setDependency(new Future<AsyncConnectionEndpoint>())
                                                |
                                                V
            返回最初的 new ComplexFuture<>(callback) 作为Future，至此CloseableHttpAsyncClient.execute链路结束


### CloseableHttpAsyncClient 创建流程
&emsp;&emsp; 在创建CloseableHttpAsyncClient时，会先设置参数，构建InternalHttpAsyncClient对象

                                HttpAsyncClients.custom().build()
                                                |
                                                V 设置connectionManager
    AsyncClientConnectionManager connManagerCopy = this.connManager | PoolingAsyncClientConnectionManagerBuilder.create().build()
                                                |
                                                V 设置connectionKeepAliveStrategy
    ConnectionKeepAliveStrategy keepAliveStrategyCopy = this.keepAliveStrategy | DefaultConnectionKeepAliveStrategy.INSTANCE
                                                |
                                                V 设置UserTokenHandler
    UserTokenHandler userTokenHandlerCopy = this.userTokenHandler | (connectionStateDisabled ? NoopUserTokenHandler.INSTANCE : DefaultUserTokenHandler.INSTANCE)
                                                |
                                                V 新建NamedElementChain
                NamedElementChain<AsyncExecChainHandler> execChainDefinition = new NamedElementChain<>()
                                                |
                                                V 向NamedElementChain尾节点加入HttpAsyncMainClientExec
    execChainDefinition.addLast(new HttpAsyncMainClientExec(keepAliveStrategyCopy, userTokenHandlerCopy), MAIN_TRANSPORT)
                                                |
                                                V
    AuthenticationStrategy targetAuthStrategyCopy = this.targetAuthStrategy | DefaultAuthenticationStrategy.INSTANCE
                                                |
                                                V
    AuthenticationStrategy proxyAuthStrategyCopy = this.proxyAuthStrategy | DefaultAuthenticationStrategy.INSTANCE
                                                |
                                                V
    String userAgentCopy = this.userAgent | VersionInfo.getSoftwareInfo("Apache-HttpAsyncClient","org.apache.hc.client5", getClass())
                                                |
                                                V 向NamedElementChain头节点加入AsyncConnectExec
            execChainDefinition.addFirst(new AsyncConnectExec(
                new DefaultHttpProcessor(new RequestTargetHost(), new RequestUserAgent(userAgentCopy)), 
                proxyAuthStrategyCopy), CONNECT)
                                                |
                                                V 向NamedElementChain头节点加入AsyncProtocolExec，这一步包含HttpProcessor
            execChainDefinition.addFirst(new AsyncProtocolExec(httpProcessor, targetAuthStrategyCopy, proxyAuthStrategyCopy),
                ChainElement.PROTOCOL.name())
                                                |
                                                V 如果redirectHandlingDisabled=false，向NamedElementChain头节点加入AsyncRedirectExec
            execChainDefinition.addFirst(new AsyncRedirectExec(routePlannerCopy, redirectStrategyCopy),
                    ChainElement.REDIRECT.name());
                                                |
                                                V
            new IdleConnectionEvictor((ConnPoolControl<?>) connManagerCopy, maxIdleTime,  maxIdleTime).start()
                                                |
                                                V
            IOEventHandlerFactory ioEventHandlerFactory = new HttpAsyncClientEventHandlerFactory(...)
                                                |
                                                V
            DefaultConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(
                HttpAsyncClientEventHandlerFactory, IOReactorConfig, NamedThreadFactory, LoggingIOSessionDecorator,
                LoggingExceptionCallback, null, sessionShutdownCallback)
                                                |
                                                V
    DefaultConnectingIOReactor.workerCount = ioReactorConfig.getIoThreadCount() | Runtime.getRuntime().availableProcessors()
                                                |
                                                V
                DefaultConnectingIOReactor.workers = new SingleCoreIOReactor[workerCount]
                                                | <---------------------------------------------
                                                V 根据worker size循环                            |
                        SingleCoreIOReactor dispatcher = new SingleCoreIOReactor(               |
                            LoggingExceptionCallback,                                           |
                            HttpAsyncClientEventHandlerFactory,                                 |
                            IOReactorConfig                                                     |
                            LoggingIOSessionDecorator,                                          |
                            null,                                                               |
                            sessionShutdownCallback)                                            |
                                                |                                               |
                                                V                                               |
                        DefaultConnectingIOReactor.workers[i] = dispatcher                      |
                                                |                                               |
                                                V                                               |
                    threads[i] = threadFactory.newThread(new IOReactorWorker(dispatcher));      |
                                                |-----------------------------------------------
                                                V
                    DefaultConnectingIOReactor.ioReactor = new MultiCoreIOReactor(this.workers, threads)
                                                |
                                                V
                    DefaultConnectingIOReactor.workerSelector = IOWorkers.newSelector(workers)
                                                |
                                                V
                                new DefaultConnectingIOReactor()执行完毕
                                                |
                                                V
                        execInterceptors != null 向execChainDefinition中添加ExecInterceptorEntry
                                                |
                                                V
            new InternalHttpAsyncClient(DefaultConnectingIOReactor ioReactor, AsyncExecChainElement execChain,
                AsyncPushConsumerRegistry, NamedThreadFactory, PoolingAsyncClientConnectionManager, DefaultRoutePlanner,
                HttpVersionPolicy, Lookup<CookieSpecFactory>, Lookup<AuthSchemeFactory>, BasicCookieStore,
                BasicCredentialsProvider, RequestConfig defaultConfig, List<Closeable> closeables)


### CloseableHttpAsyncClient 启动流程
&emsp;&emsp; 我们在创建CloseableHttpAsyncClient，会调用start来启动这个异步client，来执行异步IO任务
                                                
                                CloseableHttpAsyncClient.start()
                                                |
                                                V
                                AbstractHttpAsyncClientBase.start()
                                                |
                                                V InternalHttpAsyncClient本身会new一个单线程异步执行这一步
                            AbstractHttpAsyncClientBase.ioReactor.start()
                                                |
                                                V
                            DefaultConnectingIOReactor.ioReactor.start()
                                                |
                                                V 这一步根据之前创建的线程size，循环启动线程
                                MultiCoreIOReactor.threads[i].start()
                                                |
                                                V
                    Thread.start()时，会执行run()方法，run()方法里面会执行Thread.target.run()
                                                |
                                                V
                                        IOReactorWorker.run()
                                                |
                                                V
                                IOReactorWorker.ioReactor.execute()
                                                |
                                                V SingleCoreIOReactor
                                AbstractSingleCoreIOReactor.execute()
                                                |
                                                V
                                    SingleCoreIOReactor.doExecute()
                                                |
                                                V 默认1S 获取目前已经ready的IO事件数量
                        int readyCount = SingleCoreIOReactor.selector.select(this.selectTimeoutMillis)
                                                |
                                                V中间还有拉events，清理关闭的session等操作
                            SingleCoreIOReactor.processPendingConnectionRequests()
                                                | 循环处理requestQueue中的请求，单次最多处理10000条
                                                V
                            SocketChannel socketChannel = SocketChannel.open()
                                                |
                                                V
                SingleCoreIOReactor.processConnectionRequest(SocketChannel, IOSessionRequest)
                                                |
                                                V
                
                        SingleCoreIOReactor.prepareSocket(socketChannel.socket())
                                                |
                                                V
                        socket.setTcpNoDelay(this.reactorConfig.isTcpNoDelay())
                        socket.setKeepAlive(this.reactorConfig.isSoKeepalive())
                        socket.setSendBufferSize(this.reactorConfig.getSndBufSize())
                        socket.setReceiveBufferSize(this.reactorConfig.getRcvBufSize())
                        socket.setTrafficClass(this.reactorConfig.getTrafficClass())
                        socket.setSoLinger(true, linger)
                                                |
                                                V
                        socketChannel.connect(IOSessionRequest.remoteAddress)
                                                |
                                                V 一个selectionKey由一组selector + socketChannel + ops(目前是9)确定
            socketChannel.register(this.selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ)
                                                |
                                                V
            new InternalConnectChannel(SelectionKey, socketChannel, sessionRequest, new InternalDataChannelFactory())
                                                |
                                                V
                            SelectionKey.attach(InternalConnectChannel)
                                                |
                                                V
                                    IOSessionRequest.assign(channel)
                                                | SingleCoreIOReactor会循环执行selector.select，监听ready的事件并处理
                                                V
                    SingleCoreIOReactor.processEvents(Set<SelectionKey> selectedKeys)
                                                |
                                                V
                    SelectionKey.attachment().handleIOEvent(SelectionKey.readyOps())
                                                |
                                                V 建连首次收到的是8
                            InternalConnectChannel.onIOEvent(readyOps)
                                                |
                                                V 校验now - InternalConnectChannel.initTime <= requestConfig.connectTimeout
                    InternalConnectChannel.checkTimeout(System.currentTimeMillis())
                                                |
                                                V 创建InternalDataChannel
        InternalConnectChannel.dataChannelFactory.create(SelectionKey, SocketChannel,NamedEndpoint,Object attachment)
                                                |
                                                V
                    InternalConnectChannel.sessionRequest.completed(InternalDataChannel)
                                                |
                                                V
                DefaultAsyncClientConnectionOperator.FutureCallback<IOSession>.completed(InternalDataChannel)
                                                |
                                                V 创建DefaultManagedAsyncClientConnection
                        DefaultManagedAsyncClientConnection(InternalDataChannel)
                                                |
                                                V handshakeTimeout = connectTimeout
        DefaultAsyncClientConnectionOperator.tlsStrategy.upgrade(DefaultManagedAsyncClientConnection, HttpHost, 
            SocketAddress localAddress, SocketAddress remoteAddress, HttpVersionPolicy attachment, Timeout handshakeTimeout)
                                                |
                                                V
                    DefaultManagedAsyncClientConnection.startTls(SSLContext, HttpHost, )
                                                |
                                                V
    PoolingAsyncClientConnectionManager.FutureCallback<ManagedAsyncClientConnection>.completed(DefaultManagedAsyncClientConnection)
                                                |
                                                V 这一步设置poolEntry的connRef、validityDeadline、expiryDeadline
                    PoolEntry.assignConnection(DefaultManagedAsyncClientConnection)
                                                |
                                                V
        InternalHttpAsyncExecRuntime.FutureCallback<AsyncConnectionEndpoint>.completed(AsyncConnectionEndpoint)
                                                |
                                                V
            AsyncConnectExec.FutureCallback<AsyncExecRuntime>.completed(InternalHttpAsyncExecRuntime)
                                                |
                                                V
        AsyncConnectExec.proceedToNextHop(State, BasicHttpRequest, AsyncEntityProducer entityProducer, 
            AsyncExecChain.Scope, AsyncExecChain, AsyncExecCallback asyncExecCallback)
                                                |
                                                V
        AsyncExecChainElement.AsyncExecChain.proceed(BasicHttpRequest, AsyncEntityProducer entityProducer, 
            AsyncExecChain.Scope, AsyncExecCallback)
                                                |
                                                V
            HttpAsyncMainClientExec.execute(BasicHttpRequest, AsyncEntityProducer entityProducer, 
                AsyncExecChain.Scope, AsyncExecCallback)
                                                |
                                                V 创建AsyncClientExchangeHandler
            AsyncClientExchangeHandler internalExchangeHandler = new AsyncClientExchangeHandler()
                                                |
                                                V
        InternalHttpAsyncExecRuntime.execute(String exchangeId, internalExchangeHandler, HttpClientContext)
                                                |
                                                V
                AsyncConnectionEndpoint.setSocketTimeout(requestConfig.getResponseTimeout())
                                                |
                                                V
    PoolingAsyncClientConnectionManager.execute(String exchangeId, AsyncClientExchangeHandler, null, HttpClientContext)
                                                |
                                                V
            ManagedAsyncClientConnection connection = getValidatedPoolEntry().getConnection()
                                                |
                                                V
                connection.submitCommand(RequestExecutionCommand, Command.Priority.NORMAL)
                                                |
                                                V
            InternalDataChannel.enqueue(RequestExecutionCommand, Command.Priority.IMMEDIATE)
                                                |
                                                V
    InternalDataChannel.currentSessionRef.get().enqueue(RequestExecutionCommand, Command.Priority.IMMEDIATE)
                                                |
                                                V
                    IOSessionImpl.commandQueue.addFirst(RequestExecutionCommand)
                                                |
                                                V OP_WRITE值为4 InternalConnectChannel.sessionRequest.completed执行完毕
            IOSessionImpl.key.interestOps(this.key.interestOps() | SelectionKey.OP_WRITE)
                                                |
                                                V SelectionKey.OP_CONNECT值为8
                        InternalDataChannel.handleIOEvent(SelectionKey.OP_CONNECT)
                                                |
                                                V 设置IOSessionImpl.lastWriteTime、lastEventTime为currentTimeMillis
                                    IOSessionImpl.updateWriteTime()
                                                |
                                                V
                            IOEventHandler.outputReady(IOSessionImpl)
                                                |
                                                V 设置socketTimeout = session.SocketTimeout  session.SocketTimeout = connectTimeout
                                SSLIOSession.initialize(SSLIOSession)
                                                |
                                                V
                        SSLIOSession.initializer.initialize(HttpHost, sslEngine)
                                                |
                                                V
                            SSLIOSession.doHandshake(SSLIOSession)
                                                |
                                                V
                    SSLIOSession.doWrap(EMPTY_BUFFER, ByteBuffer outEncryptedBuf)
                                                |
                                                V
                            SSLIOSession.doUnwrap(ByteBuffer, ByteBuffer)
                                                |
                                                V 做完ssl handshake后会执行以下方法
                        SSLIOSession.ensureHandler().connected(SSLIOSession)