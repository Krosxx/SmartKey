import cn.vove7.smartkey.AConfig
import cn.vove7.smartkey.annotation.Config
import cn.vove7.smartkey.key.noCacheKey
import cn.vove7.smartkey.key.smartKey
import cn.vove7.smartkey.settings.FileSettings
import org.junit.Test

class Test {

    @Test
    fun main() {
        print(MyConfig.toString())

        MyConfig["string"] = "1234"

        println("----------------")
        print(MyConfig.toString())

        println(CConfig.c)
        CConfig.c = 2
        println(CConfig.c)

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
        appendln("intArray: $intArray")
        appendln("model: $model")
    }
}
