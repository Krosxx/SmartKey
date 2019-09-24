package cn.vove7.smartkey

import cn.vove7.smartkey.annotation.Config
import cn.vove7.smartkey.key.SmartKey
import cn.vove7.smartkey.key.get
import cn.vove7.smartkey.key.set
import com.russhwolf.settings.Settings
import kotlin.reflect.full.findAnnotation

/**
 * # BaseConfig
 *
 * 为配置类提供基础操作
 *
 * @author Vove
 * 2019/6/18
 */
interface BaseConfig {
    //settings 可放与此
    //继承实现

    val config: Config get() = this::class.findAnnotation() ?: throw Exception("请在类${this::class.simpleName}使用注解@Config")

    /**
     * 清空所有key
     */
    fun clear() {
        settings.clear()
    }

    /**
     * 设置值
     * @param key String
     * @param value Any?
     * @param encrypt Boolean
     */
    fun set(key: String, value: Any?, encrypt: Boolean = false) {
        settings.set(key, value, encrypt)
    }

    /**
     * Settings实例
     * 使用缓存
     */
    val settings: Settings get() = SmartKey.getSettingsFromCache(config, this)
}

/**
 * 支持 set get
 * Config["..."] = ...
 * val a = Config["...", default]
 */
abstract class AConfig : BaseConfig {

    /**
     * 设置值
     * ```kotlin
     * //普通存储
     * AppConfig["key"] = 1
     * //加密储存
     * AppConfig["key", true] = 1
     * ```
     */
    operator fun set(
            key: String,
            encrypt: Boolean = false,
            value: Any?
    ) {
        set(key, value, encrypt)
    }

    /**
     * ```
     * //根据默认值类型判断返回值类型
     * val s = Config["key", "abc"] //  s: String
     *
     * //获取可空数据
     * 1. val user = AppConfig.get<UserInfo?>("userInfo", null)
     * 2. val user = AppConfig["userInfo", null as UserInfo?]
     *
     * //获得加密内容
     * val s = Config["key", "..", true]
     * ```
     * @param key String
     * @param defaultValue T
     * @return T
     */
    inline operator fun <reified T> get(
            key: String,
            defaultValue: T? = null,
            encrypt: Boolean = false
    ): T? {
        return settings.get(key, defaultValue, T::class.java, encrypt)
            ?: defaultValue
    }

}