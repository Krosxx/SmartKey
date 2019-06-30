package cn.vove7.smartkey

import cn.vove7.smartkey.annotation.Config
import cn.vove7.smartkey.key.SmartKey
import cn.vove7.smartkey.key.get
import cn.vove7.smartkey.key.set
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

    val configName
        get() = this::class.java.kotlin
                .findAnnotation<Config>()?.let {
                    if (it.name.isEmpty()) SmartKey.defaultConfigName
                    else it.name
                } ?: SmartKey.defaultConfigName

    /**
     * 清空所有key
     */
    fun clear() {
        SmartKey.getSettings(configName).clear()
    }

    /**
     * 设置值
     * @param key String
     * @param value Any?
     * @param encrypt Boolean
     */
    fun set(key: String, value: Any?, encrypt: Boolean = false) {
        SmartKey.getSettings(configName).set(key, value, encrypt)
    }

}