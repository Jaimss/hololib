package dev.jaims.hololib.core.test.version

import dev.jaims.hololib.core.version.*
import org.junit.Test
import kotlin.test.assertEquals

class BukkitVersionTest {

    @Test
    fun `test that removing snapshot works`() {
        assertEquals("1.19", "1.19-R0.1-SNAPSHOT".trimmedVersion())
        assertEquals("1.19", "1.19-R1.2-SNAPSHOT".trimmedVersion())
        assertEquals("1.19", "1.19-R0.2-SNAPSHOT".trimmedVersion())
        assertEquals("1.19", "1.19-R0.23-SNAPSHOT".trimmedVersion())
        assertEquals("1.19.1", "1.19.1-R0.23-SNAPSHOT".trimmedVersion())
    }

    @Test
    fun `test major`() {
        assertEquals(1, BukkitVersion("1.2-RO.1-SNAPSHOT").major())
        assertEquals(1, BukkitVersion("1.2.3-RO.1-SNAPSHOT").major())
        assertEquals(2, BukkitVersion("2.2.3-RO.1-SNAPSHOT").major())
    }

    @Test
    fun `test minor`() {
        assertEquals(19, BukkitVersion("1.19-RO.1-SNAPSHOT").minor())
        assertEquals(17, BukkitVersion("1.17.3-RO.1-SNAPSHOT").minor())
        assertEquals(8, BukkitVersion("2.8.3-RO.1-SNAPSHOT").minor())
    }

    @Test
    fun `test patch`() {
        assertEquals(null, BukkitVersion("1.18-RO.1-SNAPSHOT").patch())
        assertEquals(3, BukkitVersion("2.8.3-RO.1-SNAPSHOT").patch())
        assertEquals(2, BukkitVersion("1.18.2-RO.1-SNAPSHOT").patch())
    }

    @Test
    fun `test comparing of bukkit versions`() {
        //assert(BukkitVersion("1.19-R0.1-SNAPSHOT") > BukkitVersion("1.18.2-R0.1-SNAPSHOT"))
        assert(BukkitVersion("1.19-R0.1-SNAPSHOT") == BukkitVersion("1.19-R0.1-SNAPSHOT"))
        assert(BukkitVersion("1.18.2-R0.1-SNAPSHOT") > BukkitVersion("1.18.1-R0.1-SNAPSHOT"))
        assert(BukkitVersion("1.17.2-R0.1-SNAPSHOT") < BukkitVersion("1.18.1-R0.1-SNAPSHOT"))
    }

}