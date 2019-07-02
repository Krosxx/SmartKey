package cn.vove7.smartkey.tool.encryptor

/**
 * # Encryptor
 *
 * @author Vove
 * 2019/6/18
 */
interface Encryptor {

    fun encrypt(content: String): String

    fun encryptKey(key: String): String {
        return encrypt(key)
                .replace('\\', '-')
                .replace('/', '-')
    }

    fun decrypt(content: String): String
}