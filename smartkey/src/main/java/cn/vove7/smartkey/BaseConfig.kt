package cn.vove7.smartkey

import cn.vove7.smartkey.annotation.Config
import cn.vove7.smartkey.key.SmartKey
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

    /**
     * 清空所有key
     */
    fun clear() {
        val configName = this::class.java.kotlin
                .findAnnotation<Config>()?.let {
                    if (it.name.isEmpty()) SmartKey.defaultConfigName
                    else it.name
                } ?: SmartKey.defaultConfigName

        SmartKey.getSettings(configName).clear()
    }


}