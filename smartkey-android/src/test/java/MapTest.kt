import cn.vove7.smartkey.annotation.Config
import cn.vove7.smartkey.key.smartKeyMap
import cn.vove7.smartkey.settings.PropertiesSettings

/**
 * # MapTest
 *
 * Created on 2020/5/26
 * @author Vove
 */
fun main() {
    print(MapConfig.mapData.size)

    MapConfig.mapData["s1"] = "abc"
    MapConfig.mapData["num"] = 1
    print(MapConfig.mapData.size)

    MapConfig.mapData.clear()
    assert(MapConfig.mapData.isEmpty())
    MapConfig.mapData["num"] = 3
}

@Config(implCls = PropertiesSettings::class)
object MapConfig {
    var mapData by smartKeyMap<String, Any>()
}
