package com.xiao.boot.base.util

import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
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
        val encryptKeyByteArray = Base64.getDecoder().decode(encryptKey)
        val secretKeySpec = SecretKeySpec(encryptKeyByteArray, AES_ALGORITHM)
        AES_CIPHER.init(Cipher.ENCRYPT_MODE, secretKeySpec)
        return Base64.getEncoder().encodeToString(AES_CIPHER.doFinal(content.toByteArray()))
    }

    fun aesDecrypt(encryptedContent: String, encryptKey: String): String {
        check(encryptedContent.isNotEmpty() && encryptKey.isNotEmpty())
        val encryptKeyByteArray = Base64.getDecoder().decode(encryptKey)
        val secretKeySpec = SecretKeySpec(encryptKeyByteArray, AES_ALGORITHM)
        AES_CIPHER.init(Cipher.DECRYPT_MODE, secretKeySpec)
        return String(AES_CIPHER.doFinal(Base64.getDecoder().decode(encryptedContent)))
    }

    /**
     * generate random aes encrypt key
     * [length] can be 128、192、256
     */
    fun genAesRandomKey(length: Int = 256): String {
        val keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM)
        keyGenerator.init(length)
        val key = keyGenerator.generateKey()
        return Base64.getEncoder().encodeToString(key.encoded)
    }
}