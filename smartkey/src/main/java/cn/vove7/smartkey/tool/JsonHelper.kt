package cn.vove7.smartkey.tool

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose

/**
 * # JsonHelper
 * 使用[Expose] 注解属性，是否需要被序列化
 * @author Administrator
 * 2018/9/19
 */
internal object JsonHelper {

    private val builder
        get() = GsonBuilder().apply {
            serializeSpecialFloatingPointValues()
            addSerializationExclusionStrategy(object : ExclusionStrategy {

                override fun shouldSkipField(fieldAttributes: FieldAttributes): Boolean {
                    val expose = fieldAttributes.getAnnotation(Expose::class.java)
                    return if (expose == null) false
                    else !expose.serialize
                }

                override fun shouldSkipClass(aClass: Class<*>): Boolean = false
            }).addDeserializationExclusionStrategy(object : ExclusionStrategy {
                override fun shouldSkipField(fieldAttributes: FieldAttributes): Boolean {
                    val expose = fieldAttributes.getAnnotation(Expose::class.java)
                    return if (expose == null) false
                    else !expose.deserialize
                }

                override fun shouldSkipClass(aClass: Class<*>): Boolean = false
            }).disableHtmlEscaping()
        }

    /**
     * 对象转json
     * @param model Any?
     * @param pretty Boolean
     * @return String
     */
    fun toJson(model: Any?, pretty: Boolean = false): String {
        if (model == null) return ""
        val b = builder
        if (pretty) b.setPrettyPrinting()
        return b.create().toJson(model)
    }


    /**
     * Json实例化
     * @param s String?
     * @return T?
     */
    @Throws
    inline fun <reified T> fromJson(s: String?): T? {
        return builder.create().fromJson<T>(s, T::class.java)
    }

    fun <T> fromJson(s: String?, cls: Class<*>): T? {
        return builder.create().fromJson<T>(s, cls)
    }

}

/**
 * 扩展函数
 * @receiver Any
 * @return String
 */
internal fun Any?.toJson(pretty: Boolean = false): String {
    return JsonHelper.toJson(this, pretty)
}

/**
 * json字符串 转 T
 * @receiver String
 * @return T?
 */
internal inline fun <reified T> String.fromJson(): T? {
    return JsonHelper.fromJson<T>(this)
}


