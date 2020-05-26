package cn.vove7.smartkey.android

import androidx.annotation.StringRes
import cn.vove7.smartkey.AConfig
import cn.vove7.smartkey.BaseConfig
import cn.vove7.smartkey.key.set
import com.russhwolf.settings.contains

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
fun BaseConfig.set(
    @StringRes keyId: Int,
    value: Any?,
    encrypt: Boolean = false
) {
    settings.set(AndroidSettings.s(keyId), value, encrypt)
}

operator fun BaseConfig.contains(
    @StringRes keyId: Int
): Boolean = AndroidSettings.s(keyId) in settings


inline operator fun <reified T> AConfig.get(
    @StringRes keyId: Int,
    defaultValue: T,
    encrypt: Boolean = false
): T {
    return get(AndroidSettings.s(keyId), defaultValue, encrypt)
}

inline operator fun <reified T> AConfig.get(
    @StringRes keyId: Int,
    encrypt: Boolean = false
): T? {
    return get(AndroidSettings.s(keyId), encrypt)
}