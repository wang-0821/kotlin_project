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
                                                V
        AsyncExecChainElement.handler.execute(BasicHttpRequest, null, AsyncExecChain.Scope, AsyncExecChain, AsyncExecCallback)
                                                |
                                                V
        