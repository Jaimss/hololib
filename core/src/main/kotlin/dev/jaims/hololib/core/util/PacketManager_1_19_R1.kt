package dev.jaims.hololib.core.util

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import dev.jaims.hololib.core.HologramLine
import dev.jaims.hololib.core.HololibManager
import dev.jaims.hololib.core.component.GSON_SERIALIZER
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import java.util.*

class PacketManager_1_19_R1 : PacketManager {

    override val protocolManager: ProtocolManager = ProtocolLibrary.getProtocolManager()

    override fun sendHidePackets(line: HologramLine, player: Player) {
        // https://wiki.vg/Protocol#Destroy_Entities
        val packet = PacketContainer(PacketType.Play.Server.ENTITY_DESTROY)
        with(packet) {
            intLists.write(0, listOf(line.entityId, line.centeredEntityId))
        }
        protocolManager.sendServerPacket(player, packet)
    }

    override fun sendShowPackets(line: HologramLine, player: Player) {
        if (player.location.world.name != line.location.world.name) return
        val packet = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY)
        with(packet) {
            integers.write(0, line.entityId)
            entityTypeModifier.write(0, EntityType.ARMOR_STAND)
            doubles.write(0, line.location.x)
                .write(1, line.location.y)
                .write(2, line.location.z)
            uuiDs.write(0, line.uniqueId)
        }
        val centeredPacket = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY)
        with(centeredPacket) {
            integers.write(0, line.centeredEntityId)
            entityTypeModifier.write(0, EntityType.ARMOR_STAND)
            doubles.write(0, line.location.x)
                .write(1, line.location.y + 1)
                .write(2, line.location.z)
            uuiDs.write(0, line.centeredUniqueID)
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
                val lineComponent = HololibManager.instance.lineTransformation(player, line.content)
                val chatComponent = WrappedChatComponent.fromJson(GSON_SERIALIZER.serialize(lineComponent))
                setObject(nameValueIndex, Optional.of(chatComponent.handle))
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
}
