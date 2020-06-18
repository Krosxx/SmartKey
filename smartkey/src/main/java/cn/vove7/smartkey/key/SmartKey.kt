package cn.vove7.smartkey.key

import cn.vove7.smartkey.tool.Vog
import com.google.gson.reflect.TypeToken
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
        throw IllegalArgumentException("`by smartKey` don't support map type, please use `by smartKeyMap<ModelType>()")
    }
    if (Set::class.java.isAssignableFrom(type)) {
        throw IllegalArgumentException("`by smartKey` don't support set type, please use `by smartKeySet<ModelType>()")
    }
    return SmartKey(defaultValue, T::class.java, encrypt, key)
}

/**
 * 实体数组  操作即更新(add, remove, ..)  默认空列表
 * ```kotlin
 * var modelList by smartKeyList<ListModel>()
 * ```
 *
 * @param defaultValue MutableList<M>
 * @param key String?
 * @param encrypt Boolean
 * @return SmartKey<MutableList<M>>
 */
inline fun <reified M> smartKeyList(
    defaultValue: MutableList<M> = mutableListOf(),
    key: String? = null,
    encrypt: Boolean = false
): SmartKey<MutableList<M>> {
    return smartKeyCollectionTyped(defaultValue, key, encrypt)
}

/**
 * 实体集合  操作即更新(add, remove, ..)  默认空集
 * ```kotlin
 * var modelSet by smartKeySet<ItemModel>()
 * ```
 *
 * @param defaultValue MutableSet<M>
 * @param key String?
 * @param encrypt Boolean
 * @return SmartKey<Set<M>>
 */
inline fun <reified M> smartKeySet(
    defaultValue: MutableSet<M> = mutableSetOf(),
    key: String? = null,
    encrypt: Boolean = false
): SmartKey<MutableSet<M>> {
    return smartKeyCollectionTyped(defaultValue, key, encrypt)
}

/**
 * Map 操作即更新(add, remove, ..)  默认 emptyMap
 * ```kotlin
 * var map by smartKeyMap<String, Int>()
 * ```
 * @param defaultValue MutableMap<K, T>
 * @param key String?
 * @param encrypt Boolean
 * @return SmartKey<MutableMap<K, T>>
 */
inline fun <reified K, reified T> smartKeyMap(
    defaultValue: MutableMap<K, T> = mutableMapOf(),
    key: String? = null,
    encrypt: Boolean = false
): SmartKey<MutableMap<K, T>> {
    val type = TypeToken.getParameterized(Map::class.java, K::class.java, T::class.java).type
    return SmartKey(defaultValue, type, encrypt, key)
}

inline fun <reified CollectType : Collection<ItemType>, reified ItemType> smartKeyCollectionTyped(
    defaultValue: CollectType,
    key: String? = null,
    encrypt: Boolean = false
): SmartKey<CollectType> {
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

    private val cacheKey get() = buildCacheKey(config.name, internalKey)

    operator fun getValue(thisRef: Any?, p: KProperty<*>): T {
        initConfig(thisRef)
        initKey(p.name)
        if (!init) {
            keys[cacheKey] = this
            value = wrapValue(settings.get(internalKey, defaultValue, cls, encrypt))
            Vog.d("初始化值：$internalKey : $value")
        }
        init = true
        return value ?: defaultValue
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, t: T) {
        initConfig(thisRef)
        initKey(property.name)
        if (!init) {
            keys[cacheKey] = this
        }
        init = true
        Vog.d("设置值：$internalKey = $t")
        value = wrapValue(t)
        settings.set(internalKey, t, encrypt)
    }

    companion object {

        // [config_name-property_name] to SmartKey
        private val keys = WeakHashMap<String, SmartKey<*>>()

        fun clearCache(config: KeyConfig) =
            keys.filter { (k, v) -> k.startsWith("${config.name}-") }
                .forEach { (k, v) ->
                v.init = false
            }

        fun refresh(configName: String, key: String) {
            keys[buildCacheKey(configName, key)]?.also {
                Vog.d("刷新SmartKey缓存：${it.cacheKey}")
                it.init = false
            }
        }

        private fun buildCacheKey(configName: String, key: String): String = "$configName-$key"
    }
}
