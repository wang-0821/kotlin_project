package xiao.boot.base.testing

import org.springframework.test.context.ActiveProfiles
import xiao.base.testing.KtTestBase

/**
 *
 * @author lix wang
 */
@ActiveProfiles("test")
open class KtSpringTestBase : KtTestBase()