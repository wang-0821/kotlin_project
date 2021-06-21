package com.xiao.boot.base.util

import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 *
 * @author lix wang
 */
object SecureUtils {
    private const val AES_ALGORITHM = "AES"
    private const val AES_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding"
    private val AES_CIPHER = Cipher.getInstance(AES_CIPHER_ALGORITHM)

    fun aesEncrypt(content: String, encryptKey: String): String {
        check(content.isNotEmpty() && encryptKey.isNotEmpty())
        val secretKeySpec = SecretKeySpec(encryptKey.toByteArray(), AES_ALGORITHM)
        AES_CIPHER.init(Cipher.ENCRYPT_MODE, secretKeySpec)
        return Base64.getEncoder().encodeToString(AES_CIPHER.doFinal(content.toByteArray()))
    }

    fun aesDecrypt(content: String, encryptKey: String): String {
        check(content.isNotEmpty() && encryptKey.isNotEmpty())
        val secretKeySpec = SecretKeySpec(encryptKey.toByteArray(), AES_ALGORITHM)
        AES_CIPHER.init(Cipher.DECRYPT_MODE, secretKeySpec)
        return String(AES_CIPHER.doFinal(Base64.getDecoder().decode(content)))
    }
}