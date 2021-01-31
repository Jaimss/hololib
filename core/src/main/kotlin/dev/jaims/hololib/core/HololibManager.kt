package dev.jaims.hololib.core

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.EnumWrappers
import dev.jaims.hololib.core.util.protocolManager
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class HololibManager(val plugin: JavaPlugin) {

    internal companion object {
        lateinit var instance: HololibManager
    }

    init {
        instance = this
        protocolManager.addPacketListener(
            object : PacketAdapter(plugin, PacketType.Play.Client.USE_ENTITY) {
                override fun onPacketReceiving(event: PacketEvent) {
                    val entityId = event.packet.integers.read(0)
                    when (event.packet.entityUseActions.read(0)) {
                        EnumWrappers.EntityUseAction.ATTACK -> previousPageByClick(event, entityId)
                        EnumWrappers.EntityUseAction.INTERACT_AT -> nextPageByClick(event, entityId)
                    }
                }
            }
        )
    }

    /**
     * The method run when a player tries to go to the next page by clicking an arrow.
     */
    var nextPageByClick: (event: PacketEvent, entityId: Int) -> Unit = { _, _ ->
        println("[Hololib] Set the next page click method to enable switching pages by right clicking.")
        println("[Hololib] Set the next page click method to enable switching pages by right clicking.")
    }

    /**
     * The method run when a player tries to go to the previous page by clicking.
     */
    var previousPageByClick: (event: PacketEvent, entityId: Int) -> Unit = { _, _ ->
        println("[Hololib] Set the previous page click method to enable switching pages by left clicking.")
        println("[Hololib] Set the previous page click method to enable switching pages by left clicking.")
    }

    /**
     * A transformation is what happens to every line whenever the [Hologram] is [Hologram.update]d. This allows
     * you to set placeholders on every update, colorize lines, use hex colors, or whatever you like.
     */
    var lineTransformation: (player: Player, content: String) -> String =
        { _, content -> ChatColor.translateAlternateColorCodes('&', content) }

    /**
     * Set the default for wether a hologram should have arrows when created. You can still set this to false manually later on.
     */
    var defaultHasArrows: Boolean = true

    /**
     * Set the default arrow left. You can change this for each individual hologram, this is just the default.
     */
    var defaultArrowLeft: String = "««  "

    /**
     * Set the default arrow right. You can change this for each individual hologram, this is just the default.
     */
    var defaultArrowRight: String = "  »»"

    /**
     * Set the space between each [HologramLine]. 0.25 is the default and is fine in most cases.
     */
    var lineSpace = 0.25

}