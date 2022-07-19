package dev.jaims.hololib.core.component

import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

val GSON_SERIALIZER = GsonComponentSerializer.gson()

val LEGACY_SERIALIZER = LegacyComponentSerializer.legacyAmpersand()
    .toBuilder()
    .hexColors()
    .extractUrls()
    .build()