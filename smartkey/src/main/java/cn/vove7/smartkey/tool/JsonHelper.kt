package cn.vove7.smartkey.tool

import com.alibaba.fastjson.JSON

/**
 * # GsonHelper
 * 使用[Expose] 注解属性，是否需要被序列化
 * @author Administrator
 * 2018/9/19
 */
object JsonHelper {

    /**
     * 对象转json
     * @param model Any?
     * @param pretty Boolean
     * @return String
     */
    fun toJson(model: Any?, pretty: Boolean = false): String {
        if (model == null) return ""
        return JSON.toJSONString(model, pretty)
    }


    /**
     * Json实例化
     * @param s String?
     * @return T?
     */
    @Throws
    inline fun <reified T> fromJson(s: String?): T? {
        val type = T::class.java
        return JSON.parseObject<T>(s, type)
    }

    fun <T> fromJson(s: String?, cls: Class<*>): T? {
        return JSON.parseObject<T>(s, cls)
    }

}

/**
 * 扩展函数
 * @receiver Any
 * @return String
 */
fun Any?.toJson(pretty: Boolean = false): String {
    return JsonHelper.toJson(this)
}

/**
 * json字符串 转 T
 * @receiver String
 * @return T?
 */
inline fun <reified T> String.fromJson(): T? {
    return JsonHelper.fromJson<T>(this)
}


