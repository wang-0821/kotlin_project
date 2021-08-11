package xiao.boot.mybatis.factory

import org.mybatis.spring.mapper.MapperFactoryBean
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.Environment
import xiao.base.util.activeProfileType
import xiao.boot.base.env.ProfileType
import java.lang.reflect.Proxy

/**
 *
 * @author lix wang
 */
class KtMapperFactoryBean<T>(mapperInterface: Class<T>) : MapperFactoryBean<T>(mapperInterface), EnvironmentAware {
    private lateinit var environment: Environment

    @Suppress("UNCHECKED_CAST")
    override fun getObject(): T {
        return if (environment.activeProfileType() == ProfileType.TEST) {
            super.getObject()!!
        } else {
            Proxy.newProxyInstance(
                mapperInterface.classLoader,
                arrayOf(mapperInterface),
                KtSpringMapperProxy(mapperInterface, super.getObject()!!)
            ) as T
        }
    }

    override fun setEnvironment(environment: Environment) {
        this.environment = environment
    }
}