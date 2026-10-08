package com.iptvplayer.xtreamiptv.myiptvpro.data.repository

import android.util.Log
import com.iptvplayer.xtreamiptv.myiptvpro.data.prefs.AppPreferences
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.cancellation.CancellationException

/** A playlist every install starts with. [key] is the stable id used for the "already added" flag. */
class DefaultPlaylist(val key: String, val name: String, val url: String)

/** The public iptv-org category playlists named in the R&D document, in display order. */
val DEFAULT_PLAYLISTS = listOf(
    DefaultPlaylist("family", "Family", "https://iptv-org.github.io/iptv/categories/family.m3u"),
    DefaultPlaylist("entertainment", "Entertainment", "https://iptv-org.github.io/iptv/categories/entertainment.m3u"),
    DefaultPlaylist("movies", "Movies", "https://iptv-org.github.io/iptv/categories/movies.m3u"),
    DefaultPlaylist("sport", "Sport", "https://iptv-org.github.io/iptv/categories/sports.m3u"),
    DefaultPlaylist("lifestyle", "Lifestyle", "https://iptv-org.github.io/iptv/categories/lifestyle.m3u"),
)

/**
 * Adds [DEFAULT_PLAYLISTS] through the normal link import (download, parse, Room), one at a time and in order,
 * so they behave exactly like playlists the user imported.
 *
 * - Each playlist is added once: its key is remembered in preferences after a successful import, so a
 *   playlist the user later deletes does not come back, and a playlist whose link is already in the
 *   library (imported by hand) is adopted instead of duplicated.
 * - An import stores a playlist and its channels in one transaction, so a closed app, a dead process or a
 *   network error leaves nothing half-written; the playlist is simply tried again on the next start.
 * - A failure stops the run so that the remaining playlists are not added ahead of the failed one.
 */
class DefaultPlaylistSeeder(
    private val playlists: PlaylistRepository,
    private val prefs: AppPreferences,
) {
    private val lock = Mutex()
    private val _running = MutableStateFlow(false)

    /** True while at least one default playlist is still being added. */
    val running: StateFlow<Boolean> = _running.asStateFlow()

    suspend fun seedIfNeeded() {
        if (DEFAULT_PLAYLISTS.all { prefs.isDefaultPlaylistAdded(it.key) }) return
        lock.withLock {
            val pending = DEFAULT_PLAYLISTS.filterNot { prefs.isDefaultPlaylistAdded(it.key) }
            if (pending.isEmpty()) return
            _running.value = true
            try {
                for (default in pending) {
                    if (playlists.hasUrlPlaylist(default.url)) {
                        prefs.markDefaultPlaylistAdded(default.key)
                        continue
                    }
                    try {
                        withTimeout(IMPORT_TIMEOUT_MS) { playlists.importFromUrl(default.name, default.url) }
                        prefs.markDefaultPlaylistAdded(default.key)
                    } catch (e: TimeoutCancellationException) {
                        Log.w(TAG, "Default playlist ${default.key} timed out, will retry on next start", e)
                        return
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        Log.w(TAG, "Default playlist ${default.key} not added, will retry on next start", e)
                        return
                    }
                }
            } finally {
                _running.value = false
            }
        }
    }

    private companion object {
        const val TAG = "DefaultPlaylists"
        const val IMPORT_TIMEOUT_MS = 120_000L
    }
}
