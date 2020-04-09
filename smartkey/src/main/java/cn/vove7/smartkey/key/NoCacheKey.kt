package cn.vove7.smartkey.key

import cn.vove7.smartkey.tool.Vog
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import kotlin.reflect.KProperty


inline fun <reified T> noCacheKey(
        defaultValue: T,
        key: String? = null,
        encrypt: Boolean = false
): NoCacheKey<T> {
    val type = T::class.java
    if (List::class.java.isAssignableFrom(type)) {//数组类型
        throw IllegalArgumentException("`by noCacheKey` don't support list type, please use `by noCacheKeyList<ModelType>()")
    }
    if (Map::class.java.isAssignableFrom(type)) {
        throw IllegalArgumentException("`by noCacheKey` don't support map type, please use `by noCacheKeySet<ModelType>()")
    }
    if (Set::class.java.isAssignableFrom(type)) {
        throw IllegalArgumentException("`by noCacheKey` don't support set type, please use `by noCacheKeyMap<K, T>()")
    }
    return NoCacheKey(defaultValue, T::class.java, encrypt, key)
}

inline fun <reified M> noCacheKeyList(
        defaultValue: List<M> = listOf(),
        key: String? = null,
        encrypt: Boolean = false
): NoCacheKey<List<M>> {
    return noCacheKeyCollectionTyped(defaultValue, key, encrypt)
}

inline fun <reified M> noCacheKeySet(
        defaultValue: Set<M> = setOf(),
        key: String? = null,
        encrypt: Boolean = false
): NoCacheKey<Set<M>> {
    return noCacheKeyCollectionTyped(defaultValue, key, encrypt)
}


inline fun <reified K, reified T> noCacheKeyMap(
        defaultValue: Map<K, T> = mapOf(),
        key: String? = null,
        encrypt: Boolean = false
): NoCacheKey<Map<K, T>> {
    val type = TypeToken.getParameterized(Map::class.java, K::class.java, T::class.java).type
    return NoCacheKey(defaultValue, type, encrypt, key)
}

inline fun <reified CollectType : Collection<ItemType>, reified ItemType> noCacheKeyCollectionTyped(
        defaultValue: CollectType,
        key: String? = null,
        encrypt: Boolean = false): NoCacheKey<CollectType> {
    val type = TypeToken.getParameterized(CollectType::class.java, ItemType::class.java).type
    return NoCacheKey(defaultValue, type, encrypt, key)
}

/**
 * # NoCacheKey
 * 无缓存，可用于多进程
 * @author Vove
 * 2019/6/19
 */
open class NoCacheKey<T>(
        private val defaultValue: T,

        /**
         * 指定泛型class
         */
        cls: Type,

        /**
         * 加密存储数据
         */
        encrypt: Boolean = false,
        /**
         * 自定义key
         */
        key: String? = null
) : IKey(cls, encrypt, key) {

    operator fun getValue(thisRef: Any?, p: KProperty<*>): T {
        initConfig(thisRef)
        val k = key ?: p.name
        val value = settings.get(k, defaultValue, cls, encrypt)
        return value ?: defaultValue
    }


    operator fun setValue(thisRef: Any?, property: KProperty<*>, t: T) {
        initConfig(thisRef)
        val k = key ?: property.name
        Vog.d("设置值：$k = $t")
        settings.set(k, t, encrypt)
    }

}