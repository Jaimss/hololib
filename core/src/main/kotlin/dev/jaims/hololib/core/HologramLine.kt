package dev.jaims.hololib.core

import dev.jaims.hololib.core.util.LINE_SPACE
import dev.jaims.hololib.core.util.sendHidePackets
import org.bukkit.Bukkit
import org.bukkit.Location

data class HologramLine internal constructor(internal var contentData: String, internal var locationData: Location) {
    /**
     * Acts as a getter for the private [contentData]
     * The data value is private because the logic to change it is more complicated then just mutating the variable.
     */
    val content: String
        get() = contentData

    /**
     * Acts as a getter for [locationData]
     * The data value is private because the logic to change it is more complicated then just mutating the variable.
     */
    val location: Location
        get() = locationData

    /**
     * Generate an entity ID.
     */
    val entityId = (Math.random() * Int.MAX_VALUE).toInt()

    /**
     * Move a line of an armor stand up
     */
    internal fun teleportUp() {
        locationData.add(0.0, LINE_SPACE, 0.0)
    }

    /**
     * Move a line down the line space.
     */
    internal fun teleportDown() {
        locationData.subtract(0.0, LINE_SPACE, 0.0)
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