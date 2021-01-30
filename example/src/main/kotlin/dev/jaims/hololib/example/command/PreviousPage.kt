package dev.jaims.hololib.example.command

import dev.jaims.hololib.example.ExamplePlugin
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PreviousPage(private val plugin: ExamplePlugin) : CommandExecutor {

    init {
        plugin.getCommand("previouspage")?.setExecutor(this)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        plugin.hologram?.showPreviousPage(sender as Player)
        sender.sendMessage("Previous!")
        return true
    }

}