package cn.vove7.smartkey.settings

import cn.vove7.smartkey.collections.ObserveableMap
import cn.vove7.smartkey.tool.Vog
import cn.vove7.smartkey.tool.fromJson
import cn.vove7.smartkey.tool.toJson
import java.io.File

/**
 * # JsonSettings
 * 进程不安全
 * @author Vove
 * 2019/7/25
 */
class JsonSettings(configName: String) : BaseSyncFileSetting() {

    override val configFile: File by lazy { File(configPath, fileName) }

    lateinit var map: ObserveableMap<String, Any>

    companion object {
        var CONFIG_DIR: String? = null
    }

    private val configPath = if (CONFIG_DIR == null) {
        "config"
    } else {
        "$CONFIG_DIR/config"
    }

    private val fileName = "$configName.json"

    init {
        File(configPath).apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    override fun onReloadConfig(cf: File) {
        val jmap = cf.let {
            Vog.d("配置路径：${it.absolutePath}")
            if (it.exists()) {
                it.readText().fromJson<MutableMap<String, Any>>() ?: mutableMapOf()
            } else {
                mutableMapOf()
            }
        }
        map = ObserveableMap(jmap, ::syncToFile)
        Vog.d("onReloadConfig $map")
    }

    override fun doSyncToFile() {
        configFile.writeText(map.toJson(true))
    }

    override fun doClear() {
        map.clear()
    }

    override fun remove(key: String) {
        map.remove(key)
    }

    override fun hasKey_(key: String): Boolean = key in map

    override fun keys_(): Set<String> = map.keys

    override fun putInt_(key: String, value: Int) {
        map[key] = value
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return ((map[key] as? Number)?.toInt()) ?: defaultValue
    }

    override fun putLong_(key: String, value: Long) {
        map[key] = value
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        return ((map[key] as? Number)?.toLong()) ?: defaultValue
    }

    override fun putString_(key: String, value: String) {
        map[key] = value
    }

    override fun getString(key: String, defaultValue: String): String {
        return map[key]?.toString() ?: defaultValue
    }

    override fun putFloat_(key: String, value: Float) {
        map[key] = value
    }

    override fun getFloat(key: String, defaultValue: Float): Float {
        return ((map[key] as? Number)?.toFloat()) ?: defaultValue
    }

    override fun putDouble_(key: String, value: Double) {
        map[key] = value
    }

    override fun getDouble(key: String, defaultValue: Double): Double {
        return ((map[key] as? Number)?.toDouble()) ?: defaultValue
    }

    override fun putBoolean_(key: String, value: Boolean) {
        map[key] = value
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return map[key]?.toString()?.toBoolean() ?: defaultValue
    }
}
