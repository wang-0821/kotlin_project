package xiao.boot.base.env

import xiao.boot.base.ServerConstants.ENV_BETA
import xiao.boot.base.ServerConstants.ENV_DEVELOP
import xiao.boot.base.ServerConstants.ENV_PRODUCTION
import xiao.boot.base.ServerConstants.ENV_STAGING
import xiao.boot.base.ServerConstants.ENV_TEST

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