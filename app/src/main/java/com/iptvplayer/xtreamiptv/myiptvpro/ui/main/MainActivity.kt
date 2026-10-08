package com.iptvplayer.xtreamiptv.myiptvpro.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.container
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ActivityMainBinding
import com.iptvplayer.xtreamiptv.myiptvpro.domain.model.ChannelItem
import com.iptvplayer.xtreamiptv.myiptvpro.ui.common.ChannelActionHandler
import com.iptvplayer.xtreamiptv.myiptvpro.ui.common.Sheets
import com.iptvplayer.xtreamiptv.myiptvpro.ui.player.PlayerActivity
import com.iptvplayer.xtreamiptv.myiptvpro.ui.player.PlayerSource
import com.iptvplayer.xtreamiptv.myiptvpro.ui.playlist.AddPlaylistActivity
import com.iptvplayer.xtreamiptv.myiptvpro.ui.search.SearchActivity
import com.iptvplayer.xtreamiptv.myiptvpro.utils.Messages
import com.iptvplayer.xtreamiptv.myiptvpro.utils.applySystemBarInsets
import com.iptvplayer.xtreamiptv.myiptvpro.utils.enableAppEdgeToEdge

/** Hosts the four tabs (Playlist, Channel, Recent, Favorite) and the centre "+" button. */
class MainActivity : AppCompatActivity() {

    enum class Tab { PLAYLIST, CHANNEL, RECENT, FAVORITE }

    private lateinit var binding: ActivityMainBinding
    private var selectedTab = Tab.PLAYLIST

    /** Shared by every tab so favorites / more / edit / delete behave the same everywhere. */
    val actions: ChannelActionHandler by lazy {
        ChannelActionHandler(
            activity = this,
            channels = container.channelRepository,
            playlists = container.playlistRepository,
            messageAnchor = { binding.root },
            messageAbove = { binding.bottomBar },
            onNeedPlaylist = { openAddPlaylist() },
        )
    }

    private val addPlaylist = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val message = result.data?.getStringExtra(AddPlaylistActivity.EXTRA_MESSAGE) ?: return@registerForActivityResult
        select(Tab.PLAYLIST)
        Messages.show(binding.root, message, above = binding.bottomBar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableAppEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.mainRoot.applySystemBarInsets(top = true, bottom = false)
        binding.bottomBarContainer.applySystemBarInsets(top = false, bottom = true, sides = false)
        // The content area ends at the top edge of the bar (its height varies with the system navigation inset),
        // so the last list item can always be scrolled fully above the bar.
        binding.bottomBarContainer.addOnLayoutChangeListener { v, _, _, _, _, _, _, _, _ ->
            val clearance = v.height - binding.bottomBar.top
            if (binding.fragmentContainer.paddingBottom != clearance) binding.fragmentContainer.updatePadding(bottom = clearance)
        }

        selectedTab = savedInstanceState?.getString(STATE_TAB)?.let(Tab::valueOf) ?: Tab.PLAYLIST
        binding.navPlaylist.setOnClickListener { select(Tab.PLAYLIST) }
        binding.navChannel.setOnClickListener { select(Tab.CHANNEL) }
        binding.navRecent.setOnClickListener { select(Tab.RECENT) }
        binding.navFavorite.setOnClickListener { select(Tab.FAVORITE) }
        binding.btnAdd.setOnClickListener {
            Sheets.addMenu(this, onAddPlaylist = { openAddPlaylist() }, onAddChannel = { actions.addChannel() })
        }
        select(selectedTab, force = savedInstanceState == null)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_TAB, selectedTab.name)
    }

    /** Opens Add Playlist; [startOnFile] lands on the "Upload File" tab. */
    fun openAddPlaylist(startOnFile: Boolean = false) {
        addPlaylist.launch(
            Intent(this, AddPlaylistActivity::class.java).putExtra(AddPlaylistActivity.EXTRA_START_ON_FILE, startOnFile),
        )
    }

    fun openSearch(scope: SearchActivity.Scope) = startActivity(SearchActivity.intent(this, scope))

    fun openPlayer(source: PlayerSource, channel: ChannelItem) =
        startActivity(PlayerActivity.intent(this, source, channel))

    fun goToTab(tab: Tab) = select(tab)

    /** The view toasts are shown on / above. */
    val messageAnchor: View get() = binding.root
    val messageAbove: View get() = binding.bottomBar

    private fun select(tab: Tab, force: Boolean = false) {
        val changed = tab != selectedTab
        selectedTab = tab
        renderNav(binding.navPlaylist, binding.navPlaylistDot, tab == Tab.PLAYLIST)
        renderNav(binding.navChannel, binding.navChannelDot, tab == Tab.CHANNEL)
        renderNav(binding.navRecent, binding.navRecentDot, tab == Tab.RECENT)
        renderNav(binding.navFavorite, binding.navFavoriteDot, tab == Tab.FAVORITE)
        if (changed || force) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.fragmentContainer, createFragment(tab), tab.name)
            }
        }
    }

    private fun renderNav(item: View, dot: View, selected: Boolean) {
        item.isSelected = selected
        dot.alpha = if (selected) 1f else 0f
    }

    private fun createFragment(tab: Tab): Fragment = when (tab) {
        Tab.PLAYLIST -> PlaylistsFragment()
        Tab.CHANNEL -> ChannelsFragment()
        Tab.RECENT -> CollectionFragment.newInstance(CollectionMode.RECENT)
        Tab.FAVORITE -> CollectionFragment.newInstance(CollectionMode.FAVORITE)
    }

    private companion object {
        const val STATE_TAB = "selected_tab"
    }
}
