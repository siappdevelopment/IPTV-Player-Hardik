package com.iptvplayer.xtreamiptv.myiptvpro.IPTV.ui.playlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.container
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.data.local.entity.PlaylistEntity
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.data.prefs.ViewMode
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.ui.common.ChannelActionHandler
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.ui.common.ChannelAdapter
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.ui.common.ChannelCallbacks
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.ui.common.ViewToggle
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.ui.player.PlayerActivity
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.ui.player.PlayerSource
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ActivityPlaylistDetailsBinding
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.ui.search.SearchActivity
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.utils.applySystemBarInsets
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.utils.enableAppEdgeToEdge
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.utils.show
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.utils.vmFactory
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.AppControlPreference
import com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege.InterstitialAdScreen
import kotlinx.coroutines.launch

/** Channels of one playlist. */
class PlaylistDetailsActivity : AppCompatActivity() {

    @Suppress("DEPRECATION")
    override fun onResume() {
        super.onResume()
        overridePendingTransition(0, 0)
    }

    private lateinit var binding: ActivityPlaylistDetailsBinding
    private val playlistId by lazy { intent.getLongExtra(EXTRA_PLAYLIST_ID, -1L) }
    private var playlistName: String = ""

    private val viewModel: PlaylistDetailsViewModel by viewModels {
        vmFactory {
            PlaylistDetailsViewModel(
                playlistId,
                container.channelRepository,
                container.playlistRepository,
                container.preferences,
            )
        }
    }

    private val actions by lazy {
        ChannelActionHandler(
            activity = this,
            channels = container.channelRepository,
            playlists = container.playlistRepository,
            messageAnchor = { binding.root },
        )
    }

    private val adapter by lazy {
        ChannelAdapter(
            ChannelCallbacks(
                onOpen = {
                    InterstitialAdScreen.reso__Fullscreen_ADS_Display(
                        this@PlaylistDetailsActivity,
                        object : InterstitialAdScreen.OnCompeteAds {
                            override fun onCompeteAds(b: Boolean) {
                                startActivity(
                                    PlayerActivity.intent(
                                        this@PlaylistDetailsActivity,
                                        PlayerSource.Playlist(playlistId, playlistName),
                                        it
                                    )
                                )
                            }
                        },
                        false
                    )
                },
                onFavorite = actions::toggleFavorite,
                onMore = { actions.showActions(it) },
            ),
            adEveryRows = AppControlPreference.getAd_After_Row(),
        )
    }

    private lateinit var toggle: ViewToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        enableAppEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.detailsRoot.applySystemBarInsets()

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    backClick()
                }
            }
        )

        binding.btnBack.setOnClickListener { backClick() }

        binding.btnSearch.setOnClickListener {
            InterstitialAdScreen.reso__Fullscreen_ADS_Display(
                this@PlaylistDetailsActivity,
                object : InterstitialAdScreen.OnCompeteAds {
                    override fun onCompeteAds(b: Boolean) {
                        startActivity(
                            SearchActivity.intent(
                                this@PlaylistDetailsActivity,
                                SearchActivity.Scope.Playlist(playlistId, playlistName)
                            )
                        )
                    }
                },
                false
            )
        }
        binding.btnEmptyAdd.setOnClickListener { actions.addChannel(playlistId) }

        val span = resources.getInteger(R.integer.grid_span)
        adapter.gridSpan = span
        val lm = GridLayoutManager(this, span)
        lm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int) =
                if (adapter.mode == ViewMode.GRID && !adapter.isAd(position)) 1 else span
        }
        binding.rvChannels.layoutManager = lm
        binding.rvChannels.adapter = adapter
        binding.rvChannels.itemAnimator = null
        toggle = ViewToggle(
            binding.viewToggle,
            listOf(ViewMode.GRID, ViewMode.LIST),
            viewModel::setViewMode
        )

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.ui.collect { ui -> if (ui != null) render(ui) }
            }
        }
    }

    private fun render(ui: DetailsUi) {
        val playlist = ui.playlist
        if (playlist == null) {
            finish()
            return
        }
        playlistName = playlist.name
        binding.tvTitle.text = playlist.name
        val count = resources.getQuantityString(R.plurals.channel_count, ui.channels.size, ui.channels.size)
        val source = getString(
            if (playlist.sourceType == PlaylistEntity.SOURCE_URL) R.string.source_imported_url else R.string.source_imported_file,
        )
        binding.tvSub.text = getString(R.string.details_subtitle, count, source)

        val has = ui.channels.isNotEmpty()
        binding.sectionRow.show(has)
        binding.rvChannels.show(has)
        binding.emptyState.show(!has)
        toggle.select(ui.viewMode)
        adapter.mode = ui.viewMode
        adapter.submitList(ui.channels)
    }

    companion object {
        private const val EXTRA_PLAYLIST_ID = "playlist_id"

        fun intent(context: Context, playlistId: Long) =
            Intent(context, PlaylistDetailsActivity::class.java).putExtra(EXTRA_PLAYLIST_ID, playlistId)
    }

    private fun backClick() {
        InterstitialAdScreen.reso__Back_Fullscreen_ADS_Display(
            this,
            object : InterstitialAdScreen.OnCompeteAds {
                override fun onCompeteAds(b: Boolean) {
                    finish()
                }
            }
        )
    }
}
