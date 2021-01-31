package dev.jaims.hololib.gson

import com.google.gson.*
import org.bukkit.Bukkit
import org.bukkit.Location
import java.lang.reflect.Type

data class LocationHolder(
    val world: String,
    val x: Double,
    val y: Double,
    val z: Double,
    val yaw: Float,
    val pitch: Float
) {
    companion object {
        fun of(location: Location) = LocationHolder(location.world.name, location.x, location.y, location.z, location.yaw, location.pitch)
    }

    val location: Location
        get() = Location(Bukkit.getWorld(world), x, y, z, yaw, pitch)
}

/**
 * A [JsonSerializer] and [JsonDeserializer] for a [Location].
 */
@Suppress("UNUSED")
class LocationAdapter : JsonSerializer<Location>, JsonDeserializer<Location> {
    private val gson = GsonBuilder().setPrettyPrinting().create()

    override fun serialize(src: Location, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return gson.toJsonTree(LocationHolder.of(src), LocationHolder::class.java)
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Location {
        return gson.fromJson(json, LocationHolder::class.java).location
    }

}