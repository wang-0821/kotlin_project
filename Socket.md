* [1.TCP连接](#1)

<h2 id="1">1.TCP连接</h2>
&emsp;&emsp; TCP连接经过三次握手，断开连接经过4次挥手。4此挥手时，断开发起方会在接收到FIN后，发送ACK，并等待2MS，然后再释放资源，
这是由于一个数据包最长存活时间为MSL(Maximum Segment Life)，如果发起方发送了ACK，那么如果另一方如果没收到会重传FIN，一来一回是就是2MSL。
而且，如果没有等待2MSL，那么新建立的连接可能会接收到旧连接的报文。

### CLOSE_WAIT及TIME_WAIT异常
&emsp;&emsp; CLOSE_WAIT只会出现在被断开连接的一方，出现大量CLOSE_WAIT的原因可能是一方发起了断开连接请求，另一方忙于读或者写，
没有关闭连接。TIME_WAIT大量出现，会导致端口耗尽。

    // TCP连接建立过程。
    1，主机A向主机B发起TCP连接，发送一条SYN报文，此时A主机状态从CLOSED变为SYN-SEND。
    2，主机B处于LISTEN状态，主机B接收到SYN包后，状态从LISTEN变成SYN-RECV，然后向主机A发送针对此SYN包的SYN/ACK包，确认收到SYN包。
    3，主机A收到SYN/ACK包后，状态从SYN-SEND转换为ESTABLISHED，然后向主机B发送SYN/ACK包。主机B收到SYN/ACK包后，也进入ESTABLISHED状态，
        此时主机A、B就能进行通信了。

                                A CLOSED                B LISTEN
                                 SYN-SEND ------------> SYN-RECV
                                               SYN
                              ESTABLISHED <------------ 
                                             SYN/ACK
                                          ------------> ESTABLISHED
                                             SYN/ACK

    // TCP连接断开过程。
    1，主机A向主机B发送FIN包，此时主机A状态从ESTABLISHED变成FIN_WAIT_1。
    2，主机B收到主机A发来的FIN包，状态从ESTABLISHED变为CLOSE_WAIT，并向主机A发送ACK包，表示准备断开。主机A在收到ACK包后，
        状态从FIN_WAIT_1变成FIN_WAIT_2，然后等待主机B发送确认断开的包。
    3，主机B在发送完所有未发送的数据包后，会再发送FIN包，此时主机B从CLOSE_WAIT状态，变为LAST_ACK状态。
    4，主机A收到主机B发送来的FIN包后，主机A状态从FIN_WAIT_2变为TIME_WAIT，然后发送ACK包，在等待2MSL后，如果还是没有收到回复，
        那么证明服务器已经正常关闭。主机B收到ACK包后，状态从LAST_ACK变为CLOSED。

                            A ESTABLISHED               B ESTABLISHED
                                FIN_WAIT_1 -----------> CLOSE_WAIT
                                               FIN
                                FIN_WAIT_2 <----------- 
                                               ACK   
                                TIME_WAIT  <----------- LAST_ACK   
                                               FIN
                                           -----------> CLOSED
                                               ACK