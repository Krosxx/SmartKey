package cn.vove7.smartkey.key

import cn.vove7.smartkey.BaseConfig
import cn.vove7.smartkey.annotation.Config
import cn.vove7.smartkey.annotation.parseConfigAnnotation
import cn.vove7.smartkey.settings.JsonSettings
import cn.vove7.smartkey.settings.PropertiesSettings
import cn.vove7.smartkey.tool.JsonHelper
import cn.vove7.smartkey.tool.Vog
import cn.vove7.smartkey.tool.encryptor.AESEncryptor
import cn.vove7.smartkey.tool.toJson
import com.russhwolf.settings.Settings
import com.russhwolf.settings.contains
import com.russhwolf.settings.minusAssign
import kotlin.reflect.KClass

/**
 * # IKey
 *
 * @author Vove
 * 2019/6/19
 */
abstract class IKey(
        /**
         * 指定泛型class
         */
        internal val cls: Class<*>,

        /**
         * 加密存储数据
         */
        internal val encrypt: Boolean = false,
        /**
         * 自定义key
         */
        internal val key: String? = null
) {
    class MyConfig(config: Config, thisRef: Any) {
        val name: String = config.name.let {
            if (it.isEmpty()) thisRef::class.simpleName?.toLowerCase()
                ?: DEFAULT_CONFIG_NAME else it
        }
        val implCls = config.implCls
    }

    lateinit var config: MyConfig


    @Synchronized
    fun initConfig(thisRef: Any?) {
        if (!::config.isInitialized) {
            checkNotNull(thisRef) {
                "请在类中属性使用"
                //反射获取类注解@Config(name)
            }
            config = MyConfig(if (thisRef is BaseConfig) {
                thisRef.config
            } else parseConfigAnnotation(thisRef), thisRef)
            Vog.d("初始化配置：${config.name} ${config.implCls.simpleName}")
        }
    }

    val settings: Settings by lazy { getSettingsImpl(config) }

    companion object {

        val DEFAULT_CONFIG_NAME = "config"
        /**
         * 默认存储实现
         */
        var DEFAULT_SETTING_IMPL_CLS: KClass<out Settings> =
            when (System.getProperties().getProperty("sun.desktop")) {
                "windows" -> JsonSettings::class
                else -> JsonSettings::class
            }

        fun getSettingsImpl(config: MyConfig): Settings {
            val con = config.parseImplCls.java.getConstructor(String::class.java)
                ?: throw Exception("Settings实现类必须有String的构造函数")

            return con.newInstance(config.name)
        }

        val MyConfig.parseImplCls: KClass<out Settings>
            get() = if (implCls == Settings::class) DEFAULT_SETTING_IMPL_CLS
            else implCls

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
            val content = getString(key, (defaultValue as String?) ?: "")
            return (if (encrypt) AESEncryptor.decrypt(content)
            else content) as T
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
                JsonHelper.fromJson(value, cls) ?: defaultValue
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
            val v = if (encrypt) AESEncryptor.encrypt(value)
            else value
            putString(key, v)
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
