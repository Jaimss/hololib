package dev.jaims.hololib.core

import dev.jaims.hololib.core.util.HOLOGRAM_LINE_TRANSFORM
import dev.jaims.hololib.core.util.LINE_SPACE
import dev.jaims.hololib.core.util.sendHidePackets
import dev.jaims.hololib.core.util.sendShowPackets
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

@Suppress("MemberVisibilityCanBePrivate", "unused", "NAME_SHADOWING")
data class HologramPage internal constructor(
    private val location: Location,
    private val linesData: MutableList<HologramLine>,
    private val viewersData: MutableSet<UUID> = mutableSetOf()
) {

    /**
     * True if the hologram has an arrow line.
     */
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
     *
     * @param transform the transformation that should occur on each line. Can be used to parse placeholders, player things, colorize, etc.
     */
    fun update(transform: (player: Player, content: String) -> String = HOLOGRAM_LINE_TRANSFORM) {
        val oldViewers = viewingPlayers
        hide(*viewingPlayers.toTypedArray())
        show(*oldViewers.toTypedArray(), transform = transform)
    }

    /**
     * Insert a new line
     *
     * @param index the index to insert the line at. All following lines will be moved "down" one.
     * @param content the content of the line.
     */
    fun insertLine(index: Int, content: String, transform: (player: Player, content: String) -> String = HOLOGRAM_LINE_TRANSFORM) {
        linesData.add(index, HologramLine(content, location.clone().subtract(0.0, LINE_SPACE * index, 0.0)))
        linesData.filterIndexed { i, _ -> i > index }.forEach(HologramLine::teleportDown)
        update(transform)
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
    fun setLine(index: Int, content: String, transform: (player: Player, content: String) -> String = HOLOGRAM_LINE_TRANSFORM): Boolean {
        if (hasArrows && index == linesData.size - 1) return false
        val line = linesData.getOrNull(index) ?: return false
        line.contentData = content
        update(transform)
        return true
    }

    /**
     * Remove a line at a given index.
     *
     * @param index the index of the line to remove
     *
     * @return a [HologramLine] that was removed or null if nothing was removed.
     */
    fun removeLine(index: Int, transform: (player: Player, content: String) -> String = HOLOGRAM_LINE_TRANSFORM): HologramLine? {
        val removed = linesData.getOrNull(index) ?: return null
        linesData.filterIndexed { i, _ -> i >= index }.forEach(HologramLine::teleportUp)
        removed.despawn()
        linesData.remove(removed)
        update(transform)
        return removed
    }

    /**
     * Add a varable amount of lines to the end of this hologram.
     *
     * @param contents the lines to add
     */
    fun addLines(vararg contents: String, transform: (player: Player, content: String) -> String = HOLOGRAM_LINE_TRANSFORM) {
        contents.forEach { content ->
            if (hasArrows) {
                insertLine(linesData.size - 1, content)
            } else {
                insertLine(linesData.size, content)
            }
        }
        update(transform)
    }

    /**
     * Show this page to an amount of players.
     *
     * @param player the players to show this page to.
     * @param transform the transformation on the line that should apply to each player. This can be used for placeholders, etc.
     */
    internal fun show(vararg player: Player, transform: (player: Player, content: String) -> String = HOLOGRAM_LINE_TRANSFORM) {
        linesData.forEach { line ->
            player.forEach { player ->
                sendShowPackets(line, player, transform)
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
                sendHidePackets(line, player)
                viewersData.remove(player.uniqueId)
            }
        }
    }
}