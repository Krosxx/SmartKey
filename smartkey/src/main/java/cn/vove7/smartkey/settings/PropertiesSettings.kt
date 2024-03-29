package cn.vove7.smartkey.settings

import cn.vove7.smartkey.tool.Vog
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

/**
 * # PropertiesSettings
 * 基于[Properties]持久化
 * @author Vove
 * 2019/6/19
 */
class PropertiesSettings(private val configName: String) : BaseSyncFileSetting() {

    companion object {
        //存储目录
        var baseDir: String? = null
    }

    private val prefix get() = if (baseDir != null) "$baseDir/properties" else "properties"

    override val configFile by lazy {
        File("$prefix/$configName.properties")
    }

    private lateinit var properties: Properties

    init {
        syncConfig()
    }

    override fun onReloadConfig(cf: File) {
        properties = Properties().apply {
            File(prefix).also {
                if (!it.exists()) it.mkdir()
            }
            cf.let {
                if (!it.exists()) {
                    it.createNewFile()
                }
                load(FileInputStream(it))

                Vog.d("配置路径：${it.absolutePath}")
            }
        }
    }

    override fun doClear() {
        properties.clear()
        syncToFile()
    }

    override fun remove(key: String) {
        properties.remove(key)
        syncToFile()
    }

    override fun hasKey_(key: String): Boolean = properties.containsKey(key)

    override fun keys_(): Set<String> = properties.keys.filterIsInstance<String>().toSet()

    override fun putInt_(key: String, value: Int) {
        properties[key] = value.toString()
        syncToFile()
    }

    override fun doSyncToFile() {
        properties.store(FileOutputStream(configFile), null)
    }


    override fun getInt(key: String, defaultValue: Int): Int =
        properties.getProperty(key)?.toIntOrNull()
            ?: defaultValue

    override fun putLong_(key: String, value: Long) {
        properties[key] = value.toString()
        syncToFile()
    }

    override fun getLong(key: String, defaultValue: Long): Long =
        properties.getProperty(key)?.toLongOrNull()
            ?: defaultValue


    override fun putString_(key: String, value: String) {
        properties[key] = value
        syncToFile()
    }

    override fun getString(key: String, defaultValue: String): String = properties.getProperty(key)
        ?: defaultValue

    override fun putFloat_(key: String, value: Float) {
        properties[key] = value.toString()
        syncToFile()
    }

    override fun getFloat(key: String, defaultValue: Float): Float =
        properties.getProperty(key)?.toFloatOrNull()
            ?: defaultValue

    override fun putDouble_(key: String, value: Double) {
        properties[key] = value.toString()
        syncToFile()
    }

    override fun getDouble(key: String, defaultValue: Double): Double =
        properties.getProperty(key)?.toDoubleOrNull()
            ?: defaultValue

    override fun putBoolean_(key: String, value: Boolean) {
        properties[key] = value.toString()
        syncToFile()
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        properties.getProperty(key)?.toBoolean()
            ?: defaultValue
}
