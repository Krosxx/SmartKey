import cn.vove7.smartkey.BaseConfig
import cn.vove7.smartkey.annotation.Config
import cn.vove7.smartkey.key.smartKey

/**
 * # ClearTest
 *
 * Created on 2020/6/18
 * @author Vove
 */

fun main() {
    println(SmartKeyConfig.s)
    SmartKeyConfig.s = "b"
    SmartKeyConfig.clear()
    assert(SmartKeyConfig.s == "a")
}

@Config
object SmartKeyConfig : BaseConfig {
    var s by smartKey("a")
}
