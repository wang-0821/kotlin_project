package com.xiao.boot.server.demo

import com.xiao.boot.server.base.annotations.CoroutineSpringBootApplication
import org.springframework.transaction.annotation.EnableTransactionManagement

/**
 *
 * @author lix wang
 */
@EnableTransactionManagement
@CoroutineSpringBootApplication
class DemoServerConfiguration