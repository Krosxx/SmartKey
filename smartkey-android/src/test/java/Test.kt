import cn.vove7.smartkey.key.IKey
import cn.vove7.smartkey.settings.PropertiesSettings
import cn.vove7.smartkey.tool.Vog
import java.util.*

fun main() {


    IKey.settingImplCls = PropertiesSettings::class.java
    val b = System.currentTimeMillis()

    Vog.init(100)

    val start = Runtime.getRuntime().freeMemory()
    println(Arrays.toString(RunConfig.intArray))
    val start2 = Runtime.getRuntime().freeMemory()
    println("mem: ${(start - start2) shr 10}")

    println(RunConfig.number)

    RunConfig.number = 2
    println(RunConfig.string)
    RunConfig.string = "aaa"
    println(RunConfig.nullableNumber)
    RunConfig.nullableNumber = 5

    println(RunConfig.nullableString)
    RunConfig.nullableString = "abc"

    println(RunConfig.model)
    RunConfig.model = Model()
    println(RunConfig.model)

    RunConfig.intArray = intArrayOf(2, 3, 4, 9)

    val e = System.currentTimeMillis()
    println(e - b)

}