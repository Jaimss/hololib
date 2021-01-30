package dev.jaims.hololib.example.command

import dev.jaims.hololib.example.ExamplePlugin
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class AddPage(private val plugin: ExamplePlugin) : CommandExecutor {

    init {
        plugin.getCommand("addpage")?.setExecutor(this)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        plugin.hologram?.addPage(*args.joinToString(" ").split("\n").toTypedArray())
        sender.sendMessage("Page Added")
        return true
    }

}