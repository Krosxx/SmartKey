package cn.vove7.smartkey.tool.encryptor

/**
 * # AESEncryptor
 *
 * @author Vove
 * 2019/6/18
 */
object AESEncryptor : Encryptor {
    override fun encrypt(content: String): String {
        return content
    }

    override fun decrypt(content: String): String {
        return content
    }
}