package cn.vove7.smartkey.collections

/**
 * # ObserveableList
 *
 * Created on 2020/5/26
 * @author Vove
 */
class ObserveableList<T>(
    val src: MutableList<T>,
    val onUpdate: () -> Unit
) : MutableList<T> by src {
    override fun toString(): String {
        return src.toString()
    }

    override fun add(element: T): Boolean = src.add(element).also {
        onUpdate()
    }

    override fun add(index: Int, element: T) = src.add(index, element).also {
        onUpdate()
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean =
        src.addAll(index, elements).also {
            onUpdate()
        }

    override fun addAll(elements: Collection<T>): Boolean = src.addAll(elements).also {
        onUpdate()
    }

    override fun clear() = src.clear().also {
        onUpdate()
    }

    override fun remove(element: T): Boolean = src.remove(element).also {
        onUpdate()
    }

    override fun removeAll(elements: Collection<T>): Boolean = src.removeAll(elements).also {
        onUpdate()
    }

    override fun removeAt(index: Int): T = src.removeAt(index).also {
        onUpdate()
    }

    override fun set(index: Int, element: T): T = src.set(index, element).also {
        onUpdate()
    }
}