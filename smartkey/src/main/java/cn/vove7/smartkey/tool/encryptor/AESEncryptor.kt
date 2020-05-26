package cn.vove7.smartkey.tool.encryptor

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * # AESEncryptor
 *
 * @author Vove
 * 2019/6/18
 */
object AESEncryptor : Encryptor {

    private val key: String

    init {
        val serialNo = getDeviceSerialNumber()
        key = SHA("$serialNo#\$ERDTS\$D%F^Gojikbh").substring(0, 16)
    }

    private fun getDeviceSerialNumber(): String {
        return "DAIUD*&YEU#QQR"
    }

    /**
     * SHA加密
     *
     * @param strText 明文
     * @return
     */
    private fun SHA(strText: String?): String {
        // 返回值
        var strResult: String? = null
        // 是否是有效字符串
        if (!strText.isNullOrEmpty()) {
            try {
                // SHA 加密开始
                val messageDigest = MessageDigest.getInstance("SHA-256")
                // 传入要加密的字符串
                messageDigest.update(strText.toByteArray())
                val byteBuffer = messageDigest.digest()
                val strHexString = StringBuffer()
                for (i in byteBuffer.indices) {
                    val hex = Integer.toHexString(0xff and byteBuffer[i].toInt())
                    if (hex.length == 1) {
                        strHexString.append('0')
                    }
                    strHexString.append(hex)
                }
                strResult = strHexString.toString()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }

        }

        return strResult ?: "bh&T*&GRjhlbli&^(*&RPEfiuw3y"
    }


    override fun encrypt(content: String): String {
        return try {
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            val keyspec = SecretKeySpec(key.toByteArray(), "AES")
            cipher.init(Cipher.ENCRYPT_MODE, keyspec)
            val encrypted = cipher.doFinal(content.toByteArray())
            Base64.encodeToString(encrypted, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            content
        }

    }

    override fun decrypt(content: String): String {
        return try {
            val encrypted1 = Base64.decode(content, Base64.NO_WRAP)
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            val keyspec = SecretKeySpec(key.toByteArray(), "AES")
            cipher.init(Cipher.DECRYPT_MODE, keyspec)
            val original = cipher.doFinal(encrypted1)
            String(original)
        } catch (e: Exception) {
            e.printStackTrace()
            content
        }
    }
}