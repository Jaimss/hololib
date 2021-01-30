package dev.jaims.hololib.example.command

import dev.jaims.hololib.core.builder.buildHologram
import dev.jaims.hololib.example.ExamplePlugin
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CreateHologram(private val plugin: ExamplePlugin) : CommandExecutor {

    init {
        plugin.getCommand("createhologram")?.setExecutor(this)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        plugin.hologram = buildHologram(args[0], (sender as Player).location) {
            val lines = args.drop(1).joinToString(" ").split("\\n").map { it.trim() }
            addPage(*lines.toTypedArray())
        }
        sender.sendMessage("Created!")
        return true
    }

}