package cn.vove7.smartkey.tool

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.annotations.Expose
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonToken.*
import com.google.gson.stream.JsonWriter
import java.lang.reflect.Type


/**
 * # GsonHelper
 * 使用[Expose] 注解属性，是否需要被序列化
 * @author Administrator
 * 2018/9/19
 */
object GsonHelper {

    val builder
        get() = GsonBuilder().apply {
            serializeSpecialFloatingPointValues()
            setDateFormat("yyyy-MM-dd HH:mm:ss")
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
        val type = getType<T>()
        return builder
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create().fromJson<T>(s, type)
    }

    fun <T> fromJson(s: String?, cls: Class<*>): T? {
        return builder
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create().fromJson<T>(s, cls)
    }

    inline fun <reified T> getType(): Type {
        return object : TypeToken<T>() {}.type
    }

}

/**
 * 扩展函数
 * @receiver Any
 * @return String
 */
fun Any?.toJson(pretty: Boolean = false): String {
    return GsonHelper.toJson(this)
}

/**
 * json字符串 转 T
 * @receiver String
 * @return T?
 */
inline fun <reified T> String.fromJson(): T? {
    return GsonHelper.fromJson<T>(this)
}

class GsonTypeAdapter : TypeAdapter<Any>() {

    override fun read(`in`: JsonReader): Any? {
        // 反序列化
        val token = `in`.peek()
        when (token) {
            BEGIN_ARRAY -> {
                val list = ArrayList<Any?>()
                `in`.beginArray()
                while (`in`.hasNext()) {
                    list.add(read(`in`))
                }
                `in`.endArray()
                return list
            }

            BEGIN_OBJECT -> {
                val map = HashMap<String, Any>()
                `in`.beginObject()
                while (`in`.hasNext()) {
                    map[`in`.nextName()] = read(`in`)!!
                }
                `in`.endObject()
                return map
            }

            STRING ->
                return `in`.nextString()

            NUMBER -> {
                /**
                 * 改写数字的处理逻辑，将数字值分为整型与浮点型。
                 */
                val dbNum = `in`.nextDouble()

                // 数字超过long的最大值，返回浮点类型
                if (dbNum > java.lang.Long.MAX_VALUE) {
                    return dbNum
                }

                // 判断数字是否为整数值
                val lngNum = dbNum.toLong()
                return if (dbNum == lngNum.toDouble()) {
                    lngNum
                } else {
                    dbNum
                }
            }

            BOOLEAN -> return `in`.nextBoolean()

            NULL -> {
                `in`.nextNull()
                return null
            }

            else -> throw IllegalStateException()
        }
    }

    override fun write(out: JsonWriter, value: Any) {
        // 序列化不处理
    }
}

