package dev.jaims.hololib.example.command

import dev.jaims.hololib.example.ExamplePlugin
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class NextPageCommand(private val plugin: ExamplePlugin) : CommandExecutor {

    init {
        plugin.getCommand("nextpage")?.setExecutor(this)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        plugin.hologram?.showNextPage(sender as Player) ?: return true
        sender.sendMessage("Next Page Shown.")
        return true
    }

}