package cn.vove7.smartkey.settings

import com.russhwolf.settings.Settings
import java.io.File


/**
 * # FileSettings
 * 文件存储
 * @author Vove
 * 2019/6/18
 */
class FileSettings(val configName: String) : Settings {

    companion object {
        //存储目录
        var baseDir: String? = null
    }

    private val configDir by lazy {
        if (baseDir != null) "$baseDir/$configName"
        //默认当前目录
        else configName
    }

    override fun keys(): Set<String> {
        return File(configDir).listFiles()?.filter {
            it.isFile
        }?.map { it.name }?.toSet() ?: emptySet()
    }

    init {
        File(configName).apply {
            if (!exists()) mkdir()
        }
    }

    override fun clear() {
        File(configDir).listFiles()?.forEach { it.delete() }
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return File(configDir, key).getValue {
            it.toBoolean()
        } ?: defaultValue
    }

    override fun getDouble(key: String, defaultValue: Double): Double {
        return File(configDir, key).getValue {
            it.toDoubleOrNull()
        } ?: defaultValue
    }

    override fun getFloat(key: String, defaultValue: Float): Float {
        return File(configDir, key).getValue {
            it.toFloatOrNull()
        } ?: defaultValue
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return File(configDir, key).getValue {
            it.toIntOrNull()
        } ?: defaultValue
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        return File(configDir, key).getValue {
            it.toLong()
        } ?: defaultValue
    }

    override fun getString(key: String, defaultValue: String): String {
        return File(configDir, key).getValue { it }
            ?: defaultValue
    }


    override fun hasKey(key: String): Boolean =
        File(configDir, key).exists()


    override fun putBoolean(key: String, value: Boolean) {
        File(configDir, key).apply {
            if (!exists()) createNewFile()
            writeText(value.toString())
        }
    }

    override fun syncImmediately(): Boolean {
        return true
    }

    override fun putDouble(key: String, value: Double) {
        File(configDir, key).apply {
            if (!exists()) createNewFile()
            writeText(value.toString())
        }
    }

    override fun putFloat(key: String, value: Float) {
        File(configDir, key).apply {
            if (!exists()) createNewFile()
            writeText(value.toString())
        }
    }

    override fun putInt(key: String, value: Int) {
        File(configDir, key).apply {
            if (!exists()) createNewFile()
            writeText(value.toString())
        }
    }

    override fun putLong(key: String, value: Long) {
        File(configDir, key).apply {
            if (!exists()) createNewFile()
            writeText(value.toString())
        }
    }

    @Synchronized
    override fun putString(key: String, value: String) {
        File(configDir, key).apply {
            try {
                if (!exists()) createNewFile()
            } catch (e: Exception) {
                print(e.message + absolutePath)
            }
            writeText(value)
        }
    }

    override fun remove(key: String) {
        File(configDir, key).delete()
    }
}

fun <T> File.getValue(t: (String) -> T?): T? {
    return if (exists()) {
        t(readText())
    } else null
}