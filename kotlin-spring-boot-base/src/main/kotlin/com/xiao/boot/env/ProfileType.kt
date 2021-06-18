package com.xiao.boot.env

import com.xiao.boot.env.EnvConstants.ENV_BETA
import com.xiao.boot.env.EnvConstants.ENV_DEVELOP
import com.xiao.boot.env.EnvConstants.ENV_PRODUCTION
import com.xiao.boot.env.EnvConstants.ENV_STAGING
import com.xiao.boot.env.EnvConstants.ENV_TEST

/**
 *
 * @author lix wang
 */
enum class ProfileType(val profileName: String) {
    TEST(ENV_TEST),
    DEV(ENV_DEVELOP),
    BETA(ENV_BETA),
    STAGING(ENV_STAGING),
    PRODUCTION(ENV_PRODUCTION)
}