@file:JvmName("LineUtil")

package dev.jaims.hololib.core.util

import org.bukkit.entity.Player

var HOLOGRAM_LINE_TRANSFORM: (player: Player, content: String) -> String = { _, content -> content }

/**
 * The default state of having arrows.
 */
var HAS_PAGE_ARROWS_DEFAULT: Boolean = true

/**
 * The default arrow that you would click to go left.
 */
var LEFT_PAGE_ARROW_DEFAULT: String = "««  "

/**
 * The default arrow you would click to go right.
 */
var RIGHT_PAGE_ARROW_DEFAULT: String = "  »»"

const val LINE_SPACE = 0.25
