package com.iptvplayer.xtreamiptv.myiptvpro.IPTV

import android.content.Context
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.data.local.database.AppDatabase
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.data.prefs.AppPreferences
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.data.remote.PlaylistDownloader
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.data.repository.ChannelRepository
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.data.repository.DefaultPlaylistSeeder
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.data.repository.PlaylistRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Hand-rolled dependency container (the app is small enough not to need a DI framework).
 * Self-managed singleton: independent of the Application class, created lazily on first use.
 */
class AppContainer private constructor(context: Context) {
    private val database = AppDatabase.create(context)
    val preferences = AppPreferences(context)
    val playlistRepository =
        PlaylistRepository(database, PlaylistDownloader(), context.contentResolver)
    val channelRepository = ChannelRepository(database)
    val defaultPlaylists = DefaultPlaylistSeeder(playlistRepository, preferences)

    init {
        // Adds the default playlists in the background; a no-op once they are all in the library.
        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch { defaultPlaylists.seedIfNeeded() }
    }

    companion object {
        @Volatile
        private var instance: AppContainer? = null

        fun get(context: Context): AppContainer =
            instance ?: synchronized(this) {
                instance ?: AppContainer(context.applicationContext).also { instance = it }
            }
    }
}

val Context.container: AppContainer
    get() = AppContainer.get(this)
