package cn.vove7.smartkey.demo

import cn.vove7.smartkey.AConfig
import cn.vove7.smartkey.R
import cn.vove7.smartkey.android.AndroidSettings
import cn.vove7.smartkey.android.noCacheKey
import cn.vove7.smartkey.android.smartKey
import cn.vove7.smartkey.annotation.Config
import cn.vove7.smartkey.key.smartKeyList

/**
 * # AppConfig
 *
 * @author 11324
 * 2019/4/21
 */
@Config(implCls = AndroidSettings::class)
object AppConfig : AConfig() {

    //基本类型存储
    var text: String by smartKey("a", keyId = R.string.key_text)

    //可空基础类型
    var nullableInt: Int? by smartKey(null)
    var number: Int by smartKey(50)

    //数组
    var intArr: Array<Int> by smartKey(emptyArray())

    //实体类
    var userInfo: UserInfo? by smartKey(null, encrypt = true)

    //实体数组
    var modelList: MutableList<ListModel> by smartKeyList()
}

data class UserInfo(
        val name: String
)

class ListModel(val s: String, val a: Int)
