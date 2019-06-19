import cn.vove7.smartkey.BaseConfig
import cn.vove7.smartkey.key.noCacheKey

/**
 * # NoCacheConfig
 *
 * @author Vove
 * 2019/6/19
 */
object NoCacheConfig : BaseConfig {


    var nullableString: String? by noCacheKey(null)

    var string: String? by noCacheKey("hello")


    var number: Int by noCacheKey(1)

    var nullableNumber: Int? by noCacheKey(null)

    var intArray: IntArray by noCacheKey(intArrayOf(10, 20))

    var model: Model? by noCacheKey(null)
}

