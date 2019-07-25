package cn.vove7.smartkey.android

import cn.vove7.smartkey.BaseConfig
import cn.vove7.smartkey.key.SmartKey
import cn.vove7.smartkey.key.set

/**
 * BaseConfig的扩展
 */


/**
 * 设置值
 * @receiver BaseConfig
 * @param keyId Int
 * @param value Any?
 * @param encrypt Boolean
 */
fun BaseConfig.set(keyId: Int, value: Any?, encrypt: Boolean = false) {
    settings.set(AndroidSettings.s(keyId), value, encrypt)
}