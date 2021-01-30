package dev.jaims.hololib.core.error

/**
 * Thrown when the armor stand is not found.
 */
class StandNotFoundException(override val message: String) : Throwable(message)