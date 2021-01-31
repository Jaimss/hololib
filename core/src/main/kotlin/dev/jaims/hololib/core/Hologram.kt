package dev.jaims.hololib.core

import dev.jaims.hololib.core.util.HAS_PAGE_ARROWS_DEFAULT
import dev.jaims.hololib.core.util.HOLOGRAM_LINE_TRANSFORM
import dev.jaims.hololib.core.util.LEFT_PAGE_ARROW_DEFAULT
import dev.jaims.hololib.core.util.RIGHT_PAGE_ARROW_DEFAULT
import org.bukkit.Location
import org.bukkit.entity.Player

/**
 * The default Hologram class. Contains a list of pages, as well as a name and location.
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
data class Hologram internal constructor(
    var name: String,
    private var locationData: Location,
    private val pageData: MutableList<HologramPage> = mutableListOf()
) {

    /**
     * Set this to false if you want to remove the page arrows.
     */
    val hasPageArrows: Boolean = HAS_PAGE_ARROWS_DEFAULT

    /**
     * The left page arrow.
     */
    val leftArrow: String = LEFT_PAGE_ARROW_DEFAULT

    /**
     * The right page arrow.
     */
    val rightArrow: String = RIGHT_PAGE_ARROW_DEFAULT

    /**
     * The [HologramPage]s of the Hologram. Acts as a getter for the private [pageData]
     * The data value is private because the logic to change it is more complicated then just mutating the variable.
     */
    val pages: List<HologramPage>
        get() = pageData.toList()

    /**
     * The location of this [Hologram]. Acts as a getter for the private [locationData]
     * The data value is private because the logic to change it is more complicated then just mutating the variable.
     */
    val location: Location
        get() = locationData

    /**
     * Update the whole hologram.
     * @param transform the transformation that should occur on each line. Can be used to parse placeholders, colorize the line, etc.
     */
    fun update(transform: (player: Player, content: String) -> String = HOLOGRAM_LINE_TRANSFORM) {
        pageData.forEach { page ->
            if (pages.size > 1) {
                if (hasPageArrows && !page.hasArrows) {
                    page.addLines("$leftArrow$rightArrow")
                    page.hasArrows = true
                }
                if (page.hasArrows && !hasPageArrows) {
                    page.removeLine(page.lines.size)
                    page.hasArrows = false
                }
            }
            if (pages.size <= 1 && page.hasArrows) {
                page.removeLine(page.lines.size - 1)
                page.hasArrows = false
            }
            page.update(transform)
        }
    }

    /**
     * Despawn a hologram.
     */
    fun despawn() {
        pageData.forEach(HologramPage::despawn)
    }

    /**
     * Add a a page to the [Hologram].
     *
     * @param lines the variable amount of lines the page will contain.
     */
    fun addPage(vararg lines: String): HologramPage {
        return insertPage(pageData.size, *lines)
    }

    /**
     * Sets a page of this [Hologram]
     *
     * @param index the index of the page
     * @param lines the variable amount of lines that will be the new page.
     *
     * @return true if success, false if fail. can fail if the index doesn't exist in the pages
     */
    fun setPage(index: Int, vararg lines: String, transform: (player: Player, content: String) -> String = HOLOGRAM_LINE_TRANSFORM): Boolean {
        val page = pageData.getOrNull(index) ?: return false
        page.clearLines()
        page.addLines(*lines)
        update(transform)
        return true
    }

    /**
     * Inserts a page for this [Hologram]. Will push the pages after this to an index 1 greater than their current.
     *
     * @param index the index of this new page.
     * @param lines the lines of the page that you are inserting.
     */
    fun insertPage(
        index: Int,
        vararg lines: String,
        transform: (player: Player, content: String) -> String = HOLOGRAM_LINE_TRANSFORM
    ): HologramPage {
        val page =
            HologramPage(location, mutableListOf()).apply { addLines(*lines, transform = transform) }
        pageData.add(index, page)
        update(transform)
        return page
    }

    /**
     * Remove a page from the [Hologram]
     *
     * @param index the index of the page to remove.
     *
     * @return the [HologramPage] removed or null if the page didn't exist at the given [index]
     */
    fun removePage(index: Int, transform: (player: Player, content: String) -> String = HOLOGRAM_LINE_TRANSFORM): HologramPage? {
        val removed = pageData.getOrNull(index) ?: return null
        removed.despawn()
        pageData.remove(removed)
        update(transform)
        return removed
    }

    /**
     * Get the current page a [Player] is looking at.
     *
     * @return the index of the page, or null if they can't see a page.
     */
    fun getCurrentPageIndex(player: Player): Int? {
        val index = pageData.indexOfFirst { it.viewers.contains(player.uniqueId) }
        if (index == -1) return null
        return index
    }

    /**
     * A method to hide all the pages. Is smart to call this before show a page to a player.
     */
    fun hideAllPages(player: Player) {
        val pageIndex = getCurrentPageIndex(player) ?: return
        pages.getOrNull(pageIndex)?.hide(player)
    }

    /**
     * Will show the next page to a player. If the player is looking at the final page it will fail silently and show them nothing.
     * If the player is not looking at any page, it will show them the first page on the hologram, or nothing if the pages size is 0.
     *
     * @param players the players to show the next page to.
     */
    fun showNextPage(vararg players: Player) {
        players.forEach { player ->
            val currentPageIndex = getCurrentPageIndex(player) ?: run {
                pages.firstOrNull()?.show(player)
                return@forEach
            }
            pages.getOrNull(currentPageIndex)?.hide(player) ?: return@forEach
            pages.getOrNull(currentPageIndex + 1)?.show(player)
        }
    }

    /**
     * Will show the previous hologram page to a player. If the player is looking at the first page, it will fail silently and remove the
     * hologram from their view. If the player is not looking at a page at all, it will fail silently and show nothing.
     *
     * @param players the players to show the previous page to.
     */
    fun showPreviousPage(vararg players: Player) {
        players.forEach { player ->
            val currentPageIndex = getCurrentPageIndex(player) ?: return@forEach
            pages.getOrNull(currentPageIndex)?.hide(player) ?: return@forEach
            pageData.getOrNull(currentPageIndex - 1)?.show(player)
        }
    }
}

