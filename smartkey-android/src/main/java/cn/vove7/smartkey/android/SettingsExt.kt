package cn.vove7.smartkey.android

import cn.vove7.smartkey.key.get
import cn.vove7.smartkey.key.set
import com.russhwolf.settings.Settings

/**
 * # SettingsExt
 * 扩展keyId
 * @author Vove
 * 2019/7/1
 */


@Suppress("UNCHECKED_CAST")
fun <T> Settings.get(keyId: Int, defaultValue: T?, cls: Class<*>, encrypt: Boolean = false): T? {
    return get(AndroidSettings.s(keyId), defaultValue, cls, encrypt)
}

fun <T> Settings.set(keyId: Int, value: T?, encrypt: Boolean = false) {
    set(AndroidSettings.s(keyId), value, encrypt)
}
