package cn.vove7.smartkey.tool


/**
 * #Vog
 *
 */
typealias PrintListener = (String) -> Unit

object Vog {
    private var output_level = 0

    /**
     * @param outputLevel Log.***
     */
    fun init(outputLevel: Int): Vog {
        output_level = outputLevel
        return this
    }

    fun d(msg: Any?) {
        println(1, msg.toString())
    }

    fun wtf(msg: Any?) {
        println(5, msg.toString())
    }

    fun v(msg: Any) {
        println(0, msg.toString())
    }

    fun i(msg: Any) {
        println(2, msg.toString())
    }

    fun w(msg: Any) {
        println(3, msg.toString())
    }

    fun e(msg: Any) {
        println(4, msg.toString())
    }

    fun a(msg: Any) {
        println(6, msg.toString())
    }

    const val TAG = "VOG"


    private val liss = mutableListOf<PrintListener>()

    fun addListener(lis: PrintListener) {
        liss.add(lis)
    }

    fun removeListener(lis: PrintListener) {
        liss.remove(lis)
    }

    private fun dispatchPrinter(msg: String) {
        liss.forEach {
            it.invoke(msg)
        }
    }

    private fun println(priority: Int, msg: String) {
        if (output_level > priority)
            return//省运算

        dispatchPrinter(msg)

        val pre = findCaller(3)?.let {
            (it.methodName + "(" + it.fileName +
                    ":" + it.lineNumber + ")")
        }
        println("$pre  >> $msg")
    }

    private fun findCaller(upDepth: Int): StackTraceElement? {
        // 获取堆栈信息
        val callStack = Thread.currentThread().stackTrace
        // 最原始被调用的堆栈信息
        // 日志类名称
        val logClassName = Vog::class.java.name
        // 循环遍历到日志类标识
        var i = 0
        val len = callStack.size
        while (i < len) {
            if (logClassName == callStack[i].className)
                break
            i++
        }
        return try {
            callStack[i + upDepth]
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}
