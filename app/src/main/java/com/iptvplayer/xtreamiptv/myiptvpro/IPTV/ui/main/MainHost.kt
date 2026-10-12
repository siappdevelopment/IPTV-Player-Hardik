package com.iptvplayer.xtreamiptv.myiptvpro.IPTV.ui.main

import android.view.View
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.domain.model.ChannelItem
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.ui.common.ChannelActionHandler
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.ui.player.PlayerSource
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.ui.search.SearchActivity

/** What the tab fragments need from the activity hosting them (LeftFragment or HomeScreen2). */
interface MainHost {
    val actions: ChannelActionHandler
    val messageAnchor: View
    val messageAbove: View
    fun openAddPlaylist(startOnFile: Boolean = false)
    fun openSearch(scope: SearchActivity.Scope)
    fun openPlayer(source: PlayerSource, channel: ChannelItem)
    fun goToTab(tab: Tab)
}

/** The four tabs hosted by [MainHost]. */
enum class Tab { PLAYLIST, CHANNEL, RECENT, FAVORITE }
