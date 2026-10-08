package com.iptvplayer.xtreamiptv.myiptvpro.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iptvplayer.xtreamiptv.myiptvpro.data.local.entity.PlaylistEntity
import com.iptvplayer.xtreamiptv.myiptvpro.data.prefs.AppPreferences
import com.iptvplayer.xtreamiptv.myiptvpro.data.prefs.ViewMode
import com.iptvplayer.xtreamiptv.myiptvpro.data.repository.DefaultPlaylistSeeder
import com.iptvplayer.xtreamiptv.myiptvpro.data.repository.PlaylistRepository
import com.iptvplayer.xtreamiptv.myiptvpro.domain.model.PlaylistItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SourceFilter { ALL, URL, FILE }

data class PlaylistsUi(
    /** Playlists after the source filter. */
    val playlists: List<PlaylistItem>,
    val totalPlaylists: Int,
    val urlCount: Int,
    val fileCount: Int,
    val totalChannels: Int,
    val filter: SourceFilter,
    val viewMode: ViewMode,
    /** The default playlists are still being added. */
    val seeding: Boolean,
)

/** Activity-scoped so the filter and layout survive tab switches. */
class PlaylistsViewModel(
    private val playlists: PlaylistRepository,
    private val prefs: AppPreferences,
    seeder: DefaultPlaylistSeeder,
) : ViewModel() {

    private val _filter = MutableStateFlow(SourceFilter.ALL)
    private val _viewMode = MutableStateFlow(prefs.viewMode(SCREEN, ViewMode.GRID))

    val ui: StateFlow<PlaylistsUi?> = combine(
        playlists.observePlaylists(),
        _filter,
        _viewMode,
        seeder.running,
    ) { all, filter, mode, seeding ->
        val urlCount = all.count { it.sourceType == PlaylistEntity.SOURCE_URL }
        PlaylistsUi(
            playlists = all.filter { playlist ->
                when (filter) {
                    SourceFilter.ALL -> true
                    SourceFilter.URL -> playlist.sourceType == PlaylistEntity.SOURCE_URL
                    SourceFilter.FILE -> playlist.sourceType == PlaylistEntity.SOURCE_FILE
                }
            },
            totalPlaylists = all.size,
            urlCount = urlCount,
            fileCount = all.size - urlCount,
            totalChannels = all.sumOf { it.channelCount },
            filter = filter,
            viewMode = mode,
            seeding = seeding,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val filter: StateFlow<SourceFilter> = _filter.asStateFlow()

    fun setFilter(value: SourceFilter) {
        _filter.value = value
    }

    fun setViewMode(mode: ViewMode) {
        _viewMode.value = mode
        prefs.setViewMode(SCREEN, mode)
    }

    fun rename(id: Long, name: String) {
        viewModelScope.launch { playlists.renamePlaylist(id, name) }
    }

    fun delete(id: Long) {
        viewModelScope.launch { playlists.deletePlaylist(id) }
    }

    private companion object {
        const val SCREEN = "playlists"
    }
}
