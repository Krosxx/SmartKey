package cn.vove7.smartkey.settings

import cn.vove7.smartkey.tool.Vog
import cn.vove7.smartkey.tool.fromJson
import cn.vove7.smartkey.tool.toJson
import com.russhwolf.settings.Settings
import java.io.File

/**
 * # JsonSettings
 * 进程不安全
 * @author Vove
 * 2019/7/25
 */
class JsonSettings(private val configName: String) : Settings {

    val map: ObserveableMap<String, Any>

    companion object {
        var CONFIG_DIR: String? = null
    }

    val configPath = if (CONFIG_DIR == null) {
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
        val jmap = File(configPath, fileName).let {
            Vog.d("配置路径：${it.absolutePath}")
            if (it.exists()) {
                it.readText().fromJson<MutableMap<String, Any>>()
            } else {
                mutableMapOf<String, Any>()
            }
        }
        map = ObserveableMap(jmap)
        map.lis = {
            sync()
        }
    }

    private fun sync() {
        File(configPath, fileName).writeText(map.toJson())
    }

    override fun clear() {
        map.clear()
    }

    override fun remove(key: String) {
        map -= key
    }

    override fun hasKey(key: String): Boolean = key in map

    override fun putInt(key: String, value: Int) {
        map[key] = value
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return map[key]?.toString()?.toIntOrNull() ?: defaultValue
    }

    override fun putLong(key: String, value: Long) {
        map[key] = value
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        return map[key]?.toString()?.toLongOrNull() ?: defaultValue
    }

    override fun putString(key: String, value: String) {
        map[key] = value
    }

    override fun getString(key: String, defaultValue: String): String {
        return map[key]?.toString() ?: defaultValue
    }

    override fun putFloat(key: String, value: Float) {
        map[key] = value
    }

    override fun getFloat(key: String, defaultValue: Float): Float {
        return map[key]?.toString()?.toFloatOrNull() ?: defaultValue
    }

    override fun putDouble(key: String, value: Double) {
        map[key] = value
    }

    override fun getDouble(key: String, defaultValue: Double): Double {
        return map[key]?.toString()?.toDouble() ?: defaultValue
    }

    override fun putBoolean(key: String, value: Boolean) {
        map[key] = value
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return map[key]?.toString()?.toBoolean() ?: defaultValue
    }


}


/**
 * # ObserveableMap
 *
 *
 * @author Vove
 * 2019/7/25
 */
class ObserveableMap<K, T> : HashMap<K, T> {

    var lis: (() -> Unit)? = null

    constructor(p0: MutableMap<out K, out T>?) : super(p0)
    constructor() : super()

    override fun remove(key: K): T? {
        return super.remove(key).also { lis?.invoke() }
    }

    override fun put(key: K, value: T): T? {
        return super.put(key, value).also { lis?.invoke() }
    }

    override fun clear() {
        super.clear()
        lis?.invoke()
    }
}