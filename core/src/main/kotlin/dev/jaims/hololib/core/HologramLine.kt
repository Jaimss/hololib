package dev.jaims.hololib.core

import com.google.gson.annotations.Expose
import dev.jaims.hololib.core.util.sendHidePackets
import org.bukkit.Bukkit
import org.bukkit.Location

data class HologramLine internal constructor(
    var parent: Hologram,
    @Expose internal var contentData: String,
    @Expose internal var index: Int
) {
    /**
     * Acts as a getter for the private [contentData]
     * The data value is private because the logic to change it is more complicated then just mutating the variable.
     */
    val content: String
        get() = contentData

    /**
     * The data value is private because the logic to change it is more complicated then just mutating the variable.
     */
    val location: Location
        get() = parent.location.clone().subtract(0.0, HololibManager.instance.lineSpace * index, 0.0)

    /**
     * Generate an entity ID.
     */
    @Expose
    val entityId = (Math.random() * Int.MAX_VALUE).toInt()

    /**
     * Move a line of an armor stand up
     */
    internal fun teleportUp() {
        index -= 1
    }

    /**
     * Move a line down the line space.
     */
    internal fun teleportDown() {
        index += 1
    }

    /**
     * Despawn an armor stand.
     */
    internal fun despawn() {
        Bukkit.getOnlinePlayers().forEach {
            sendHidePackets(this, it)
        }
    }

}