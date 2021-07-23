package com.xiao.boot.base.env

import com.xiao.boot.base.ServerConstants.ENV_BETA
import com.xiao.boot.base.ServerConstants.ENV_DEVELOP
import com.xiao.boot.base.ServerConstants.ENV_PRODUCTION
import com.xiao.boot.base.ServerConstants.ENV_STAGING
import com.xiao.boot.base.ServerConstants.ENV_TEST

/**
 *
 * @author lix wang
 */
enum class ProfileType(val profileName: String) {
    TEST(ENV_TEST),
    DEV(ENV_DEVELOP),
    BETA(ENV_BETA),
    STAGING(ENV_STAGING),
    PRODUCTION(ENV_PRODUCTION);

    companion object {
        fun match(profileName: String): ProfileType? {
            for (profile in values()) {
                if (profile.profileName == profileName) {
                    return profile
                }
            }
            return null
        }
    }
}