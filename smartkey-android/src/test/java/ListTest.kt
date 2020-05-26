import cn.vove7.smartkey.annotation.Config
import cn.vove7.smartkey.key.smartKeyList
import cn.vove7.smartkey.key.smartKeySet

/**
 * # ListTest
 *
 * Created on 2020/5/26
 * @author Vove
 */
fun main() {
    println(ListConfig.setData.size)

    ListConfig.setData.add("aaa")
    ListConfig.setData.add("bbb")
    ListConfig.setData.add("aaa")
    println(ListConfig.setData.size)

//    ListConfig.listData.remove("aaa")
    ListConfig.setData.removeIf {
        it == "aaa"
    }
    println(ListConfig.setData.size)
    ListConfig.setData.clear()

    ListConfig.setData.add("ccc")

    println(ListConfig.setData.size)

}

@Config
object ListConfig {
    var listData by smartKeyList<String>()
    var setData by smartKeySet<String>()
}
