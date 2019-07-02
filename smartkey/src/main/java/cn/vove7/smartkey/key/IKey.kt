package cn.vove7.smartkey.key

import cn.vove7.smartkey.annotation.Config
import cn.vove7.smartkey.settings.FileSettings
import cn.vove7.smartkey.tool.GsonHelper
import cn.vove7.smartkey.tool.Vog
import cn.vove7.smartkey.tool.encryptor.AESEncryptor
import cn.vove7.smartkey.tool.toJson
import com.russhwolf.settings.Settings
import com.russhwolf.settings.contains
import com.russhwolf.settings.minusAssign
import kotlin.reflect.full.findAnnotation

/**
 * # IKey
 *
 * @author Vove
 * 2019/6/19
 */
abstract class IKey {


    lateinit var configName: String

    var init: Boolean = false

    @Synchronized
    fun initConfigName(thisRef: Any?) {
        if (!init) {
            configName = if (thisRef == null) SmartKey.defaultConfigName
            //反射获取类注解@Config(name)
            else thisRef::class.java.kotlin
                    .findAnnotation<Config>()?.let {
                        if (it.name.isEmpty()) SmartKey.defaultConfigName
                        else it.name
                    } ?: SmartKey.defaultConfigName
            Vog.d("初始化配置文件名：$configName")
        }
    }

    companion object {
        fun getSettingsImpl(configName: String): Settings = getSettingsFromCls(configName)

        operator fun <T> set(key: String, value: T?) {
            getSettingsImpl(SmartKey.defaultConfigName).set(key, value, false)
        }

        inline operator fun <reified T> get(key: String, defaultValue: T?): T? {
            return getSettingsImpl(SmartKey.defaultConfigName).get(key, defaultValue, cls = T::class.java, encrypt = false)
        }

        var settingImplCls: Class<out Settings> =
            when (System.getProperties().getProperty("sun.desktop")) {
                "windows" -> FileSettings::class.java
                else -> FileSettings::class.java
            }

        fun getSettingsFromCls(configName: String): Settings {
            val con = settingImplCls.getConstructor(String::class.java)
                ?: throw Exception("Settings实现类必须有String的构造函数")

            return con.newInstance(configName)
        }
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> Settings.get(key: String, defaultValue: T?, cls: Class<*>, encrypt: Boolean = false): T? {

    if (key !in this) return defaultValue

    return when (cls.simpleName) {//可空类型使用 SmartKey.auto()
        "Int", "Integer" -> {
            getInt(key, (defaultValue as Int?) ?: 0) as T
        }
        "Long" -> {
            getLong(key, (defaultValue as Long?) ?: 0L) as T
        }
        "String" -> {
            getString(key, (defaultValue as String?) ?: "") as T
        }
        "Float" -> {
            getFloat(key, (defaultValue as Float?) ?: 0f) as T
        }
        "Double" -> {
            getDouble(key, (defaultValue as Double?) ?: 0.0) as T
        }
        "Boolean" -> {
            getBoolean(key, (defaultValue as Boolean?) ?: false) as T
        }
        else -> {//数组, 实体类
            try {
                val value = getString(
                        if (encrypt) AESEncryptor.encryptKey(key) else key
                ).let {
                    if (encrypt) AESEncryptor.decrypt(it)
                    else it
                }
                GsonHelper.fromJson(value, cls) ?: defaultValue
            } catch (e: Exception) {
                e.printStackTrace()
                Vog.e("$key   de: $defaultValue")
                throw e
            }
        }
    }
}

fun <T> Settings.set(key: String, value: T?, encrypt: Boolean = false) {
    if (value == null) {
        this -= key
        return
    }
    when (value) {
        is Int -> {
            putInt(key, value)
        }
        is Long -> {
            putLong(key, value)
        }
        is String -> {
            putString(key, value)
        }
        is Float -> {
            putFloat(key, value)
        }
        is Double -> {
            putDouble(key, value)
        }
        is Boolean -> {
            putBoolean(key, value)
        }
        else -> {//gson
            val content = value.toJson().let {
                if (encrypt) AESEncryptor.encrypt(it)
                else it
            }
            putString(if (encrypt) AESEncryptor.encryptKey(key) else key, content)
        }
    }
}
