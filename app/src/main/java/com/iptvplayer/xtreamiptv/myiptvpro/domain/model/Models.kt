package com.iptvplayer.xtreamiptv.myiptvpro.domain.model

const val GROUP_UNKNOWN = "Unknown"

/** Row of the playlist list: counts and the first two channel logos for the card artwork. */
data class PlaylistItem(
    val id: Long,
    val name: String,
    val sourceType: String,
    val source: String,
    val createdAt: Long,
    val channelCount: Int,
    val logo1: String?,
    val logo2: String?,
)

/** A channel as shown in every list (details, channel tab, recents, favorites, search, player). */
data class ChannelItem(
    val id: Long,
    val playlistId: Long?,
    val name: String,
    val groupTitle: String,
    val logoUrl: String?,
    val streamUrl: String,
    val isFavorite: Boolean,
    /** Only set for history rows: when the channel was last opened. */
    val playedAt: Long? = null,
) {
    /** Individual categories of the raw `A;B;C` group value. */
    val categories: List<String>
        get() = groupTitle.split(';').map { it.trim() }.filter { it.isNotEmpty() }

    /** Resolution tag found in the channel name, e.g. "(1080p)" → "1080p"; null when absent. */
    val quality: String?
        get() = QUALITY.find(name)?.groupValues?.get(1)

    /** Name without the resolution tag; falls back to the category for blank names. */
    val displayName: String
        get() = name.replace(QUALITY, "").trim().ifBlank { categories.firstOrNull() ?: groupTitle }

    /** "A · B" style line shown under the name. */
    val categoryLine: String
        get() = categories.joinToString(" · ").ifBlank { GROUP_UNKNOWN }

    private companion object {
        val QUALITY = Regex("""\s*[(\[]\s*(\d{3,4}[pi]|4K|8K|UHD|FHD|HD|SD)\s*[)\]]""", RegexOption.IGNORE_CASE)
    }
}

/** How many channels carry one raw group value (used to build category chips). */
data class GroupCount(val groupTitle: String, val total: Int)

/** A channel produced by the M3U parser, before it is stored. */
data class ParsedChannel(
    val name: String,
    val groupTitle: String,
    val logoUrl: String?,
    val streamUrl: String,
    val tvgId: String?,
)

/** Generic UI state used by the list screens. */
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data object Empty : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val messageRes: Int) : UiState<Nothing>
}
