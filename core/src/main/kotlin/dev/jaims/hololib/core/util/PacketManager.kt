package dev.jaims.hololib.core.util

import com.comphenix.protocol.ProtocolManager
import dev.jaims.hololib.core.HologramLine
import org.bukkit.entity.Player

interface PacketManager {

    val protocolManager: ProtocolManager

    /**
     * Send the appropriate packets to Hide a [line] from a [player]
     */
    fun sendHidePackets(line: HologramLine, player: Player)

    /**
     * Send the appropriate packets to Hide a [line] from a [player]
     */
    fun sendShowPackets(line: HologramLine, player: Player)

}