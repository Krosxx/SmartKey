package cn.vove7.smartkey.annotation

import com.russhwolf.settings.Settings
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

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
fun parseConfigAnnotation(thisRef: Any): Config {
    val it = thisRef::class.findAnnotation<Config>()
    if (it != null) {
        return it
    }
    throw Exception("please annotate with @Config on class ${thisRef::class.simpleName}")
}