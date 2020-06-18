package cn.vove7.smartkey.annotation

import cn.vove7.smartkey.key.KeyConfig
import com.russhwolf.settings.Settings
import kotlin.reflect.KClass

/**
 * # Config
 *
 * @author 11324
 * 2019/4/23
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Config(
    val name: String = "",
    val implCls: KClass<out Settings> = Settings::class
)

/**
 * 反射获取类注解@Config(name)
 */
fun parseConfigAnnotation(thisRef: Any): KeyConfig {
    return thisRef::class.java.getAnnotation(Config::class.java).let {
        KeyConfig(it, thisRef)
    }
}