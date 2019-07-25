package cn.vove7.smartkey.key

import cn.vove7.smartkey.tool.Vog
import kotlin.reflect.KProperty


inline fun <reified T> noCacheKey(
        defaultValue: T,
        key: String? = null,
        encrypt: Boolean = false
): NoCacheKey<T> {
    return NoCacheKey(defaultValue, T::class.java, encrypt, key)
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
        cls: Class<*>,

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