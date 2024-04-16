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
        val content = "meituanmain3"
        val encryptKey = SecureUtils.genAesRandomKey()
        val encryptedContent = SecureUtils.aesEncrypt(content, encryptKey)
        val decryptedContent = SecureUtils.aesDecrypt(encryptedContent, encryptKey)
        println(encryptedContent)
        Assertions.assertEquals(content, decryptedContent)
    }
}