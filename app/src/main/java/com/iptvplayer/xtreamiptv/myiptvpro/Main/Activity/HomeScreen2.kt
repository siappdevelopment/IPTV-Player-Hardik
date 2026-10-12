package com.iptvplayer.xtreamiptv.myiptvpro.Main.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.container
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.domain.model.ChannelItem
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.ui.common.ChannelActionHandler
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.ui.common.Sheets
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.ui.main.ChannelsFragment
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.ui.main.CollectionFragment
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.ui.main.CollectionMode
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.ui.main.Tab
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.ui.main.MainHost
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.ui.main.PlaylistsFragment
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.ui.player.PlayerActivity
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.ui.player.PlayerSource
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.ui.playlist.AddPlaylistActivity
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.ui.search.SearchActivity
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.utils.Messages
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.utils.applySystemBarInsets
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.utils.enableAppEdgeToEdge
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppControlPreference
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppControllerManege
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.ExitDialogViewAd
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.InterstitialAdScreen
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ActivityHomeScreenTwoBinding

/**
 * Main screen: hosts the four IPTV tabs (Playlist, Channel, Recent, Favorite) and the centre "+" button
 * (formerly MainActivity), plus the launcher's exit-dialog / double-tap-to-exit back handling.
 */
class HomeScreen2 : BaseActivity(), MainHost {

    private lateinit var binding: ActivityHomeScreenTwoBinding
    private var selectedTab = Tab.PLAYLIST
    private var isFlag = false

    /** Shared by every tab so favorites / more / edit / delete behave the same everywhere. */
    override val actions: ChannelActionHandler by lazy {
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
        binding = ActivityHomeScreenTwoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // The ad is the bottom-most view, so the nav-bar inset goes on the root, not on the tab bar above the ad.
        binding.main.applySystemBarInsets(top = true, bottom = true)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = backClick()
        })

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

    override fun onDestroy() {
        super.onDestroy()
        AppControllerManege.removeListener()
    }

    /** Opens Add Playlist; [startOnFile] lands on the "Upload File" tab. */
    override fun openAddPlaylist(startOnFile: Boolean) {
        InterstitialAdScreen.reso__Fullscreen_ADS_Display(
            this@HomeScreen2,
            object : InterstitialAdScreen.OnCompeteAds {
                override fun onCompeteAds(b: Boolean) {
                    addPlaylist.launch(
                        Intent(
                            this@HomeScreen2,
                            AddPlaylistActivity::class.java
                        ).putExtra(
                            AddPlaylistActivity.EXTRA_START_ON_FILE,
                            startOnFile
                        )
                    )
                }
            },
            false
        )
    }

    override fun openSearch(scope: SearchActivity.Scope) = startActivity(SearchActivity.intent(this, scope))

    override fun openPlayer(source: PlayerSource, channel: ChannelItem) {
        InterstitialAdScreen.reso__Fullscreen_ADS_Display(
            this@HomeScreen2,
            object : InterstitialAdScreen.OnCompeteAds {
                override fun onCompeteAds(b: Boolean) {
                    startActivity(PlayerActivity.intent(this@HomeScreen2, source, channel))
                }
            },
            false
        )
    }

    override fun goToTab(tab: Tab) = select(tab)

    /** The view toasts are shown on / above. */
    override val messageAnchor: View get() = binding.root
    override val messageAbove: View get() = binding.bottomBar

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

    override fun onResume() {
        super.onResume()
        overridePendingTransition(0, 0)
    }

    private fun backClick() {
        if (AppControlPreference.reso__is_Exit_Dialog()) {
            ExitDialogViewAd.reso__App_Exit_Dialog(this)
        } else if (isFlag) {
            finishAffinity()
        } else {
            isFlag = true
            Toast.makeText(this, "Please tap again!", Toast.LENGTH_SHORT).show()
            Handler(Looper.getMainLooper()).postDelayed({ isFlag = false }, 3000)
        }
    }

    private companion object {
        const val STATE_TAB = "selected_tab"
    }
}
