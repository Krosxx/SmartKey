package cn.vove7.smartkey.collections

class ObserveableMap<K, V>(
    private val src: MutableMap<K, V>,
    val onUpdate: () -> Unit
) : MutableMap<K, V> by src {
    override fun toString(): String {
        return src.toString()
    }

    override fun put(key: K, value: V): V? = src.put(key, value).also {
        onUpdate()
    }

    override fun clear() =
        src.clear().also {
            onUpdate()
        }


    override fun putAll(from: Map<out K, V>) = src.putAll(from).also {
        onUpdate()
    }

    override fun putIfAbsent(p0: K, p1: V): V? = src.putIfAbsent(p0, p1).also {
        onUpdate()
    }

    override fun remove(key: K): V? = src.remove(key).also {
        onUpdate()
    }

    override fun remove(key: K, value: V): Boolean = src.remove(key, value).also {
        onUpdate()
    }

    override fun replace(p0: K, p1: V, p2: V): Boolean = src.replace(p0, p1, p2).also { onUpdate() }

    override fun replace(p0: K, p1: V): V? = src.replace(p0, p1).also { onUpdate() }

}