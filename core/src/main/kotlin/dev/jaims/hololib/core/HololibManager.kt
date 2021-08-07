package dev.jaims.hololib.core

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.EnumWrappers
import dev.jaims.hololib.core.event.HologramClickEvent
import dev.jaims.hololib.core.listener.WorldSwitchEventListener
import dev.jaims.hololib.core.util.protocolManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class HololibManager(val plugin: JavaPlugin) {

    internal companion object {
        lateinit var instance: HololibManager
    }

    init {
        instance = this
        plugin.server.pluginManager.registerEvents(WorldSwitchEventListener(this), plugin)
        protocolManager.addPacketListener(
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
     */
    var lineTransformation: (player: Player, content: String) -> String =
        { _, content -> ChatColor.translateAlternateColorCodes('&', content) }

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