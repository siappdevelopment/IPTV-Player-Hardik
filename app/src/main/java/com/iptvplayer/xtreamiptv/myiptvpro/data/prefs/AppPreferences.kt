package com.iptvplayer.xtreamiptv.myiptvpro.data.prefs

import android.content.Context
import androidx.core.content.edit
import com.iptvplayer.xtreamiptv.myiptvpro.domain.model.ChannelItem

/** How a list is laid out. Persisted per screen. */
enum class ViewMode { CARDS, GRID, LIST }

/** Small key-value store for settings that are not part of the library database. */
class AppPreferences(context: Context) {
    private val sp = context.applicationContext.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    var onboardingDone: Boolean
        get() = sp.getBoolean(KEY_ONBOARDING, false)
        set(value) = sp.edit { putBoolean(KEY_ONBOARDING, value) }

    var languageCode: String
        get() = sp.getString(KEY_LANGUAGE, "en") ?: "en"
        set(value) = sp.edit { putString(KEY_LANGUAGE, value) }

    /** Reopen the channel that was playing when the app was last used. */
    var resumeLastChannel: Boolean
        get() = sp.getBoolean(KEY_RESUME, false)
        set(value) = sp.edit { putBoolean(KEY_RESUME, value) }

    /** When off, software decoders are preferred. */
    var hardwareDecoding: Boolean
        get() = sp.getBoolean(KEY_HW, true)
        set(value) = sp.edit { putBoolean(KEY_HW, value) }

    fun viewMode(screen: String, default: ViewMode): ViewMode =
        runCatching { ViewMode.valueOf(sp.getString(KEY_VIEW + screen, null) ?: default.name) }.getOrDefault(default)

    fun setViewMode(screen: String, mode: ViewMode) = sp.edit { putString(KEY_VIEW + screen, mode.name) }

    fun saveLastChannel(item: ChannelItem, playlistId: Long?) = sp.edit {
        putString(KEY_LAST_URL, item.streamUrl)
        putString(KEY_LAST_NAME, item.name)
        putString(KEY_LAST_GROUP, item.groupTitle)
        putString(KEY_LAST_LOGO, item.logoUrl)
        putLong(KEY_LAST_ID, item.id)
        putLong(KEY_LAST_PLAYLIST, playlistId ?: -1L)
    }

    /** The channel saved by [saveLastChannel], or null. */
    fun lastChannel(): Pair<ChannelItem, Long?>? {
        val url = sp.getString(KEY_LAST_URL, null) ?: return null
        val playlist = sp.getLong(KEY_LAST_PLAYLIST, -1L).takeIf { it >= 0 }
        val item = ChannelItem(
            id = sp.getLong(KEY_LAST_ID, 0L),
            playlistId = playlist,
            name = sp.getString(KEY_LAST_NAME, "").orEmpty(),
            groupTitle = sp.getString(KEY_LAST_GROUP, "").orEmpty(),
            logoUrl = sp.getString(KEY_LAST_LOGO, null),
            streamUrl = url,
            isFavorite = false,
        )
        return item to playlist
    }

    /** Whether the default playlist [key] has been added (or adopted) on this install. */
    fun isDefaultPlaylistAdded(key: String): Boolean = sp.getBoolean(KEY_DEFAULT + key, false)

    fun markDefaultPlaylistAdded(key: String) = sp.edit { putBoolean(KEY_DEFAULT + key, true) }

    fun clearLastChannel() = sp.edit {
        remove(KEY_LAST_URL); remove(KEY_LAST_NAME); remove(KEY_LAST_GROUP)
        remove(KEY_LAST_LOGO); remove(KEY_LAST_ID); remove(KEY_LAST_PLAYLIST)
    }

    private companion object {
        const val FILE = "app_prefs"
        const val KEY_ONBOARDING = "onboarding_done"
        const val KEY_LANGUAGE = "language"
        const val KEY_RESUME = "resume_last"
        const val KEY_HW = "hardware_decoding"
        const val KEY_VIEW = "view_"
        const val KEY_DEFAULT = "default_playlist_"
        const val KEY_LAST_URL = "last_url"
        const val KEY_LAST_NAME = "last_name"
        const val KEY_LAST_GROUP = "last_group"
        const val KEY_LAST_LOGO = "last_logo"
        const val KEY_LAST_ID = "last_id"
        const val KEY_LAST_PLAYLIST = "last_playlist"
    }
}
