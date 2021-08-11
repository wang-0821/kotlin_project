package xiao.demo.cases

/**
 *
 * @author lix wang
 */
class InlineDemo() {
    inline fun f(crossinline body: () -> Unit) {
        val f = Runnable { body() }
        f.run()
        println("finish body run")
    }
}

fun main() {
    InlineDemo().f {
        println("start run body")
        return@f
    }
}