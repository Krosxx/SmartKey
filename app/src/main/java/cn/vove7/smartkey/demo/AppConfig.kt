package cn.vove7.smartkey.demo

import cn.vove7.smartkey.BaseConfig
import cn.vove7.smartkey.R
import cn.vove7.smartkey.android.smartKey
import cn.vove7.smartkey.annotation.Config

/**
 * # AppConfig
 *
 * @author 11324
 * 2019/4/21
 */
@Config("app")
object AppConfig : BaseConfig {

    /**
     * 基本类型存储
     */
    var text: String by smartKey("a", keyId = R.string.key_text)

    var number: Int  by smartKey(50)

    var intArr: Array<Int> by smartKey(emptyArray())
    var userInfo: UserInfo? by smartKey(null, encrypt = true)

    /**
     * 实体类
     */
    var nullableInt: Int? by smartKey(null)
}

data class UserInfo(
        val name: String
)

