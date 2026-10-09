package com.iptvplayer.xtreamiptv.myiptvpro

import com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement.ADSAppManage
import com.iptvplayer.xtreamiptv.myiptvpro.utils.ActivityStackTracker
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import com.iptvplayer.xtreamiptv.myiptvpro.data.local.database.AppDatabase
import com.iptvplayer.xtreamiptv.myiptvpro.data.prefs.AppPreferences
import com.iptvplayer.xtreamiptv.myiptvpro.data.remote.PlaylistDownloader
import com.iptvplayer.xtreamiptv.myiptvpro.data.repository.ChannelRepository
import com.iptvplayer.xtreamiptv.myiptvpro.data.repository.DefaultPlaylistSeeder
import com.iptvplayer.xtreamiptv.myiptvpro.data.repository.PlaylistRepository

/** Hand-rolled dependency container (the app is small enough not to need a DI framework). */
class AppContainer(context: Context) {
    private val database = AppDatabase.create(context)
    val preferences = AppPreferences(context)
    val playlistRepository = PlaylistRepository(database, PlaylistDownloader(), context.contentResolver)
    val channelRepository = ChannelRepository(database)
    val defaultPlaylists = DefaultPlaylistSeeder(playlistRepository, preferences)
}

class IptvApplication : ADSAppManage() {
    val container: AppContainer by lazy { AppContainer(this) }

    override fun onCreate() {
        super.onCreate()
        ActivityStackTracker.register(this)
        // Adds the default playlists in the background; a no-op once they are all in the library.
        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch { container.defaultPlaylists.seedIfNeeded() }
    }
}

val Context.container: AppContainer
    get() = (applicationContext as IptvApplication).container
