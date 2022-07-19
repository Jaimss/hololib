package dev.jaims.hololib.example

import dev.jaims.hololib.core.Hologram
import dev.jaims.hololib.core.HololibManager
import dev.jaims.hololib.example.command.*
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.plugin.java.JavaPlugin

class ExamplePlugin : JavaPlugin() {

    // This is a bad way to do this, i just didn't wanna make a whole storage system for my example.
    var hologram: Hologram? = null

    override fun onEnable() {
        logger.info("Starting Example Holograms Plugin")

        val miniMessage = MiniMessage.miniMessage()

        val m = HololibManager(this)
        m.lineTransformation = { player, content -> miniMessage.deserialize(content) }

        AddPage(this)
        CreateHologram(this)
        NextPageCommand(this)
        PreviousPage(this)
        RemovePage(this)
    }
}