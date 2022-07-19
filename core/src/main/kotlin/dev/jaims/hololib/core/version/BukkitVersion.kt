package dev.jaims.hololib.core.version

data class BukkitVersion(
    val version: String
) : Comparable<BukkitVersion> {

    override fun compareTo(other: BukkitVersion): Int {
        // check major
        if (major() > other.major()) return 1
        if (major() < other.major()) return -1

        // check minor
        if (minor() > other.minor()) return 1
        if (minor() < other.minor()) return -1

        // check patch
        val patch = patch()
        val otherPatch = other.patch()
        if (patch == null) {
            if (otherPatch != null) return -1
            return 0
        }
        // patch can't be null here
        if (otherPatch == null) return 1
        if (patch > otherPatch) return 1
        if (patch < otherPatch) return -1
        if (patch == otherPatch) return 0
        error("Impossible!")
    }

}

val REVISION_SNAPSHOT_REGEX = "-R\\d+.\\d+-SNAPSHOT".toRegex()
val VERSION_REGEX = "(\\d+).(\\d+)(.\\d+)?".toRegex()

fun String.trimmedVersion() = replace(REVISION_SNAPSHOT_REGEX) {
    it.value.replace(it.value, "")
}

fun BukkitVersion.major() = VERSION_REGEX.find(this.version.trimmedVersion())!!.groupValues[1].toInt()

fun BukkitVersion.minor() = VERSION_REGEX.find(this.version.trimmedVersion())!!.groupValues[2].toInt()

fun BukkitVersion.patch(): Int? {
    val values = VERSION_REGEX.find(this.version.trimmedVersion())!!.groupValues
    return values.getOrNull(3)?.replace(".", "")?.toIntOrNull()
}