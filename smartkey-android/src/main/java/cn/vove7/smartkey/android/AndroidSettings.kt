package cn.vove7.smartkey.android

import android.annotation.SuppressLint
import android.content.Context
import cn.vove7.smartkey.android.AndroidSettings.Companion.context
import cn.vove7.smartkey.key.*
import cn.vove7.smartkey.tool.Vog
import com.russhwolf.settings.Settings
import kotlin.reflect.KClass

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
    keyId: Int? = null,
    encrypt: Boolean = false
): SmartKey<T> {
    val k = keyId?.let { context.getString(it) }
    return smartKey(defaultValue, k, encrypt)
}

/**
 * [NoCacheKey]
 *
 * @param defaultValue T
 * @param encrypt Boolean
 * @param key String?
 * @param keyId Int?
 * @return NoCacheKey<T>
 */
inline fun <reified T> noCacheKey(
    defaultValue: T,
    keyId: Int? = null,
    encrypt: Boolean = false
): NoCacheKey<T> {
    val k = keyId?.let { context.getString(it) }
    return noCacheKey(defaultValue, k, encrypt)
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
        @JvmOverloads
        @JvmStatic
        fun init(context: Context, settingImplCls: KClass<out Settings> = AndroidSettings::class) {
            IKey.DEFAULT_SETTING_IMPL_CLS = settingImplCls
            Vog.init(100)//不输出日志
            Companion.context = context
        }


        private fun create(configName: String): Settings =
            AndroidSettingsImpl(context, configName)

        fun s(keyId: Int) = context.getString(keyId)
    }

}

