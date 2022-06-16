## TLS

&emsp;&emsp; TLS1.3包括3个子协议：alert、handshake、record。层级关系为：HTTP -> handshake/alert -> record。

            ————>   HTTP
           |
    应用层------->   handshake |  alert
      |    |
      |     ---->   record
      |
      V
    传输层------->   TCP
      |        
      |
      |
      V
    ......

### 握手协议(handshake)
&emsp;&emsp; handshake协议负责协商使用的TLS版本、加密算法、哈希算法、密钥材料和其他通信过程有关的信息，对服务器进行身份认证，
对客户端进行可选的身份认证，最后对整个握手阶段信息进行完整化校验以防范中间人攻击，是整个TLS协议的核心。TLS1.3握手按照严格的顺序发送不同的报文，
各报文包含标识自己种类的数据和其他握手协商有关的扩展数据。任何时候收到不按顺序发出的报文种类，服务器都会报错，并转交给alert协议层处理。

    TLS1.3握手应按照以下顺序组织报文：
    1，客户端发送Client Hello(CH)报文，包含有关密钥协商以及其他与TLS连接建立有关的扩展给服务端。
    2，服务器发送Server Hello(SH)报文，包含有关密钥协商的扩展返还给客户端，双方根据CH和SH的协商结果可以得出密钥材料。
    3，如果客户端发送的CH报文不满足服务端的需要，服务端会发送一个Hello Retry Request报文给客户端，要求客户端重新发送符合要求的CH报文。
    4，利用密钥材料和前两个报文的哈希值，使用HKDF计算出一个handshake_key，此后握手阶段的信息受该密钥保护。
    5，服务端发送Encrypted Extension(EE)报文，包含其他与密钥协商无关的扩展数据给客户端。
    6，如果使用公钥证书进行身份验证，服务器端发送Certificate报文和Certificate Verify(CV)报文给客户端。
    7，如果需要对客户端身份进行认证，服务器端还需要发送Certificate Request(CR)报文给对方请求客户端发送证书。
    8，服务器端发送Finished报文。表明服务端到客户端信道的握手阶段结束，理论上不得再由该信道发送任何握手报文。
    9，如果客户端收到了服务器端的CR报文，返回自己的Certificate报文和CV报文。
    10，客户端发送Finished报文，表明握手阶段结束，可以正式开始会话通信。Finished报文使用会话密钥对上述所有握手信息进行HMAC签名，
        校验签名可以检验握手阶段的完整性，也可以验证双方是否协商出了一致的密钥。
        
&emsp;&emsp; 所有握手阶段的报文，都是由record协议层进行加解密、分片、填充、转发的。在这个过程中，如果发生了任何错误，则会发送一个alert报文，
转交给alert协议层进行错误处理。

### SSLSocket建立过程

    1，构建SSLSocketFactory。
        1.1，首先构建TrustManager：
            val factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            factory.init(null as KeyStore?)
            factory!!.trustManagers[0] as X509TrustManager
        1.2，根据TrustManager构建SSLSocketFactory：
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, arrayOf(SslTrustManagerFactorySelector.select().trustManager()), null)
            val sslSocketFactory = sslContext.socketFactory
    
    2，建立Socket连接。
        Socket().connect(InetSocketAddress address, int connectTimeout)

    3，创建SSLSocket。
        sslSocketFactory!!.createSocket(socket, host, port, true) as SSLSocket

    4，配置SSLSocket。
        sslSocket.setEnabledProtocols(specToApply.tlsVersions) 设置tls版本。
        sslSocket.setEnabledCipherSuites(specToApply.cipherSuites) 设置加密套件。
        SSLParameters.setApplicationProtocols(String[] protocols) 设置协议类型："http/1.1"，"h2"。

    5，握手。
        sslSocket.startHandshake()

    