package com.iptvplayer.xtreamiptv.myiptvpro.ui.playlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.container
import com.iptvplayer.xtreamiptv.myiptvpro.data.local.entity.PlaylistEntity
import com.iptvplayer.xtreamiptv.myiptvpro.data.prefs.ViewMode
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ActivityPlaylistDetailsBinding
import com.iptvplayer.xtreamiptv.myiptvpro.ui.common.ChannelActionHandler
import com.iptvplayer.xtreamiptv.myiptvpro.ui.common.ChannelAdapter
import com.iptvplayer.xtreamiptv.myiptvpro.ui.common.ChannelCallbacks
import com.iptvplayer.xtreamiptv.myiptvpro.ui.common.ViewToggle
import com.iptvplayer.xtreamiptv.myiptvpro.ui.player.PlayerActivity
import com.iptvplayer.xtreamiptv.myiptvpro.ui.player.PlayerSource
import com.iptvplayer.xtreamiptv.myiptvpro.ui.search.SearchActivity
import com.iptvplayer.xtreamiptv.myiptvpro.utils.applySystemBarInsets
import com.iptvplayer.xtreamiptv.myiptvpro.utils.enableAppEdgeToEdge
import com.iptvplayer.xtreamiptv.myiptvpro.utils.show
import com.iptvplayer.xtreamiptv.myiptvpro.utils.vmFactory
import kotlinx.coroutines.launch

/** Channels of one playlist. */
class PlaylistDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistDetailsBinding
    private val playlistId by lazy { intent.getLongExtra(EXTRA_PLAYLIST_ID, -1L) }
    private var playlistName: String = ""

    private val viewModel: PlaylistDetailsViewModel by viewModels {
        vmFactory {
            PlaylistDetailsViewModel(
                playlistId, container.channelRepository, container.playlistRepository, container.preferences,
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
                onOpen = { startActivity(PlayerActivity.intent(this, PlayerSource.Playlist(playlistId, playlistName), it)) },
                onFavorite = actions::toggleFavorite,
                onMore = { actions.showActions(it) },
            ),
        )
    }

    private lateinit var toggle: ViewToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        enableAppEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.detailsRoot.applySystemBarInsets()

        binding.btnBack.setOnClickListener { finish() }
        binding.btnSearch.setOnClickListener {
            startActivity(SearchActivity.intent(this, SearchActivity.Scope.Playlist(playlistId, playlistName)))
        }
        binding.btnEmptyAdd.setOnClickListener { actions.addChannel(playlistId) }

        val span = resources.getInteger(R.integer.grid_span)
        val lm = GridLayoutManager(this, span)
        lm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int) = if (adapter.mode == ViewMode.GRID) 1 else span
        }
        binding.rvChannels.layoutManager = lm
        binding.rvChannels.adapter = adapter
        binding.rvChannels.itemAnimator = null
        toggle = ViewToggle(binding.viewToggle, listOf(ViewMode.GRID, ViewMode.LIST), viewModel::setViewMode)

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
}
