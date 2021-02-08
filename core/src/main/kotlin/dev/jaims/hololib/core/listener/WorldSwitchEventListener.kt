package dev.jaims.hololib.core.listener

import dev.jaims.hololib.core.HololibManager
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent

/**
 * A listener for every time a player switches worlds, show them holograms that they can't see using
 * a specific predicate for the hologram.
 */
internal class WorldSwitchEventListener(private val hololibManager: HololibManager) : Listener {

    fun PlayerTeleportEvent.onWorldChange() {
        if (player.location.world == from.world) return
        hololibManager.cachedHolograms.forEach { hologram ->
            // continue if they don't meet predicate
            if (!hololibManager.showHologramPredicate(player, hologram)) return@forEach
            if (hologram.getCurrentPageIndex(player) == null) hologram.showNextPage(player)
        }
    }

}