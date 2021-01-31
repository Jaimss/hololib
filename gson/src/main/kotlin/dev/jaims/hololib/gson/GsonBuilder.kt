@file:JvmName("HololibGsonBuilder")

package dev.jaims.hololib.gson

import com.google.gson.GsonBuilder
import dev.jaims.hololib.core.Hologram
import org.bukkit.Location

val hololibGsonBuilder: GsonBuilder = GsonBuilder()
    .registerTypeAdapter(Location::class.java, LocationAdapter())
    .registerTypeAdapter(Hologram::class.java, HologramAdapter())