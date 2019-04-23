package cn.vove7.smartkey

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.StringRes
import cn.vove7.smartkey.SmartKey.Companion.auto
import com.russhwolf.settings.PlatformSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.contains
import com.russhwolf.settings.minusAssign
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.internal.ReflectionFactoryImpl

/**
 * # SmartKey
 *
 * @author 11324
 * 2019/4/22
 */
class SmartKey<T>(
        private val defaultValue: T,
        /**
         * 指定泛型class @see[auto]
         */
        private val cls: Class<*>? = null,
        /**
         * 加密存储数据
         */
        val encrypt: Boolean = false,
        /**
         * 自定义key
         */
        key: String? = null,
        @StringRes keyId: Int? = null
) {
    /**
     * 指定key
     */
    private val key: String? =
        key ?: if (keyId != null) {
            context.getString(keyId)
        } else null

    private lateinit var configName: String


    private fun intiConfigName(thisRef: Any?) {
        if (!init) {
            configName = if (thisRef == null) defaultName
            else ReflectionFactoryImpl().createKotlinClass(thisRef::class.java)
                    .findAnnotation<Config>()?.let {
                        if (it.name.isEmpty()) defaultName
                        else it.name
                    } ?: defaultName
            Vog.d("初始化配置文件名：$configName")
        }
    }


    private var value: T? = defaultValue

    private var init = false

    operator fun getValue(thisRef: Any?, p: KProperty<*>): T {
        intiConfigName(thisRef)
        val k = key ?: p.name
        if (!init) {
            value = getSettings(configName).get(k, defaultValue, cls)
            Vog.d("初始化值：$k : $value")
        }
        init = true
        return value ?: defaultValue
    }


    operator fun setValue(thisRef: Any?, property: KProperty<*>, t: T) {
        intiConfigName(thisRef)
        init = true
        val k = key ?: property.name
        Vog.d("设置值：$k = $t")
        value = t
        getSettings(configName).set(k, t)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        fun init(context: Context) {
            Vog.init(100)//不输出日志
            this.context = context
        }

        private val defaultName by lazy {
            context.packageName
        }

        private val cache = mutableMapOf<String, Settings>()

        fun getSettings(name: String = defaultName): Settings {
            return cache.getOrPut(name) { PlatformSettings.Factory(SmartKey.context).create(name) }
        }

        private fun s(@StringRes id: Int): String = context.getString(id)

        /**
         * 具体化泛型类型
         * @param defaultValue T
         * @param encrypt Boolean
         * @param key String?
         * @param keyId Int?
         * @return SmartKey<T>
         */
        inline fun <reified T> auto(
                defaultValue: T,
                encrypt: Boolean = false,
                key: String? = null,
                keyId: Int? = null): SmartKey<T> {
            return SmartKey(defaultValue, T::class.java, encrypt, key, keyId)
        }

        operator fun <T> set(key: String, value: T?) {
            getSettings().set(key, value)
        }

        operator fun <T> set(@StringRes keyId: Int, value: T?) {
            getSettings().set(s(keyId), value)
        }

        inline operator fun <reified T> get(key: String, defaultValue: T): T {
            return getSettings().get(key, defaultValue, cls = T::class.java)
        }

        inline operator fun <reified T> get(@StringRes keyId: Int, defaultValue: T): T? {
            return getSettings().get(context.getString(keyId), defaultValue, cls = T::class.java)
        }


        operator fun set(configName: String, k: String, v: Any) {
            getSettings(configName).set(k, v)
        }

        inline operator fun <reified T> get(configName: String, key: String, defaultValue: T): T? {
            return getSettings(configName).get(key, defaultValue)
        }

        operator fun set(configName: String, kId: Int, v: Any) {
            getSettings(configName).set(s(kId), v)
        }

        inline operator fun <reified T> get(configName: String, kid: Int, defaultValue: T): T? {
            return getSettings(configName).get(context.getString(kid), defaultValue)
        }

    }
}

@Suppress("UNCHECKED_CAST")
fun <T> Settings.get(key: String, defaultValue: T, cls: Class<*>? = null): T {
    if (key !in this) return defaultValue

    return when (defaultValue) {
        is Int -> {
            getInt(key, defaultValue) as T
        }
        is Long -> {
            getLong(key, defaultValue) as T
        }
        is String -> {
            getString(key, defaultValue) as T
        }
        is Float -> {
            getFloat(key, defaultValue) as T
        }
        is Double -> {
            getDouble(key, defaultValue) as T
        }
        is Boolean -> {
            getBoolean(key, defaultValue) as T
        }
        else -> {//gson
            if (key in this && cls != null) {
                GsonHelper.fromJson(
                        getString(key, defaultValue.toJson()), cls
                ) ?: defaultValue
            } else defaultValue
        }
    }
}

fun <T> Settings.set(key: String, value: T?) {
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
            putString(key, value.toJson())
        }
    }
}
