package com.iptvplayer.xtreamiptv.myiptvpro.Main.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
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
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.FragmentLeftBinding

/**
 * Hosts the four IPTV tabs (Playlist, Channel, Recent, Favorite) and the centre "+" button.
 * The tab fragments live in the child fragment manager and find this fragment through [MainHost].
 */
class LeftFragment : Fragment(), MainHost {

    private var _binding: FragmentLeftBinding? = null
    private val binding get() = _binding!!
    private var selectedTab = Tab.PLAYLIST

    /** Shared by every tab so favorites / more / edit / delete behave the same everywhere. */
    override val actions: ChannelActionHandler by lazy {
        ChannelActionHandler(
            activity = requireActivity() as AppCompatActivity,
            channels = requireContext().container.channelRepository,
            playlists = requireContext().container.playlistRepository,
            messageAnchor = { binding.root },
            messageAbove = { binding.bottomBar },
            onNeedPlaylist = { openAddPlaylist() },
        )
    }

    private val addPlaylist = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val message = result.data?.getStringExtra(AddPlaylistActivity.EXTRA_MESSAGE) ?: return@registerForActivityResult
        val b = _binding ?: return@registerForActivityResult
        select(Tab.PLAYLIST)
        Messages.show(b.root, message, above = b.bottomBar)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLeftBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // The root's bg_color fills the status / navigation bar areas instead of the wallpaper showing through.
        binding.main.applySystemBarInsets(top = true, bottom = true, sides = false)
        selectedTab = savedInstanceState?.getString(STATE_TAB)?.let(Tab::valueOf) ?: Tab.PLAYLIST
        binding.navPlaylist.setOnClickListener { select(Tab.PLAYLIST) }
        binding.navChannel.setOnClickListener { select(Tab.CHANNEL) }
        binding.navRecent.setOnClickListener { select(Tab.RECENT) }
        binding.navFavorite.setOnClickListener { select(Tab.FAVORITE) }
        binding.btnAdd.setOnClickListener {
            Sheets.addMenu(requireContext(), onAddPlaylist = { openAddPlaylist() }, onAddChannel = { actions.addChannel() })
        }
        // Child fragments are restored by the FragmentManager; only the first creation adds one.
        select(selectedTab, force = savedInstanceState == null)
    }

    override fun onResume() {
        super.onResume()
        val window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireContext(), android.R.color.black)
        // Makes status bar icons/text WHITE
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_TAB, selectedTab.name)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /** Opens Add Playlist; [startOnFile] lands on the "Upload File" tab. */
    override fun openAddPlaylist(startOnFile: Boolean) {
        addPlaylist.launch(
            Intent(requireContext(), AddPlaylistActivity::class.java)
                .putExtra(AddPlaylistActivity.EXTRA_START_ON_FILE, startOnFile),
        )
    }

    override fun openSearch(scope: SearchActivity.Scope) =
        startActivity(SearchActivity.intent(requireContext(), scope))

    override fun openPlayer(source: PlayerSource, channel: ChannelItem) =
        startActivity(PlayerActivity.intent(requireContext(), source, channel))

    override fun goToTab(tab: Tab) = select(tab)

    /** The view toasts are shown on / above. */
    override val messageAnchor: View get() = binding.root
    override val messageAbove: View get() = binding.bottomBar

    private fun select(tab: Tab, force: Boolean = false) {
        val b = _binding ?: return
        val changed = tab != selectedTab
        selectedTab = tab
        renderNav(b.navPlaylist, b.navPlaylistDot, tab == Tab.PLAYLIST)
        renderNav(b.navChannel, b.navChannelDot, tab == Tab.CHANNEL)
        renderNav(b.navRecent, b.navRecentDot, tab == Tab.RECENT)
        renderNav(b.navFavorite, b.navFavoriteDot, tab == Tab.FAVORITE)
        if (changed || force) {
            childFragmentManager.commit {
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
