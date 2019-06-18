package cn.vove7.smartkey

import cn.vove7.smartkey.annotation.Config
import cn.vove7.smartkey.settings.WindowsSettings
import cn.vove7.smartkey.tool.GsonHelper
import cn.vove7.smartkey.tool.Vog
import cn.vove7.smartkey.tool.encryptor.AESEncryptor
import cn.vove7.smartkey.tool.toJson
import com.russhwolf.settings.Settings
import com.russhwolf.settings.contains
import com.russhwolf.settings.minusAssign
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation


/**
 * 委托属性 持久化存储
 *
 * 具体化泛型类型
 * @param defaultValue T
 * @param encrypt Boolean 加密只能用于String 和 实体类
 * @param key String?
 * @return SmartKey<T>
 */
inline fun <reified T> smartKey(
        defaultValue: T,
        encrypt: Boolean = false,
        key: String? = null): SmartKey<T> {
    return SmartKey(defaultValue, T::class.java, encrypt, key)
}

/**
 * # SmartKey
 *
 * @author 11324
 * 2019/4/22
 */
class SmartKey<T> constructor(
        private val defaultValue: T,

        /**
         * 指定泛型class
         */
        private val cls: Class<*>,

        /**
         * 加密存储数据
         */
        private val isEncrypt: Boolean = false,
        /**
         * 自定义key
         */
        private val key: String? = null
) {

    private lateinit var configName: String


    private fun initConfigName(thisRef: Any?) {
        if (!init) {
            configName = if (thisRef == null) defaultConfigName
            //反射获取类注解@Config(name)
            else thisRef::class.java.kotlin
                    .findAnnotation<Config>()?.let {
                        if (it.name.isEmpty()) defaultConfigName
                        else it.name
                    } ?: defaultConfigName
            Vog.d("初始化配置文件名：$configName")
        }
    }


    private var value: T? = defaultValue

    private var init = false

    operator fun getValue(thisRef: Any?, p: KProperty<*>): T {
        initConfigName(thisRef)
        val k = key ?: p.name
        if (!init) {
            value = getSettings(configName).get(k, defaultValue, cls, isEncrypt)
            Vog.d("初始化值：$k : $value")
        }
        init = true
        return value ?: defaultValue
    }


    operator fun setValue(thisRef: Any?, property: KProperty<*>, t: T) {
        initConfigName(thisRef)
        init = true
        val k = key ?: property.name
        Vog.d("设置值：$k = $t")
        value = t
        getSettings(configName).set(k, t, isEncrypt)
    }

    companion object {
        val defaultConfigName = "config"

        private val cache = mutableMapOf<String, Settings>()

        //缓存cls -> configName
//        private val configNameMap = mutableMapOf<Class<*>, String>()
//        private fun getConfigName(cls: Class<*>, dn: String): String {
//            return configNameMap.getOrPut(cls) { dn }
//        }

        var settingImplCls: Class<out Settings> =
            when (System.getProperties().getProperty("sun.desktop")) {
                "windows" -> WindowsSettings::class.java
                else -> WindowsSettings::class.java
            }

        fun getSettings(name: String): Settings =
            cache.getOrPut(name) { getSettingImpl(name) }


        private fun getSettingImpl(configName: String): Settings {

            return if (System.getProperties().getProperty("sun.desktop") == "windows") {
                WindowsSettings(configName)
            } else {
                getSettingsFromCls(configName)
            }
        }

        private fun getSettingsFromCls(configName: String): Settings {
            val con = settingImplCls.getConstructor(String::class.java)
                ?: throw Exception("Settings实现类必须有String的构造函数")

            return con.newInstance(configName)
        }

        operator fun <T> set(key: String, value: T?) {
            getSettings(defaultConfigName).set(key, value, false)
        }

        inline operator fun <reified T> get(key: String, defaultValue: T?): T? {
            return getSettings(defaultConfigName).get(key, defaultValue, cls = T::class.java, encrypt = false)
        }


        operator fun set(configName: String, k: String, v: Any) {
            getSettings(configName).set(k, v, encrypt = false)
        }

        inline operator fun <reified T> get(configName: String, key: String, defaultValue: T): T? {
            return getSettings(configName).get(key, defaultValue, T::class.java, false)
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
        else -> {
            try {
                if (key in this) {
                    val value = getString(key).let {
                        if (encrypt) AESEncryptor.decrypt(it)
                        else it
                    }
                    GsonHelper.fromJson(value, cls) ?: defaultValue

                } else defaultValue
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
            putString(key, content)
        }
    }
}
