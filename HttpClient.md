## HttpClient笔记

* [1.执行流程](#1)

<h2 id="1">1.执行流程</h2>
### CloseableHttpAsyncClient
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
                                                V
            
        