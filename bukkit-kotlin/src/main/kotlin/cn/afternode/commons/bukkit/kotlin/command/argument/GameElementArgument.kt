package cn.afternode.commons.bukkit.kotlin.command.argument

import cn.afternode.commons.bukkit.kotlin.command.ArgumentResolver
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.potion.PotionEffectType

//<editor-fold desc="materials">
@Suppress("DEPRECATION")
private val materials by lazy { Material.entries.filterNot(Material::isLegacy).map(Material::name) }
@Suppress("DEPRECATION")
private val items by lazy { Material.entries.filter(Material::isItem).filterNot(Material::isLegacy).map(Material::name) }
@Suppress("DEPRECATION")
private val blocks by lazy { Material.entries.filter(Material::isBlock).filterNot(Material::isLegacy).map(Material::name) }

/**
 * Block/Item types argument
 * @see Material
 */
open class MaterialArgument(override val key: String) : ArgumentResolver<Material> {
    override fun resolve(sender: CommandSender, current: String): Material? =
        try {
            Material.valueOf(current.uppercase())
        } catch (_: IllegalArgumentException) {
            null
        }

    override fun completion(sender: CommandSender, current: String): List<String> =
        materials.filter { it.startsWith(current) }
}

fun materialArgument(key: String) =
    MaterialArgument(key)

/**
 * Item types argument
 * @see Material
 * @see Material.isItem
 */
class ItemTypeArgument(key: String) : MaterialArgument(key) {
    override fun resolve(sender: CommandSender, current: String): Material? {
        return super.resolve(sender, current)?.takeIf(Material::isItem)
    }

    override fun completion(sender: CommandSender, current: String): List<String> =
        items.filter { it.startsWith(current) }
}

fun itemTypeArgument(key: String) =
    ItemTypeArgument(key)

/**
 * Block types argument
 * @see Material
 * @see Material.isBlock
 */
class BlockTypeArgument(key: String) : MaterialArgument(key) {
    override fun resolve(sender: CommandSender, current: String): Material? =
        super.resolve(sender, current)?.takeIf(Material::isBlock)

    override fun completion(sender: CommandSender, current: String): List<String> =
        blocks.filter { it.startsWith(current) }
}

fun blockTypeArgument(key: String) =
    BlockTypeArgument(key)
//</editor-fold>

//<editor-fold desc="entity">
private val entities by lazy { EntityType.entries.map(EntityType::name) }
private val livingEntities by lazy { EntityType.entries.filter(EntityType::isAlive).map(EntityType::name) }

/**
 * Entity types argument
 * @see EntityType
 */
open class EntityTypeArgument(override val key: String) : ArgumentResolver<EntityType> {
    override fun resolve(
        sender: CommandSender,
        current: String
    ): EntityType? =
        try {
            EntityType.valueOf(current.uppercase())
        } catch (_: IllegalArgumentException) {
            null
        }

    override fun completion(sender: CommandSender, current: String): List<String> =
        entities.filter { it.startsWith(current) }
}

fun entityTypeArgument(key: String) =
    EntityTypeArgument(key)

/**
 * Entity type argument with living entity filter
 * @see EntityType
 * @see EntityType.isAlive
 */
class LivingEntityTypeArgument(key: String) : EntityTypeArgument(key) {
    override fun resolve(sender: CommandSender, current: String): EntityType? =
        super.resolve(sender, current)?.takeIf(EntityType::isAlive)

    override fun completion(sender: CommandSender, current: String): List<String> =
        livingEntities.filter { it.startsWith(current) }
}

fun livingEntityTypeArgument(key: String) =
    LivingEntityTypeArgument(key)
//</editor-fold>

//<editor-fold desc="buff">
private val enchantments by lazy { Enchantment.values().map(Enchantment::getKey).map(NamespacedKey::toString) }
private val potions by lazy { PotionEffectType.values().map(PotionEffectType::getKey).map(NamespacedKey::toString) }

/**
 * Enchantment identifier argument
 *
 * For formats, see [org.bukkit.NamespacedKey]
 *
 * @see Enchantment
 */
class EnchantmentArgument(override val key: String) : ArgumentResolver<Enchantment> {

    override fun resolve(
        sender: CommandSender,
        current: String
    ): Enchantment? =
        NamespacedKey.fromString(current)
            ?.let(Enchantment::getByKey)

    override fun completion(sender: CommandSender, current: String): List<String> =
        enchantments.filter { it.startsWith(current) }
}

fun enchantmentArgument(key: String) =
    EnchantmentArgument(key)

/**
 * Potion effect identifier argument
 *
 * For formats, see [org.bukkit.NamespacedKey]
 *
 * @see PotionEffectType
 */
class PotionEffectTypeArgument(override val key: String) : ArgumentResolver<PotionEffectType> {
    override fun resolve(
        sender: CommandSender,
        current: String
    ): PotionEffectType? =
        NamespacedKey.fromString(current)
            ?.let(PotionEffectType::getByKey)

    override fun completion(sender: CommandSender, current: String): List<String> =
        potions.filter { it.startsWith(current) }
}

fun potionEffectTypeArgument(key: String) =
    PotionEffectTypeArgument(key)
//</editor-fold>
