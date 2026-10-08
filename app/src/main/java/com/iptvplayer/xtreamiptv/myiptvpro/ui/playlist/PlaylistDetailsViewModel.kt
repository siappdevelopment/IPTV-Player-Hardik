package com.iptvplayer.xtreamiptv.myiptvpro.ui.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iptvplayer.xtreamiptv.myiptvpro.data.prefs.AppPreferences
import com.iptvplayer.xtreamiptv.myiptvpro.data.prefs.ViewMode
import com.iptvplayer.xtreamiptv.myiptvpro.data.repository.ChannelRepository
import com.iptvplayer.xtreamiptv.myiptvpro.data.repository.PlaylistRepository
import com.iptvplayer.xtreamiptv.myiptvpro.domain.model.ChannelItem
import com.iptvplayer.xtreamiptv.myiptvpro.domain.model.PlaylistItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class DetailsUi(
    /** Null once the playlist was deleted. */
    val playlist: PlaylistItem?,
    val channels: List<ChannelItem>,
    val viewMode: ViewMode,
)

class PlaylistDetailsViewModel(
    playlistId: Long,
    channels: ChannelRepository,
    playlists: PlaylistRepository,
    private val prefs: AppPreferences,
) : ViewModel() {

    private val viewMode = MutableStateFlow(prefs.viewMode(SCREEN, ViewMode.GRID))

    val ui: StateFlow<DetailsUi?> = combine(
        playlists.observePlaylist(playlistId),
        channels.observeByPlaylist(playlistId),
        viewMode,
    ) { playlist, list, mode -> DetailsUi(playlist, list, mode) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun setViewMode(mode: ViewMode) {
        viewMode.value = mode
        prefs.setViewMode(SCREEN, mode)
    }

    private companion object {
        const val SCREEN = "details"
    }
}
