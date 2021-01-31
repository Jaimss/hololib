package dev.jaims.hololib.gson

import com.google.gson.*
import com.google.gson.annotations.Expose
import org.bukkit.Bukkit
import org.bukkit.Location
import java.lang.reflect.Type

data class LocationHolder(
    @Expose val world: String,
    @Expose val x: Double,
    @Expose val y: Double,
    @Expose val z: Double,
    @Expose val yaw: Float,
    @Expose val pitch: Float
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
        // TODO NAME IS NULL WHY
        return gson.toJsonTree(LocationHolder.of(src), LocationHolder::class.java)
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Location {
        return gson.fromJson(json, LocationHolder::class.java).location
    }

}