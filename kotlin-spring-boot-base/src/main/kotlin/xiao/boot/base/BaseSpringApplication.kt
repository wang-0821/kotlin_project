package xiao.boot.base

import org.springframework.boot.SpringApplication
import xiao.boot.base.ServerConstants.SERVER_NAME_KEY

/**
 *
 * @author lix wang
 */
abstract class BaseSpringApplication {
    companion object {
        @JvmStatic
        fun start(cls: Class<*>, serverName: String, vararg args: String) {
            System.setProperty(SERVER_NAME_KEY, serverName)
            SpringApplication.run(cls, *args)
        }
    }
}