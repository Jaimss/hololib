package dev.jaims.hololib.example.command

import dev.jaims.hololib.example.ExamplePlugin
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class RemovePage(private val plugin: ExamplePlugin) : CommandExecutor {

    init {
        plugin.getCommand("removepage")?.setExecutor(this)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        plugin.hologram?.removePage(args[0].toInt())
        sender.sendMessage("Removed")
        return true
    }
}