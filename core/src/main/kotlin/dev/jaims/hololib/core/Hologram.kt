package dev.jaims.hololib.core

import dev.jaims.hololib.core.util.TRANSFORM
import org.bukkit.Location
import org.bukkit.entity.Player

/**
 * The default Hologram class. Contains a list of pages, as well as a name and location.
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
data class Hologram internal constructor(
    private var nameData: String,
    private var locationData: Location,
    private val pageData: MutableList<HologramPage> = mutableListOf()
) {

    /**
     * The [HologramPage]s of the Hologram. Acts as a getter for the private [pageData]
     * The data value is private because the logic to change it is more complicated then just mutating the variable.
     */
    val pages: List<HologramPage>
        get() = pageData.toList()

    /**
     * The name of the [Hologram]. Acts as a getter for the private [nameData]
     * The data value is private because the logic to change it is more complicated then just mutating the variable.
     */
    val name: String
        get() = nameData

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
    fun update(transform: (player: Player, content: String) -> String = TRANSFORM) {
        pageData.forEach { page -> page.update(transform) }
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
    fun setPage(index: Int, vararg lines: String, transform: (player: Player, content: String) -> String = TRANSFORM): Boolean {
        val page = pageData.getOrNull(index) ?: return false
        page.clear()
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
        transform: (player: Player, content: String) -> String = TRANSFORM
    ): HologramPage {
        val page = buildPage(location) { addLines(*lines, transform = transform) }
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
    fun removePage(index: Int, transform: (player: Player, content: String) -> String = TRANSFORM): HologramPage? {
        val removed = pageData.getOrNull(index) ?: return null
        removed.clear()
        pageData.remove(removed)
        update(transform)
        return removed
    }

    /**
     * Get the current page a [Player] is looking at.
     *
     * @return the index of the page, or null if they can't see a page.
     */
    fun getCurrentPage(player: Player): Int? {
        val index = pageData.indexOfFirst { it.viewers.contains(player.uniqueId) }
        if (index == -1) return null
        return index
    }

    /**
     * A method to hide all the pages. Is smart to call this before show a page to a player.
     */
    fun hideAllPages(player: Player) {
        val pageIndex = getCurrentPage(player) ?: return
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
            val currentPageIndex = getCurrentPage(player) ?: run {
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
            val currentPageIndex = getCurrentPage(player) ?: return@forEach
            pages.getOrNull(currentPageIndex)?.hide(player) ?: return@forEach
            pageData.getOrNull(currentPageIndex - 1)?.show(player)
        }
    }
}

