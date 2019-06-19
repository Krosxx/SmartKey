package cn.vove7.smartkey.key

import cn.vove7.smartkey.tool.Vog
import com.russhwolf.settings.Settings
import kotlin.reflect.KProperty


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
) : IKey() {

    private var value: T? = defaultValue


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

        fun getSettings(name: String): Settings =
            cache.getOrPut(name) { getSettingsImpl(name) }


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
