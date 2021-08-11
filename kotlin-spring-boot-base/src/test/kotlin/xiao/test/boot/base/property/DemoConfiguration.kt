package xiao.test.boot.base.property

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import xiao.test.boot.base.enhancer.ClassA
import xiao.test.boot.base.enhancer.ClassB

/**
 *
 * @author lix wang
 */
@Configuration
class DemoConfiguration {
    @Bean
    fun createClassA(): ClassA {
        return ClassA(createClassB())
    }

    @Bean
    fun createClassB(): ClassB {
        return ClassB()
    }
}