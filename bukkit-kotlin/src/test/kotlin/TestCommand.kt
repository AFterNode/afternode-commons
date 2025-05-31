import cn.afternode.commons.bukkit.kotlin.command.CompositeCommand
import cn.afternode.commons.bukkit.kotlin.command.ParsedArguments
import cn.afternode.commons.bukkit.kotlin.command.argument.itemTypeArgument
import cn.afternode.commons.bukkit.kotlin.command.argument.plainTextArgument
import kotlin.test.Test

class TestCommand: CompositeCommand("test", "test") {
    val sub by sub("sub") {
        argument(plainTextArgument("wow", "wow_a", "wow_b"))
        argument(itemTypeArgument("item"))
        flag(plainTextArgument("flag"))
        executes = {
            println(this.args["wow"])
            println(this.args["item"])
            println(this.args["flag"])
        }
    }

    init {
        this.add(sub)
    }

    @Test
    fun testParsedArguments() {
        val parsed = ParsedArguments("test", "--test=value", "\'long", "text\'", "--test-b=\"long", "\\\\text\"")
        println(parsed.args)
        println(parsed.flags)
    }

    @Test
    fun testCompletion() {
        println(this.tabComplete(Dummy, "", "sub ".split(' ').toTypedArray()))
    }

    @Test
    fun testExecution() {
        this.execute(Dummy, "", "sub wow diamond_pickaxe --flag=awa".split(' ').toTypedArray())
    }
}
