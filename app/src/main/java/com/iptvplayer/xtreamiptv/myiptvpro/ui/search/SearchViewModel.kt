package com.iptvplayer.xtreamiptv.myiptvpro.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iptvplayer.xtreamiptv.myiptvpro.data.repository.ChannelRepository
import com.iptvplayer.xtreamiptv.myiptvpro.data.repository.PlaylistRepository
import com.iptvplayer.xtreamiptv.myiptvpro.data.repository.SearchScope
import com.iptvplayer.xtreamiptv.myiptvpro.domain.model.ChannelItem
import com.iptvplayer.xtreamiptv.myiptvpro.domain.model.GROUP_UNKNOWN
import com.iptvplayer.xtreamiptv.myiptvpro.domain.model.PlaylistItem
import com.iptvplayer.xtreamiptv.myiptvpro.ui.common.Categories
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn

sealed interface SearchRow {
    data class Count(val total: Int) : SearchRow
    data class PlaylistResult(val playlist: PlaylistItem) : SearchRow
    data class ChannelResult(val channel: ChannelItem) : SearchRow
}

sealed interface SearchUi {
    /** Nothing typed yet. */
    data object Idle : SearchUi
    data class Results(val rows: List<SearchRow>) : SearchUi
    data object NoResults : SearchUi
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchViewModel(
    private val scope: SearchScope,
    private val channels: ChannelRepository,
    private val playlists: PlaylistRepository,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    /** Re-runs when favorites change so hearts stay current. */
    val ui: StateFlow<SearchUi> = combine(
        _query.debounce(DEBOUNCE_MS).map { it.trim() }.distinctUntilChanged(),
        channels.observeFavoriteUrls(),
    ) { q, _ -> q }
        .mapLatest { q ->
            if (q.isEmpty()) {
                SearchUi.Idle
            } else {
                val foundPlaylists = if (scope == SearchScope.All) playlists.searchPlaylists(q) else emptyList()
                val foundChannels = channels.search(scope, q)
                if (foundPlaylists.isEmpty() && foundChannels.isEmpty()) {
                    SearchUi.NoResults
                } else {
                    val rows = buildList {
                        add(SearchRow.Count(foundPlaylists.size + foundChannels.size))
                        foundPlaylists.forEach { add(SearchRow.PlaylistResult(it)) }
                        foundChannels.forEach { add(SearchRow.ChannelResult(it)) }
                    }
                    SearchUi.Results(rows)
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SearchUi.Idle)

    /** Category suggestions for the idle state, taken from the user's own library. */
    val suggestions: StateFlow<List<String>> = channels.observeGroupCounts().map { groups ->
        groups.flatMap { it.groupTitle.split(';') }
            .map { it.trim() }
            .filter { it.isNotEmpty() && it != GROUP_UNKNOWN }
            .distinct()
            .sortedBy { Categories.sortKey(it) }
            .take(MAX_SUGGESTIONS)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setQuery(value: String) {
        _query.value = value
    }

    private companion object {
        const val DEBOUNCE_MS = 200L
        const val MAX_SUGGESTIONS = 8
    }
}
