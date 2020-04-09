import cn.vove7.smartkey.AConfig
import cn.vove7.smartkey.annotation.Config
import cn.vove7.smartkey.key.*
import cn.vove7.smartkey.settings.FileSettings
import cn.vove7.smartkey.settings.JsonSettings
import cn.vove7.smartkey.tool.Vog
import com.google.gson.JsonObject
import com.google.gson.internal.LinkedTreeMap
import org.junit.Test

class Test {
    init {
        Vog.init(100)//不输出日志
    }

    @Test
    fun testCollection() {

        val strNullable: String? = JsonConfig["dont_exists_key", true]
        val strNotNull: String = JsonConfig["dont_exists_key", "def"]
        println("$strNullable,  $strNotNull")

        val list: List<JsonConfig.ListModel> = JsonConfig.modelList
        println(list)
        if (list.isNotEmpty()) {
            println(list[0].a)
        }

        JsonConfig.modelList = listOf(JsonConfig.ListModel("a", 1), JsonConfig.ListModel("b", 2))
        println(JsonConfig.modelList)

        println(JsonConfig.modelSet)
        JsonConfig.modelSet = setOf(JsonConfig.ListModel("s", 2), JsonConfig.ListModel("ss", 2))
        println(JsonConfig.modelSet)

        println(JsonConfig.map)
        JsonConfig.map = mapOf("a" to 1, "b" to 2)
        println(JsonConfig.map)

        //
        val ml: List<Map<Any, Any>>? = JsonConfig["modelList"]
        println(ml)
        println(ml!![0]::class.java)

        JsonConfig["strList"] = listOf("1", "2")
        val sl: List<String>? = JsonConfig["strList"]
        println(sl)
        println(sl!![0]::class.java)

    }

    @Test
    fun main() {
        print(MyConfig.toString())
        MyConfig["string"] = "1234"

        println("----------------")
        print(MyConfig.toString())

        println(CConfig.c)
        CConfig.c = 2
        println(CConfig.c)
        println(JsonConfig.number)
        JsonConfig.number += 123
        println(JsonConfig.number)

    }
}

@Config("config_3", implCls = JsonSettings::class)
object JsonConfig : AConfig() {

    var nullableString: String? by smartKey(null)

    var string: String? by smartKey("hello")


    var number: Int by smartKey(1)

    var nullableNumber: Int? by smartKey(null)

    var intArray: IntArray by smartKey(intArrayOf(1, 2), encrypt = true)

    var model: Model? by smartKey(null, encrypt = true)

    var strList by smartKeyList<String>(emptyList())

    var modelList by smartKeyList<ListModel>(emptyList())

    var modelSet by smartKeySet<ListModel>(emptySet())

    var map by smartKeyMap<String, Int>()

    data class ListModel(val s: String, val a: Int)


    override fun toString(): String = buildString {

        appendln("nullableString: $nullableString")
        appendln("string: $string")
        appendln("number: $number")
        appendln("nullableNumber: $nullableNumber")
        appendln("intArray: ${intArray.contentToString()}")
        appendln("model: $model")
    }
}

@Config("config_2")
object CConfig : AConfig() {
    var c by noCacheKey(1)
}

@Config("config_1", FileSettings::class)
object MyConfig : AConfig() {
    var nullableString: String? by smartKey(null)

    var string: String? by smartKey("hello")


    var number: Int by smartKey(1)

    var nullableNumber: Int? by smartKey(null)

    var intArray: IntArray by smartKey(intArrayOf(1, 2), encrypt = true)

    var model: Model? by smartKey(null, encrypt = true)

    override fun toString(): String = buildString {

        appendln("nullableString: $nullableString")
        appendln("string: $string")
        appendln("number: $number")
        appendln("nullableNumber: $nullableNumber")
        appendln("intArray: ${intArray.contentToString()}")
        appendln("model: $model")
    }
}
