package com.iptvplayer.xtreamiptv.myiptvpro.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iptvplayer.xtreamiptv.myiptvpro.data.prefs.AppPreferences
import com.iptvplayer.xtreamiptv.myiptvpro.data.prefs.ViewMode
import com.iptvplayer.xtreamiptv.myiptvpro.data.repository.ChannelRepository
import com.iptvplayer.xtreamiptv.myiptvpro.domain.model.ChannelItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

enum class CollectionMode { RECENT, FAVORITE }

data class CollectionUi(val items: List<ChannelItem>, val viewMode: ViewMode)

/** Recent and Favorite tabs share everything except where the list comes from. */
class CollectionViewModel(
    channels: ChannelRepository,
    private val prefs: AppPreferences,
    val mode: CollectionMode,
) : ViewModel() {

    private val screen = if (mode == CollectionMode.RECENT) "recent" else "favorites"
    private val viewMode = MutableStateFlow(prefs.viewMode(screen, ViewMode.GRID))

    private val source: Flow<List<ChannelItem>> = when (mode) {
        CollectionMode.RECENT -> channels.observeRecents()
        CollectionMode.FAVORITE -> channels.observeFavorites()
    }

    val ui: StateFlow<CollectionUi?> = combine(source, viewMode) { items, vm -> CollectionUi(items, vm) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun setViewMode(value: ViewMode) {
        viewMode.value = value
        prefs.setViewMode(screen, value)
    }
}
