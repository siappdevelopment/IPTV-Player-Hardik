package com.iptvplayer.xtreamiptv.myiptvpro.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iptvplayer.xtreamiptv.myiptvpro.data.prefs.AppPreferences
import com.iptvplayer.xtreamiptv.myiptvpro.data.prefs.ViewMode
import com.iptvplayer.xtreamiptv.myiptvpro.data.repository.ChannelRepository
import com.iptvplayer.xtreamiptv.myiptvpro.data.repository.PlaylistRepository
import com.iptvplayer.xtreamiptv.myiptvpro.domain.model.ChannelItem
import com.iptvplayer.xtreamiptv.myiptvpro.domain.model.GROUP_UNKNOWN
import com.iptvplayer.xtreamiptv.myiptvpro.domain.model.GroupCount
import com.iptvplayer.xtreamiptv.myiptvpro.ui.common.CategoryChip
import com.iptvplayer.xtreamiptv.myiptvpro.ui.common.Categories
import com.iptvplayer.xtreamiptv.myiptvpro.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class ChannelsUi(
    val totalChannels: Int,
    val playlistCount: Int,
    /** Most recently watched channel ("Continue watching"), or null. */
    val hero: ChannelItem?,
    val chips: List<CategoryChip>,
    val selected: String,
    val channels: List<ChannelItem>,
    val viewMode: ViewMode,
)

/** Channel tab: every channel of every playlist, filtered by one category. */
@OptIn(ExperimentalCoroutinesApi::class)
class ChannelsViewModel(
    channels: ChannelRepository,
    playlists: PlaylistRepository,
    private val prefs: AppPreferences,
) : ViewModel() {

    private val selected = MutableStateFlow("")
    private val viewMode = MutableStateFlow(prefs.viewMode(SCREEN, ViewMode.CARDS))

    private val chips = channels.observeGroupCounts().map(::buildChips)
    private val list = selected.flatMapLatest { channels.observeAll(it) }
    private val hero = channels.observeRecents().map { it.firstOrNull() }
    private val playlistCount = playlists.observePlaylists().map { it.size }

    val ui: StateFlow<ChannelsUi?> = combine(
        list, chips, playlistCount, hero, combine(selected, viewMode) { s, v -> s to v },
    ) { channelList, chipList, plCount, heroItem, (sel, mode) ->
        ChannelsUi(
            totalChannels = chipList.firstOrNull()?.count ?: 0,
            playlistCount = plCount,
            hero = heroItem,
            chips = chipList,
            selected = sel,
            channels = channelList,
            viewMode = mode,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun select(value: String) {
        selected.value = value
    }

    fun setViewMode(mode: ViewMode) {
        viewMode.value = mode
        prefs.setViewMode(SCREEN, mode)
    }

    /** "All" first, then known categories in design order, then the rest alphabetically. */
    private fun buildChips(groups: List<GroupCount>): List<CategoryChip> {
        val total = groups.sumOf { it.total }
        val perToken = LinkedHashMap<String, Int>()
        for (group in groups) {
            group.groupTitle.split(';').map { it.trim() }
                .filter { it.isNotEmpty() && it != GROUP_UNKNOWN }
                .distinct()
                .forEach { token -> perToken[token] = (perToken[token] ?: 0) + group.total }
        }
        val tokens = perToken.entries.sortedBy { Categories.sortKey(it.key) }.take(MAX_CHIPS)
        return buildList {
            add(CategoryChip(label = "", value = "", count = total, icon = R.drawable.ic_menu))
            tokens.forEach { add(CategoryChip(it.key, it.key, it.value, Categories.icon(it.key))) }
        }
    }

    private companion object {
        const val SCREEN = "channels"
        const val MAX_CHIPS = 60
    }
}
