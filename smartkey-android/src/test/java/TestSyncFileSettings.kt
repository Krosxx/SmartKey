import cn.vove7.smartkey.annotation.Config
import cn.vove7.smartkey.key.noCacheKey
import cn.vove7.smartkey.settings.JsonSettings
import cn.vove7.smartkey.settings.PropertiesSettings
import java.lang.Thread.sleep
import kotlin.concurrent.thread

/**
 * # TestSyncFileSettings
 * 测试 修改配置文件，程序实时同步
 * 需要配合 noCacheKey 使用
 * @author Vove
 * 2019/9/18
 */
fun main() {
    JsonFileConfig.a = 1
    val jt = thread {
        var a = JsonFileConfig.a
        while (a != 0) {
            println("jf -> $a")
            sleep(1000)
            a = JsonFileConfig.a
        }
        println("exit json")
    }
    PFileConfig.a = 1
    val pt = thread {
        var a = PFileConfig.a
        while (a != 0) {
            println("pf -> $a")
            sleep(1000)
            a = PFileConfig.a
        }
        println("exit pf")
    }
    jt.join()
    pt.join()
}

@Config("jsonConfig", implCls = JsonSettings::class)
object JsonFileConfig {
    //需要配合 noCacheKey 使用
    var a by noCacheKey(1)
}

@Config("jsonConfig", implCls = PropertiesSettings::class)
object PFileConfig {
    var a by noCacheKey(1)
}