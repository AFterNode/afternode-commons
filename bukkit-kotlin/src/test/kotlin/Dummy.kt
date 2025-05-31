import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Server
import org.bukkit.command.CommandSender
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionAttachment
import org.bukkit.permissions.PermissionAttachmentInfo
import org.bukkit.plugin.Plugin
import java.util.UUID

object Dummy: CommandSender {
    override fun sendMessage(message: String) {
        TODO("Not yet implemented")
    }

    override fun sendMessage(vararg messages: String) {
        TODO("Not yet implemented")
    }

    override fun sendMessage(sender: UUID?, message: String) {
        TODO("Not yet implemented")
    }

    override fun sendMessage(sender: UUID?, vararg messages: String) {
        TODO("Not yet implemented")
    }

    override fun sendMessage(message: Component) {
        println(PlainTextComponentSerializer.plainText().serialize(message))
    }

    override fun getServer(): Server {
        TODO("Not yet implemented")
    }

    override fun getName(): String {
        TODO("Not yet implemented")
    }

    override fun spigot(): CommandSender.Spigot {
        TODO("Not yet implemented")
    }

    override fun name(): Component {
        TODO("Not yet implemented")
    }

    override fun isPermissionSet(name: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun isPermissionSet(perm: Permission): Boolean {
        TODO("Not yet implemented")
    }

    override fun hasPermission(name: String): Boolean = true

    override fun hasPermission(perm: Permission): Boolean = true

    override fun addAttachment(
        plugin: Plugin,
        name: String,
        value: Boolean
    ): PermissionAttachment {
        TODO("Not yet implemented")
    }

    override fun addAttachment(plugin: Plugin): PermissionAttachment {
        TODO("Not yet implemented")
    }

    override fun addAttachment(
        plugin: Plugin,
        name: String,
        value: Boolean,
        ticks: Int
    ): PermissionAttachment? {
        TODO("Not yet implemented")
    }

    override fun addAttachment(
        plugin: Plugin,
        ticks: Int
    ): PermissionAttachment? {
        TODO("Not yet implemented")
    }

    override fun removeAttachment(attachment: PermissionAttachment) {
        TODO("Not yet implemented")
    }

    override fun recalculatePermissions() {
        TODO("Not yet implemented")
    }

    override fun getEffectivePermissions(): Set<PermissionAttachmentInfo?> {
        TODO("Not yet implemented")
    }

    override fun isOp(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setOp(value: Boolean) {
        TODO("Not yet implemented")
    }
}