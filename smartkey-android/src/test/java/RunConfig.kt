import cn.vove7.smartkey.BaseConfig
import cn.vove7.smartkey.smartKey

/**
 * # RunConfig
 * Created by 11324.
 * Date: 2019/6/18
 */
object RunConfig : BaseConfig {


    var nullableString: String? by smartKey(null)

    var string: String? by smartKey("hello")


    var number: Int by smartKey(1)

    var nullableNumber: Int? by smartKey(null)

    var intArray: IntArray by smartKey(intArrayOf(1, 2))

    var model: Model? by smartKey(null)
}

class Model {
    var number = 1
    var arr = arrayOf(1, 2, 3)
}