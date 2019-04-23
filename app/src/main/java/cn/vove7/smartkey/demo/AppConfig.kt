package cn.vove7.smartkey.demo

import android.annotation.SuppressLint
import cn.vove7.smartkey.Config
import cn.vove7.smartkey.R
import cn.vove7.smartkey.SmartKey

/**
 * # AppConfig
 *
 * @author 11324
 * 2019/4/21
 */
@SuppressLint("StaticFieldLeak")
@Config("app")
object AppConfig {

    /**
     * 基本类型存储
     */
    var text: String by SmartKey("a", keyId = R.string.key_text)

    var number: Int  by SmartKey(50)

    /**
     * 其他类型使用：[SmartKey.auto]
     */
    var intArr: Array<Int> by SmartKey.auto(emptyArray())
    var userInfo: UserInfo? by SmartKey.auto(null, encrypt = true)

    /**
     * 可空基础类型使用：SmartKey.auto
     */
    var nullableInt: Int? by SmartKey.auto(null)
}

data class UserInfo(
        val name: String
)

