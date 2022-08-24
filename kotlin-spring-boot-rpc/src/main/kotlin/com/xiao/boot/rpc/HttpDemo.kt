package com.xiao.boot.rpc

import org.apache.hc.client5.http.async.methods.AbstractCharResponseConsumer
import org.apache.hc.client5.http.async.methods.SimpleHttpRequests
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity
import org.apache.hc.client5.http.impl.async.HttpAsyncClients
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.concurrent.FutureCallback
import org.apache.hc.core5.http.ContentType
import org.apache.hc.core5.http.HttpException
import org.apache.hc.core5.http.HttpResponse
import org.apache.hc.core5.http.NameValuePair
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.apache.hc.core5.http.message.BasicNameValuePair
import org.apache.hc.core5.http.nio.support.AsyncRequestBuilder
import java.io.IOException
import java.nio.CharBuffer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Future


class HttpDemo {
    @Throws(IOException::class)
    fun demoUsage() {
        HttpClients.createDefault().use { httpclient ->
            val httpGet = HttpGet("http://httpbin.org/get")
            httpclient.execute(httpGet).use { response1 ->
                println(response1.code.toString() + " " + response1.reasonPhrase)
                val entity1 = response1.entity
                // do something useful with the response body
                // and ensure it is fully consumed
                EntityUtils.consume(entity1)
            }
            val httpPost = HttpPost("http://httpbin.org/post")
            val nvps: MutableList<NameValuePair> = ArrayList()
            nvps.add(BasicNameValuePair("username", "vip"))
            nvps.add(BasicNameValuePair("password", "secret"))
            httpPost.entity = UrlEncodedFormEntity(nvps)
            httpclient.execute(httpPost).use { response2 ->
                println(response2.code.toString() + " " + response2.reasonPhrase)
                val entity2 = response2.entity
                // do something useful with the response body
                // and ensure it is fully consumed
                EntityUtils.consume(entity2)
            }
        }
    }

    fun asyncDemoUsage() {
        HttpAsyncClients.createDefault().use { httpclient ->
            // Start the client
            httpclient.start()

            // Execute request
            val request1 = SimpleHttpRequests.get("http://httpbin.org/get")
            val future: Future<SimpleHttpResponse> = httpclient.execute(request1, null)
            // and wait until response is received
            val response1: SimpleHttpResponse = future.get()
            println(request1.requestUri + "->" + response1.code)

            // One most likely would want to use a callback for operation result
            val latch1 = CountDownLatch(1)
            val request2 = SimpleHttpRequests.get("http://httpbin.org/get")
            httpclient.execute(request2, object : FutureCallback<SimpleHttpResponse> {
                override fun completed(response2: SimpleHttpResponse) {
                    latch1.countDown()
                    println(request2.requestUri + "->" + response2.code)
                }

                override fun failed(ex: java.lang.Exception) {
                    latch1.countDown()
                    println(request2.requestUri + "->" + ex)
                }

                override fun cancelled() {
                    latch1.countDown()
                    println(request2.requestUri + " cancelled")
                }
            })
            latch1.await()

            // In real world one most likely would want also want to stream
            // request and response body content
            val latch2 = CountDownLatch(1)
            val producer3 = AsyncRequestBuilder.get("http://httpbin.org/get").build()
            val consumer3 = object : AbstractCharResponseConsumer<HttpResponse?>() {
                var response: HttpResponse? = null

                @Throws(HttpException::class, IOException::class)
                override fun start(response: HttpResponse?, contentType: ContentType?) {
                    this.response = response
                }

                override fun capacityIncrement(): Int {
                    return Int.MAX_VALUE
                }

                @Throws(IOException::class)
                override fun data(data: CharBuffer, endOfStream: Boolean) {
                    // Do something useful
                }

                @Throws(IOException::class)
                override fun buildResult(): HttpResponse? {
                    return response
                }

                override fun releaseResources() {}
            }
            httpclient.execute(producer3, consumer3, object : FutureCallback<HttpResponse?> {
                override fun failed(ex: Exception) {
                    latch2.countDown()
                    println(request2.requestUri + "->" + ex)
                }

                override fun cancelled() {
                    latch2.countDown()
                    println(request2.requestUri + " cancelled")
                }

                override fun completed(result: HttpResponse?) {
                    latch2.countDown()
                    println(request2.requestUri + "->" + result?.getCode())
                }
            })
            latch2.await()
        }
    }

    companion object {
        @Throws(IOException::class)
        @JvmStatic
        fun main(args: Array<String>) {
            val httpDemo = HttpDemo()
            httpDemo.demoUsage()
            httpDemo.asyncDemoUsage()
        }
    }
}