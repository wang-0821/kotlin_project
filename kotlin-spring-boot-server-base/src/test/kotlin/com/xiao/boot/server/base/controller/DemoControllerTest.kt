package com.xiao.boot.server.base.controller

import com.xiao.base.util.JsonUtils
import com.xiao.boot.base.env.EnvInfoProvider
import com.xiao.boot.base.testing.KtSpringTestBase
import com.xiao.boot.server.base.ServerBaseApplication
import com.xiao.boot.server.base.exception.KtExceptionResponse
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestTemplate

/**
 * @author lix wang
 */
@SpringBootTest(
    classes = [ServerBaseApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class DemoControllerTest : KtSpringTestBase() {
    @Autowired
    lateinit var envInfoProvider: EnvInfoProvider

    @Test
    fun `test request hello world`() {
        val result = RestTemplate().exchange(
            "http://localhost:${envInfoProvider.port()}/api/v1/demo/helloWorld",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            String::class.java,
            mapOf<String, String>()
        )
        Assertions.assertEquals(result.body, "hello world")
    }

    @Test
    fun `test request post method`() {
        val result = RestTemplate().exchange(
            "http://localhost:${envInfoProvider.port()}/api/v1/demo/printInput",
            HttpMethod.POST,
            HttpEntity("hello world"),
            String::class.java,
            mapOf<String, String>()
        )
        Assertions.assertEquals(result.body, "hello world")
    }

    @Test
    fun `test throw exception`() {
        val exception = assertThrows<HttpServerErrorException.InternalServerError> {
            RestTemplate().exchange(
                "http://localhost:${envInfoProvider.port()}/api/v1/demo/throwException",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                String::class.java,
                mapOf<String, String>()
            )
        }

        Assertions.assertEquals(exception.statusCode.value(), HttpStatus.INTERNAL_SERVER_ERROR.value())
        val exceptionResponse = JsonUtils.deserialize(exception.responseBodyAsString, KtExceptionResponse::class.java)
        Assertions.assertEquals(exceptionResponse.message, "throw Exception")
    }
}