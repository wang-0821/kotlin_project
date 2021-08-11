package xiao.test.boot.base.env

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import xiao.boot.base.env.EnvInfoProvider
import xiao.boot.base.env.ProfileType
import xiao.boot.base.testing.KtSpringTestBase
import xiao.test.boot.base.KtSpringBootBaseAutoConfiguration

/**
 *
 * @author lix wang
 */
@SpringBootTest(classes = [KtSpringBootBaseAutoConfiguration::class])
class EnvInfoTest : KtSpringTestBase() {
    @Autowired
    lateinit var envInfoProvider: EnvInfoProvider

    @Test
    fun `test env info bean`() {
        Assertions.assertEquals(envInfoProvider.port(), 8088)
        Assertions.assertEquals(envInfoProvider.profile(), ProfileType.TEST)
    }
}