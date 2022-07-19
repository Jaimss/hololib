package dev.jaims.hololib.core

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.EnumWrappers
import dev.jaims.hololib.core.component.LEGACY_SERIALIZER
import dev.jaims.hololib.core.event.HologramClickEvent
import dev.jaims.hololib.core.listener.WorldSwitchEventListener
import dev.jaims.hololib.core.util.PacketManager
import dev.jaims.hololib.core.util.PacketManager_1_18_2_R1
import dev.jaims.hololib.core.util.PacketManager_1_19_R1
import dev.jaims.hololib.core.version.BukkitVersion
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class HololibManager(val plugin: JavaPlugin) {

    internal companion object {
        lateinit var instance: HololibManager
        lateinit var packetManager: PacketManager
    }

    private fun getPacketManager(): PacketManager {
        val bukkitVersion = BukkitVersion(plugin.server.bukkitVersion)
        return when {
            bukkitVersion >= BukkitVersion("1.19-R0.1-SNAPSHOT") -> PacketManager_1_19_R1()
            bukkitVersion >= BukkitVersion("1.18.2-R0.1-SNAPSHOT") -> PacketManager_1_18_2_R1()
            else -> {
                plugin.logger.severe(
                    "Hololib does not support bukkit version ${plugin.server.bukkitVersion}." +
                            "Using latest as default. Proceed with caution, unexpected behavior may occur!"
                )
                PacketManager_1_19_R1()
            }
        }
    }

    init {
        instance = this
        packetManager = getPacketManager()
        plugin.logger.info("HoloLib using packet manager: ${packetManager::class.simpleName}...")
        plugin.server.pluginManager.registerEvents(WorldSwitchEventListener(this), plugin)
        packetManager.protocolManager.addPacketListener(
            object : PacketAdapter(plugin, PacketType.Play.Client.USE_ENTITY) {
                override fun onPacketReceiving(event: PacketEvent) {
                    // get some data
                    val clickType = event.packet.enumEntityUseActions.read(0).action
                    val entityId = event.packet.integers.read(0)
                    var hologram: Hologram? = null
                    // get the correct hologram
                    cachedHolograms.forEach { holo ->
                        holo.pages.forEach { page ->
                            page.lines.forEach { line ->
                                if (line.entityId == entityId || line.centeredEntityId == entityId) hologram =
                                    line.parent
                            }
                        }
                    }
                    if (hologram == null) return
                    // call the click event
                    val clickEvent = HologramClickEvent(hologram!!, event.player, clickType, entityId, event)
                    Bukkit.getServer().pluginManager.callEvent(clickEvent)

                    // respect it being cancelled
                    if (clickEvent.isCancelled) return
                    // if you don't want to switch by default, we disable it
                    if (!switchPageWithLeftRightClick) return
                    // left goes to previous, right goes to next
                    when (clickEvent.clickType) {
                        EnumWrappers.EntityUseAction.ATTACK -> {
                            if (hologram!!.getCurrentPageIndex(event.player) != 0) hologram!!.showPreviousPage(event.player)
                        }
                        EnumWrappers.EntityUseAction.INTERACT_AT -> {
                            if (hologram!!.getCurrentPageIndex(event.player) != hologram!!.pages.size - 1) hologram!!.showNextPage(
                                event.player
                            )
                        }
                        else -> {
                        }
                    }
                }
            }
        )
    }

    /**
     * This is the hologram cache containing every hologram that exists.
     * If a hologram is removed from here certain library features may not work like calling a click event.
     */
    val cachedHolograms: MutableList<Hologram> = mutableListOf()

    /**
     * A predicate to determine if a player can see a given hologram. This is checked in the world switch event.
     */
    val showHologramPredicate: (Player, Hologram) -> Boolean = { _, _ -> true }

    /**
     * A transformation is what happens to every line whenever the [Hologram] is [Hologram.update]d. This allows
     * you to set placeholders on every update, colorize lines, use hex colors, or whatever you like.
     *
     * Since 7/19/22, this uses a [Component]. The default is to use the [LEGACY_SERIALIZER] to deserialize
     * the content into a [Component]
     */
    var lineTransformation: (player: Player, content: String) -> Component = { _, content ->
        LEGACY_SERIALIZER.deserialize(content)
    }

    /**
     * If this is false, no code will run when a player clicks on a hologram unless you write a custom [HologramClickEvent]
     */
    var switchPageWithLeftRightClick: Boolean = true

    /**
     * Set the default for wether a hologram should have arrows when created. You can still set this to false manually later on.
     */
    var defaultHasArrows: Boolean = true

    /**
     * Set the default arrow left. You can change this for each individual hologram, this is just the default.
     * This will be put before the [defaultArrowRight]. You can use this to changethe message that shows at the bottom.
     */
    var defaultArrowLeft: String = "&7&o(Left Click) &r&a<<< "

    /**
     * Set the default arrow right. You can change this for each individual hologram, this is just the default.
     * This will be put after the [defaultArrowRight]. You can use this to change the message that shows at the bottom.
     */
    var defaultArrowRight: String = " &a>>> &7&o(Right Click)"

    /**
     * Set the space between each [HologramLine]. 0.25 is the default and is fine in most cases.
     */
    var lineSpace = 0.25

}