import cn.vove7.smartkey.annotation.Config
import cn.vove7.smartkey.key.smartKeySet

/**
 * # JsonTest
 *
 * @author Vove
 * 2019/9/24
 */
fun main() {
    A.s = mutableSetOf(1L, 2L, 3L, 4L, 5L)
    print(A.s.toString())
    A.s.add(6)
    assert(A.s.size == 6)
    A.s.add(1)
    assert(A.s.size == 6)
}

object A {
    var s by smartKeySet<Long>()
}
