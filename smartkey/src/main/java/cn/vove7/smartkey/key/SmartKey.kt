package cn.vove7.smartkey.key

import cn.vove7.smartkey.BaseConfig
import cn.vove7.smartkey.annotation.Config
import cn.vove7.smartkey.tool.Vog
import com.russhwolf.settings.Settings
import java.util.*
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
    key: String? = null,
    encrypt: Boolean = false
): SmartKey<T> {
    return SmartKey(defaultValue, T::class.java, encrypt, key)
}

/**
 * # SmartKey
 * 由于值缓存，在多进程会存在问题
 * 支持多进程可使用[NoCacheKey]
 * @author 11324
 * 2019/4/22
 */
class SmartKey<T> constructor(
    val defaultValue: T,
    cls: Class<*>,
    encrypt: Boolean = false,
    key: String? = null
) : IKey(cls, encrypt, key) {

    /**
     * 缓存值
     */
    private var value: T? = defaultValue

    private var init = false

    operator fun getValue(thisRef: Any?, p: KProperty<*>): T {
        initConfig(thisRef)
        val k = key ?: p.name
        if (!init) {
            keys[p.name] = this
            value = getSettingsFromCache(config).get(k, defaultValue, cls, encrypt)
            Vog.d("初始化值：$k : $value")
        }
        init = true
        return value ?: defaultValue
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, t: T) {
        initConfig(thisRef)
        val k = key ?: property.name
        if (!init) {
            keys[property.name] = this
        }
        init = true
        Vog.d("设置值：$k = $t")
        value = t
        getSettingsFromCache(config).set(k, t, encrypt)
    }

    companion object {

        /**
         * WeakHashMap缓存
         *
         * 配置名 -> Settings
         */
        private val cache = WeakHashMap<String, Settings>()

        //property name to SmartKey
        private val keys = WeakHashMap<String, SmartKey<*>>()

        fun clearCache(config: BaseConfig) {
            try {
                config::class.java.declaredFields
                    .filter { it.type == SmartKey::class.java }
                    .forEach {
                        it.isAccessible = true
                        //设置init标志
                        (it.get(config) as SmartKey<*>).apply {
                            init = false
                            value = null
                        }
                    }
            } catch (e: Throwable) {
                System.err.println("SmartKey clear err: ${e.message}")
            }
        }

        /**
         * 从缓存获取
         * @return Settings
         */
        fun getSettingsFromCache(config: MyConfig): Settings =
            cache.getOrPut(config.name) { getSettingsImpl(config) }

        fun getSettingsFromCache(config: Config, thisRef: Any): Settings =
            cache.getOrPut(config.name) { getSettingsImpl(MyConfig(config, thisRef)) }

    }
}
