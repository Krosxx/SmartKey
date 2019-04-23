package cn.vove7.smartkey

/**
 * # Config
 *
 * @author 11324
 * 2019/4/23
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Config(
        val name: String = ""
)