package cn.vove7.smartkey

import cn.vove7.smartkey.annotation.parseConfigAnnotation
import cn.vove7.smartkey.key.IKey
import cn.vove7.smartkey.key.SmartKey
import cn.vove7.smartkey.key.get
import cn.vove7.smartkey.key.set
import com.russhwolf.settings.Settings
import com.russhwolf.settings.contains

/**
 * # BaseConfig
 *
 * 为配置类提供基础操作
 *
 * @author Vove
 * 2019/6/18
 */
interface BaseConfig {
    //配置名
    val configName get() = this::class.java.simpleName!!.toLowerCase()
    //存储实现类
    val implCls get() = IKey.DEFAULT_SETTING_IMPL_CLS

    //settings 可放与此
    //继承实现

    val config get() = parseConfigAnnotation(this)

    /**
     * 清空所有key
     */
    fun clear() {
        settings.clear()
        SmartKey.clearCache(this.config)
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

    operator fun contains(key: String): Boolean = key in settings

    operator fun minusAssign(key: String): Unit = settings.remove(key)

    /**
     * Settings实例
     * 使用缓存
     */
    val settings: Settings get() = IKey.getSettingsFromCache(config)


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
        SmartKey.refresh(config.name, key)
    }

}
/**
 * 支持 set get
 * Config["..."] = ...
 * val a = Config["...", default]
 */

/**
 * ```
 * //根据默认值类型判断返回值类型
 * val s = Config["key", "abc"] //  s: String
 *
 * //获取可空数据
 * 1. val user2 :UserInfo?= AppConfig["userInfo"]
 * 2. val user = AppConfig.get<UserInfo?>("userInfo")
 * 3. val user = AppConfig["userInfo", null as UserInfo?]
 *
 * //获得加密内容
 * val s = Config["key", "..", true]
 * ```
 * @param key String
 * @param defaultValue T
 * @return T
 */
inline operator fun <reified T> BaseConfig.get(
    key: String,
    defaultValue: T,
    encrypt: Boolean = false
): T {
    return settings.get(key, defaultValue, T::class.java, encrypt)
        ?: defaultValue
}

inline operator fun <reified T> BaseConfig.get(
    key: String,
    encrypt: Boolean = false
): T? {
    return settings.get(key, null, T::class.java, encrypt)
}

//适配之前版本
abstract class AConfig : BaseConfig