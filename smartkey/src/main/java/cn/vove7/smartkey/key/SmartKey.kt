package cn.vove7.smartkey.key

import cn.vove7.smartkey.BaseConfig
import cn.vove7.smartkey.annotation.Config
import cn.vove7.smartkey.tool.Vog
import com.google.gson.reflect.TypeToken
import com.russhwolf.settings.Settings
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
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
    val type = T::class.java
    if (List::class.java.isAssignableFrom(type)) {//数组类型
        throw IllegalArgumentException("`by smartKey` don't support list type, please use `by smartKeyList<ModelType>()")
    }
    if (Map::class.java.isAssignableFrom(type)) {
        throw IllegalArgumentException("`by smartKey` don't support map type, please use `by smartKeyList<ModelType>()")
    }
    if (Set::class.java.isAssignableFrom(type)) {
        throw IllegalArgumentException("`by smartKey` don't support set type, please use `by smartKeyList<ModelType>()")
    }
    return SmartKey(defaultValue, T::class.java, encrypt, key)
}

inline fun <reified M> smartKeyList(
        defaultValue: List<M> = listOf(),
        key: String? = null,
        encrypt: Boolean = false
): SmartKey<List<M>> {
    return smartKeyCollectionTyped(defaultValue, key, encrypt)
}

inline fun <reified M> smartKeySet(
        defaultValue: Set<M> = setOf(),
        key: String? = null,
        encrypt: Boolean = false
): SmartKey<Set<M>> {
    return smartKeyCollectionTyped(defaultValue, key, encrypt)
}

inline fun <reified K, reified T> smartKeyMap(
        defaultValue: Map<K, T> = mapOf(),
        key: String? = null,
        encrypt: Boolean = false
): SmartKey<Map<K, T>> {
    val type = TypeToken.getParameterized(Map::class.java, K::class.java, T::class.java).type
    return SmartKey(defaultValue, type, encrypt, key)
}

inline fun <reified CollectType : Collection<ItemType>, reified ItemType> smartKeyCollectionTyped(
        defaultValue: CollectType,
        key: String? = null,
        encrypt: Boolean = false): SmartKey<CollectType> {
    val type = TypeToken.getParameterized(CollectType::class.java, ItemType::class.java).type
    return SmartKey(defaultValue, type, encrypt, key)
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
        cls: Type,
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
