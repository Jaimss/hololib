package dev.jaims.hololib.core.component

import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

internal val GSON_SERIALIZER = GsonComponentSerializer.gson()

internal val LEGACY_SERIALIZER = LegacyComponentSerializer.legacyAmpersand()
    .toBuilder()
    .hexColors()
    .extractUrls()
    .build()