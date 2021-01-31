@file:JvmName("HololibGsonBuilder")

package dev.jaims.hololib.gson

import com.google.gson.GsonBuilder
import dev.jaims.hololib.core.Hologram
import javax.xml.stream.Location

val hololibGsonBuilder: GsonBuilder = GsonBuilder()
    .excludeFieldsWithoutExposeAnnotation()
    .registerTypeAdapter(Hologram::class.java, HologramAdapter())
    .registerTypeAdapter(Location::class.java, LocationAdapter())