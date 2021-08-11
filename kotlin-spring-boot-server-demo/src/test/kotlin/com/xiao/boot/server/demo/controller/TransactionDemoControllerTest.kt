package com.xiao.boot.server.demo.controller

import com.xiao.boot.base.env.EnvInfoProvider
import com.xiao.boot.mybatis.testing.KtSpringMybatisTestBase
import com.xiao.boot.mybatis.testing.TestKtSpringDatabase
import com.xiao.boot.server.demo.DemoServerConfiguration
import com.xiao.boot.server.demo.mybatis.mapper.UserMapper
import com.xiao.boot.server.demo.properties.DemoDatabase
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestTemplate

/**
 *
 * @author lix wang
 */
@TestKtSpringDatabase(
    database = DemoDatabase::class,
    mappers = [UserMapper::class]
)
@SpringBootTest(
    classes = [DemoServerConfiguration::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class TransactionDemoControllerTest : KtSpringMybatisTestBase() {
    @Autowired
    lateinit var userMapper: UserMapper
    @Autowired
    lateinit var envInfoProvider: EnvInfoProvider

    @Test
    fun `test spring @Transactional rollback`() {
        Assertions.assertEquals(userMapper.selectById(1).username, "user_1")
        assertThrows<HttpServerErrorException.InternalServerError> {
            RestTemplate().exchange(
                "http://localhost:${envInfoProvider.port()}/api/v1/demo/transaction/testRollback" +
                    "?id=1&username=name123",
                HttpMethod.POST,
                HttpEntity.EMPTY,
                Unit::class.java,
                mapOf<String, String>()
            )
        }
        Assertions.assertEquals(userMapper.selectById(1).username, "user_1")
    }

    @Test
    fun `test rollback on transaction service`() {
        Assertions.assertEquals(userMapper.selectById(1).username, "user_1")
        assertThrows<HttpServerErrorException.InternalServerError> {
            RestTemplate().exchange(
                "http://localhost:${envInfoProvider.port()}/api/v1/demo/transaction/testTransactionServiceRollback" +
                    "?id=1&username=name123",
                HttpMethod.POST,
                HttpEntity.EMPTY,
                Unit::class.java,
                mapOf<String, String>()
            )
        }
        Assertions.assertEquals(userMapper.selectById(1).username, "user_1")
    }
}