import cn.vove7.smartkey.android.smartKey
import cn.vove7.smartkey.annotation.Config

/**
 * # JsonTest
 *
 * @author Vove
 * 2019/9/24
 */
fun main() {
    A.s = mutableSetOf(1L, 2L, 4L, 5L)
    print(A.s.toString())
}

@Config
object A {
    var s by smartKey(mutableSetOf<Long>())
}
