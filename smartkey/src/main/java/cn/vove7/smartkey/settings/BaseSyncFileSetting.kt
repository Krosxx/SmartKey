package cn.vove7.smartkey.settings

import cn.vove7.smartkey.tool.Vog
import com.russhwolf.settings.Settings
import java.io.File

/**
 * # BaseSyncFileSetting
 * 同步文件 （内容修改时，同步配置）
 * 需要 by nocacheKey
 * @author Vove
 * 2019/9/18
 */
abstract class BaseSyncFileSetting : Settings {
    //使用 get
    abstract val configFile: File

    abstract fun onReloadConfig(cf: File)

    var lastSync = -1L

    abstract fun hasKey_(key: String): Boolean
    abstract fun putInt_(key: String, value: Int)
    abstract fun putLong_(key: String, value: Long)
    abstract fun putString_(key: String, value: String)
    abstract fun putFloat_(key: String, value: Float)
    abstract fun putDouble_(key: String, value: Double)
    abstract fun putBoolean_(key: String, value: Boolean)

    final override fun putInt(key: String, value: Int) {
        syncConfig()
        putInt_(key, value)
    }

    final override fun putLong(key: String, value: Long) {
        syncConfig()
        putLong_(key, value)
    }

    final override fun putString(key: String, value: String) {
        syncConfig()
        putString_(key, value)
    }

    final override fun putFloat(key: String, value: Float) {
        syncConfig()
        putFloat_(key, value)
    }

    final override fun putDouble(key: String, value: Double) {
        syncConfig()
        putDouble_(key, value)
    }

    final override fun putBoolean(key: String, value: Boolean) {
        syncConfig()
        putBoolean_(key, value)
    }

    //get 时首先会判断has  自会同步配置文件
    final override fun hasKey(key: String): Boolean {
        syncConfig()
        return hasKey_(key)
    }

    private fun syncConfig() {
        val cf = configFile
        val fl = cf.lastModified()
        if (fl > lastSync) {
            Vog.d("lastSync: $lastSync sync: $fl")
            onReloadConfig(cf)
            lastSync = fl
        }
    }

}