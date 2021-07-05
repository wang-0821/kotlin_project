package com.xiao.boot.base.testing

import com.xiao.base.testing.KtTestBase
import org.springframework.test.context.ActiveProfiles

/**
 *
 * @author lix wang
 */
@ActiveProfiles("test")
open class KtSpringTestBase : KtTestBase()