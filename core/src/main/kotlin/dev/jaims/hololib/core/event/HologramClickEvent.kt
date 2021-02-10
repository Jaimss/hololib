package dev.jaims.hololib.core.event

import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.EnumWrappers
import dev.jaims.hololib.core.Hologram
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * An event called when a player clicks on a hologram.
 *
 * @param hologram the hologram that was clicked.
 * @param player the player who clicked the hologram.
 * @param clickType the type of click. can be ATTACK, INTERACT or INTERACT_AT
 * @param entityId the ID of the line that was clicked.
 * @param packetEvent the rest of the event if you need anything from it.
 */
@Suppress("MemberVisibilityCanBePrivate")
class HologramClickEvent(
    val hologram: Hologram,
    val player: Player,
    val clickType: EnumWrappers.EntityUseAction,
    val entityId: Int,
    val packetEvent: PacketEvent
) : Event(true), Cancellable {
    companion object {
        @JvmStatic
        val HANDLERS_LIST = HandlerList()

        @JvmStatic
        fun getHandlerList() = HANDLERS_LIST
    }

    private var isCancelled = false

    override fun getHandlers(): HandlerList {
        return HANDLERS_LIST
    }

    /**
     * Gets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins
     *
     * @return true if this event is cancelled
     */
    override fun isCancelled(): Boolean {
        return isCancelled
    }

    /**
     * Sets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins.
     *
     * @param cancel true if you wish to cancel this event
     */
    override fun setCancelled(cancel: Boolean) {
        isCancelled = cancel
    }
}