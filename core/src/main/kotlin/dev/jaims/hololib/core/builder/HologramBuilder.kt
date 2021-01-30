package dev.jaims.hololib.core.builder

import dev.jaims.hololib.core.Hologram
import org.bukkit.Location
import java.util.function.Supplier

/**
 * A [Hologram] builder using the Kotlin Pattern.
 *
 * @param name the name of the hologram
 * @param location the location of the hologram's first line
 * @param block the changes to apply to the hologram. Provides the [Hologram]
 *
 * @return a [Hologram]
 */
fun buildHologram(name: String, location: Location, block: Hologram.() -> Unit): Hologram {
    return Hologram(name, location).apply(block)
}

/**
 * A java friendly builder.
 */
class HologramBuilder(name: String, location: Location) : Supplier<Hologram> {

    /**
     * The [Hologram] with a base name & location.
     */
    private val hologram = buildHologram(name, location) {}

    /**
     * @return the [Hologram] you built.
     */
    override fun get(): Hologram = hologram

    /**
     * Add a page to the [Hologram]
     *
     * @param lines the lines the page will contain
     *
     * @return the [HologramBuilder] to fulfill the builder pattern.
     */
    fun addPage(vararg lines: String): HologramBuilder = apply { hologram.addPage(*lines) }
}