import cn.vove7.smartkey.key.IKey
import cn.vove7.smartkey.settings.PropertiesSettings
import cn.vove7.smartkey.tool.Vog
import java.util.*

/**
 * # TestNoCacheKey
 *
 * @author Vove
 * 2019/6/19
 */


fun main() {
    IKey.DEFAULT_SETTING_IMPL_CLS = PropertiesSettings::class

    Vog.init(100)

    val b = System.currentTimeMillis()

    val start = Runtime.getRuntime().freeMemory()

    println(Arrays.toString(NoCacheConfig.intArray))
    val start2 = Runtime.getRuntime().freeMemory()

    println("mem: ${(start - start2) shr 10}")

    println(NoCacheConfig.number)
    NoCacheConfig.number = 2
    println(NoCacheConfig.string)
    NoCacheConfig.string = "aaa"
    println(NoCacheConfig.nullableNumber)

    NoCacheConfig.nullableNumber = 5

    print(NoCacheConfig.nullableString)

    NoCacheConfig.nullableString = "123"

    println(NoCacheConfig.model)
    NoCacheConfig.model = Model()
    println(NoCacheConfig.model)

    NoCacheConfig.intArray = intArrayOf(2, 3, 4, 9)

    val e = System.currentTimeMillis()
    println(e - b)

}