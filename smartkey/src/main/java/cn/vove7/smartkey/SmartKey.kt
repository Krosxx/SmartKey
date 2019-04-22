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

    private var value: T? = defaultValue

    private var init = false

    operator fun getValue(thisRef: Any?, p: KProperty<*>): T {
        val k = key ?: p.name
        if (!init) {
            value = get(k, defaultValue)
            Vog.d("初始化值：$k : $value")
        }
        return value ?: defaultValue
    }

    open fun get(k: String, defaultValue: T): T {
        return settings.get(k, defaultValue, cls)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, t: T) {
        init = true
        val k = key ?: property.name
        Vog.d("设置值：$k = $t")
        value = t
        settings.set(k, t)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        fun init(context: Context) {
            Vog.init(100)//不输出日志
            this.context = context
        }

        private fun s(@StringRes id: Int): String = context.getString(id)

        val settings: Settings
                by lazy { PlatformSettings.Factory(SmartKey.context).create("___") }

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

        fun <T> set(key: String, value: T?) {
            settings.set(key, value)
        }

        fun <T> set(@StringRes keyId: Int, value: T?) {
            settings.set(s(keyId), value)
        }

        inline fun <reified T> get(key: String, defaultValue: T): T {
            return settings.get(key, defaultValue, cls = T::class.java)
        }

        inline fun <reified T> get(@StringRes keyId: Int, defaultValue: T): T? {
            return settings.get(context.getString(keyId), defaultValue, cls = T::class.java)
        }
    }
}

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
