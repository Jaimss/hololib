package dev.jaims.hololib.gson

import com.google.gson.*
import dev.jaims.hololib.core.Hologram
import org.bukkit.Location
import java.lang.reflect.Type

class HologramAdapter : JsonDeserializer<Hologram> {
    private val gson: Gson = GsonBuilder().registerTypeAdapter(Location::class.java, LocationAdapter()).create()

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext?): Hologram {
        val hologram = gson.fromJson(json, Hologram::class.java)
        hologram.pages.forEach { page ->
            page.parent = hologram
            page.lines.forEach { line ->
                line.parent = hologram
            }
        }
        return hologram
    }

}