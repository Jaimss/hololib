package dev.jaims.hololib.core

import com.google.gson.annotations.Expose
import dev.jaims.hololib.core.util.sendHidePackets
import dev.jaims.hololib.core.util.sendShowPackets
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*
import javax.sound.sampled.Line

@Suppress("MemberVisibilityCanBePrivate", "unused", "NAME_SHADOWING")
data class HologramPage internal constructor(
    var parent: Hologram,
    @Expose private val linesData: MutableList<HologramLine>,
    @Expose private val viewersData: MutableSet<UUID> = mutableSetOf()
) {

    /**
     * True if the hologram has an arrow line.
     */
    @Expose
    var hasArrows: Boolean = false
        internal set

    /**
     * The [HologramLine]s of this page. Acts as a getter for the private [linesData]
     * The data value is private because the logic to change it is more complicated then just mutating the variable.
     */
    val lines: List<HologramLine>
        get() = linesData.toList()

    /**
     * The viewers of this page. Acts as a getter for the private [viewersData]
     * The data value is private because the logic to change it is more complicated then just mutating the variable.
     */
    val viewers: Set<UUID>
        get() = viewersData.toSet()

    /**
     * Similar to [viewers] but its all the online [Player]s
     */
    val viewingPlayers: Set<Player>
        get() = viewersData.mapNotNull { Bukkit.getPlayer(it) }.toSet()

    /**
     * Completely despawn a hologram page.
     */
    fun despawn() {
        viewersData.clear()
        linesData.forEach(HologramLine::despawn)
    }

    /**
     * Clear all the lines of a hologram page.
     */
    fun clearLines() {
        linesData.forEach(HologramLine::despawn)
    }

    /**
     * Update this page. Will re-render the hologram for the viewers.
     */
    fun update() {
        val oldViewers = viewingPlayers
        hide(*viewingPlayers.toTypedArray())
        show(*oldViewers.toTypedArray())
    }

    /**
     * Insert a new line
     *
     * @param index the index to insert the line at. All following lines will be moved "down" one.
     * @param content the content of the line.
     */
    fun insertLine(index: Int, content: String, type: LineType = LineType.DEFAULT) {
        linesData.add(index, HologramLine(parent, content, index, type))
        linesData.filterIndexed { i, _ -> i > index }.forEach(HologramLine::teleportDown)
        update()
    }

    /**
     * Set an existing line to a new value.
     *
     * @param index the index of the line
     * @param content the content of the line
     *
     * @return true if the operation was successful, false if not. Might be false if there is no line at the given index. Will also be false
     * if you try to set the index that contains the page arrows.
     */
    fun setLine(index: Int, content: String): Boolean {
        if (hasArrows && index == linesData.size - 1) return false
        val line = linesData.getOrNull(index) ?: return false
        line.contentData = content
        update()
        return true
    }

    /**
     * Remove a line at a given index.
     *
     * @param index the index of the line to remove
     *
     * @return a [HologramLine] that was removed or null if nothing was removed.
     */
    fun removeLine(index: Int): HologramLine? {
        val removed = linesData.getOrNull(index) ?: return null
        linesData.filterIndexed { i, _ -> i >= index }.forEach(HologramLine::teleportUp)
        removed.despawn()
        linesData.remove(removed)
        update()
        return removed
    }

    /**
     * Add a varable amount of lines to the end of this hologram.
     *
     * @param contents the lines to add
     */
    fun addLines(vararg contents: String, type: LineType = LineType.DEFAULT) {
        contents.forEach { content ->
            if (hasArrows) {
                insertLine(linesData.size - 1, content, type)
            } else {
                insertLine(linesData.size, content, type)
            }
        }
        update()
    }

    /**
     * Show this page to an amount of players.
     *
     * @param player the players to show this page to.
     */
    internal fun show(vararg player: Player) {
        linesData.forEach { line ->
            player.forEach { player ->
                println("Showing line ${line.index} to ${player.uniqueId}")
                sendShowPackets(line, player)
                viewersData.add(player.uniqueId)
            }
        }
    }

    /**
     * Hide this hologram page from a list of players.
     *
     * @param players the players to hide it from
     */
    internal fun hide(vararg players: Player) {
        linesData.forEach { line ->
            players.forEach { player ->
                println("Hiding line ${line.index} from ${player.uniqueId}")
                sendHidePackets(line, player)
                viewersData.remove(player.uniqueId)
            }
        }
    }
}