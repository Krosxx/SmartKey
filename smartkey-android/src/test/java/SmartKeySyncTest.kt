import cn.vove7.smartkey.BaseConfig
import cn.vove7.smartkey.annotation.Config
import cn.vove7.smartkey.key.smartKey

/**
 * # SmartKeySyncTest
 *
 * Created on 2020/6/18
 * @author Vove
 */


fun main() {
    println(SmartKeySyncConfig.s)

    SmartKeySyncConfig["kkk"] = "1234"
    assert(SmartKeySyncConfig.s == "1234")

    println(SmartKeySyncConfig.s)
    SmartKeySyncConfig["kkk"] = "a"
    assert(SmartKeySyncConfig.s == "a")
}

@Config
object SmartKeySyncConfig : BaseConfig {
    var s by smartKey("a", key = "kkk")
}