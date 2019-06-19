package cn.vove7.smartkey.android

import android.annotation.SuppressLint
import android.content.Context
import cn.vove7.smartkey.tool.Vog
import cn.vove7.smartkey.android.AndroidSettings.Companion.context
import cn.vove7.smartkey.key.*
import com.russhwolf.settings.Settings

/**
 * 委托属性 持久化存储
 *
 * 具体化泛型类型
 *
 * @param defaultValue T
 * @param encrypt Boolean
 * @param key String?
 * @param keyId Int?
 * @return SmartKey<T>
 */
inline fun <reified T> smartKey(
        defaultValue: T,
        encrypt: Boolean = false,
        key: String? = null,
        keyId: Int? = null): SmartKey<T> {
    val k = key ?: keyId?.let { context.getString(it) }
    return SmartKey(defaultValue, T::class.java, encrypt, k)
}

inline fun <reified T> noCacheKey(
        defaultValue: T,
        encrypt: Boolean = false,
        key: String? = null,
        keyId: Int? = null): NoCacheKey<T> {
    val k = key ?: keyId?.let { context.getString(it) }
    return NoCacheKey(defaultValue, T::class.java, encrypt, k)
}

/**
 * # AndroidSettings
 *
 * @author Vove
 * 2019/6/18
 */
@SuppressLint("StaticFieldLeak")
class AndroidSettings(private val configName: String) : Settings by create(configName) {

    companion object {
        lateinit var context: Context

        /**
         * 在Application中初始化
         * @param context Context
         * @param settingImplCls Class<out Settings> 持久化存储实现类，默认为AndroidSettings
         */
        fun init(context: Context, settingImplCls: Class<out Settings> = AndroidSettings::class.java) {
            IKey.settingImplCls = settingImplCls
            Vog.init(100)//不输出日志
            Companion.context = context
        }


        fun create(configName: String): Settings =
            AndroidSettingsImpl(context, configName)

    }

}

operator fun <T> SmartKey<*>.set(keyId: Int, value: T?) {
    val s = context.getString(keyId)
    SmartKey.getSettings(SmartKey.defaultConfigName).set(s, value)
}

inline operator fun <reified T> SmartKey<*>.get(keyId: Int, defaultValue: T?): T? {
    val s = context.getString(keyId)
    return SmartKey.getSettings(SmartKey.defaultConfigName).get(s, defaultValue, cls = T::class.java)
}