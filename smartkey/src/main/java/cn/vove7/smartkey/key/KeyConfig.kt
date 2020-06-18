package cn.vove7.smartkey.key

import cn.vove7.smartkey.BaseConfig
import cn.vove7.smartkey.annotation.Config

/**
 * # KeyConfig
 *
 * Created on 2020/6/18
 * @author Vove
 */
class KeyConfig(config: Config?, thisRef: Any) {

    val name: String = {
        val default = if (thisRef is BaseConfig) thisRef.configName
        else thisRef::class.java.simpleName!!.toLowerCase()

        config?.name?.let {
            if (it.isEmpty()) default ?: IKey.DEFAULT_CONFIG_NAME else it
        } ?: default
    }()

    val implCls = {
        val defalut = if (thisRef is BaseConfig) thisRef.implCls else IKey.DEFAULT_SETTING_IMPL_CLS
        config?.implCls ?: defalut
    }()

    override fun equals(other: Any?): Boolean {
        if (other is KeyConfig) {
            return name == other.name && implCls == other.implCls
        }
        return false
    }
}
