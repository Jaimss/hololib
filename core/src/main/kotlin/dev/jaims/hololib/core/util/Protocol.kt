package dev.jaims.hololib.core.util

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import dev.jaims.hololib.core.HologramLine
import dev.jaims.hololib.core.HololibManager
import org.bukkit.entity.Player
import java.util.*

internal val protocolManager: ProtocolManager = ProtocolLibrary.getProtocolManager()

/**
 * Send the packets to hide an armor stand from a players view.
 */
internal fun sendHidePackets(line: HologramLine, player: Player) {
    // https://wiki.vg/Protocol#Destroy_Entities
    val packet = PacketContainer(PacketType.Play.Server.ENTITY_DESTROY)
    with(packet) {
        intLists.write(0, listOf(line.entityId, line.centeredEntityId))
    }
    protocolManager.sendServerPacket(player, packet)
}

/**
 * Send the packets to show an armor stand to a player.
 */
internal fun sendShowPackets(line: HologramLine, player: Player) {
    if (player.location.world.name != line.location.world.name) return
    val packet = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING)
    with(packet) {
        integers.write(0, line.entityId)
            .write(1, 1) // armor stand type
        doubles.write(0, line.location.x)
            .write(1, line.location.y)
            .write(2, line.location.z)
        uuiDs.write(0, line.uniqueId)
        float.writeDefaults()
        shorts.writeDefaults()
    }
    val centeredPacket = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING)
    with(centeredPacket) {
        integers.write(0, line.centeredEntityId)
            .write(1, 1) // armor stand type
        doubles.write(0, line.location.x)
            .write(1, line.location.y + 1)
            .write(2, line.location.z)
        uuiDs.write(0, line.centeredUniqueID)
        float.writeDefaults()
        shorts.writeDefaults()
    }
    // set the meta data packet
    val metaDataPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA)
    with(metaDataPacket) {
        integers.write(0, line.entityId)
        // set all the metadata
        watchableCollectionModifier.write(0, WrappedDataWatcher().apply {
            // set it to invisibe
            val invisibleIndex = WrappedDataWatcher.WrappedDataWatcherObject(
                0,
                WrappedDataWatcher.Registry.get(Byte::class.javaObjectType)
            )
            setObject(invisibleIndex, 0x20.toByte())
            // set the name
            val nameValueIndex = WrappedDataWatcher.WrappedDataWatcherObject(
                2,
                WrappedDataWatcher.Registry.getChatComponentSerializer(true)
            )
            val nameVisibleIndex = WrappedDataWatcher.WrappedDataWatcherObject(
                3,
                WrappedDataWatcher.Registry.get(Boolean::class.javaObjectType)
            )
            setObject(
                nameValueIndex,
                Optional.of(
                    WrappedChatComponent.fromChatMessage(
                        HololibManager.instance.lineTransformation(
                            player,
                            line.content
                        )
                    )[0].handle
                )
            )
            setObject(nameVisibleIndex, true)
        }.watchableObjects)
    }

    val centeredMetaDataPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA)
    with(centeredMetaDataPacket) {
        integers.write(0, line.centeredEntityId)
        // set all the metadata
        watchableCollectionModifier.write(0, WrappedDataWatcher().apply {
            // set it to invisibe
            val invisibleIndex = WrappedDataWatcher.WrappedDataWatcherObject(
                0,
                WrappedDataWatcher.Registry.get(Byte::class.javaObjectType)
            )
            setObject(invisibleIndex, 0x20.toByte())
        }.watchableObjects)
    }
    // send the packets
    protocolManager.sendServerPacket(player, packet)
    protocolManager.sendServerPacket(player, metaDataPacket)
    protocolManager.sendServerPacket(player, centeredPacket)
    protocolManager.sendServerPacket(player, centeredMetaDataPacket)
}
