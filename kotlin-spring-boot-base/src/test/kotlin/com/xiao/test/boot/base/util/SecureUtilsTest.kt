package com.xiao.test.boot.base.util

import com.xiao.base.testing.KtTestBase
import com.xiao.boot.base.util.SecureUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 *
 * @author lix wang
 */
class SecureUtilsTest : KtTestBase() {
    @Test
    fun `test aes encrypt and decrypt`() {
        val content = "112312413241242352341241"
        val encryptKey = SecureUtils.genAesRandomKey()
        val encryptedContent = SecureUtils.aesEncrypt(content, encryptKey)
        val decryptedContent = SecureUtils.aesDecrypt(encryptedContent, encryptKey)
        Assertions.assertEquals(content, decryptedContent)
    }
}