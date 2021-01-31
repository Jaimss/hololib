package dev.jaims.hololib.core.util

import org.bukkit.entity.Player

var HOLOGRAM_LINE_TRANSFORM: (player: Player, content: String) -> String = { _, content -> content }

const val LINE_SPACE = 0.25
