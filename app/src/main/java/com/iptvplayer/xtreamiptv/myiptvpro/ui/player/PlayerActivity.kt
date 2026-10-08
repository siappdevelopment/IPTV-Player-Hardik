package com.iptvplayer.xtreamiptv.myiptvpro.ui.player

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.container
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ActivityPlayerBinding
import com.iptvplayer.xtreamiptv.myiptvpro.domain.model.ChannelItem
import com.iptvplayer.xtreamiptv.myiptvpro.utils.applySystemBarInsets
import com.iptvplayer.xtreamiptv.myiptvpro.utils.enableAppEdgeToEdge
import com.iptvplayer.xtreamiptv.myiptvpro.utils.show
import com.iptvplayer.xtreamiptv.myiptvpro.utils.vmFactory
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * Video on top and the channel queue below (portrait), or edge-to-edge video with big controls
 * (landscape = fullscreen). The ExoPlayer lives in [PlayerViewModel], so rotating never restarts playback.
 */
@OptIn(UnstableApi::class)
class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    private val source: PlayerSource by lazy { PlayerSource.readFrom(intent) }
    private val startChannel: ChannelItem by lazy { readChannel() }

    private val viewModel: PlayerViewModel by viewModels {
        vmFactory {
            PlayerViewModel(
                application, container.channelRepository, container.playlistRepository,
                container.preferences, source, startChannel,
            )
        }
    }

    private val isLandscape: Boolean
        get() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    private val listAdapter by lazy { PlayerListAdapter(onPlay = viewModel::playAt, onFavorite = viewModel::toggleFavorite) }
    private val headerAdapter by lazy { PlayerHeaderAdapter(onFavorite = { viewModel.currentItem()?.let(viewModel::toggleFavorite) }) }

    private val handler = Handler(Looper.getMainLooper())
    private val hideControls = Runnable {
        controlsVisible = false
        renderOverlay()
    }

    private var controlsVisible = true
    private var locked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        enableAppEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locked = savedInstanceState?.getBoolean(STATE_LOCKED) ?: false
        controlsVisible = savedInstanceState?.getBoolean(STATE_CONTROLS) ?: true

        releaseForcedOrientationIfNeeded()
        applyWindowMode()
        setupViews()
        observe()
        renderOverlay()
        scheduleHide()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(STATE_LOCKED, locked)
        outState.putBoolean(STATE_CONTROLS, controlsVisible)
    }

    override fun onStart() {
        super.onStart()
        binding.playerView.player = viewModel.player
    }

    override fun onStop() {
        super.onStop()
        binding.playerView.player = null
        if (!isChangingConfigurations) viewModel.player.pause()
    }

    /**
     * A phone held still sends no sensor event, so a forced landscape would otherwise stick:
     * hand a portrait screen back to whatever is underneath.
     */
    override fun finish() {
        if (isLandscape) requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.finish()
    }

    override fun onDestroy() {
        handler.removeCallbacks(hideControls)
        super.onDestroy()
    }

    // --- window / orientation ---------------------------------------------------------------

    private fun releaseForcedOrientationIfNeeded() {
        if (!isLandscape && viewModel.orientationResetPending) {
            viewModel.orientationResetPending = false
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    private fun applyWindowMode() {
        val controller = WindowCompat.getInsetsController(window, binding.root)
        if (isLandscape) {
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller.hide(WindowInsetsCompat.Type.systemBars())
        } else {
            controller.show(WindowInsetsCompat.Type.systemBars())
            binding.playerRoot.applySystemBarInsets(bottom = false)
        }
    }

    private fun toggleFullscreen() {
        if (isLandscape) {
            viewModel.orientationResetPending = true
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        }
    }

    // --- views ------------------------------------------------------------------------------

    private fun setupViews() {
        binding.playerContainer.keepScreenOn = true
        binding.playerContainer.setOnClickListener { toggleControls() }
        binding.btnBack?.setOnClickListener { finish() }

        binding.rvChannels?.let { list ->
            list.layoutManager = LinearLayoutManager(this)
            list.adapter = ConcatAdapter(headerAdapter, listAdapter)
            list.itemAnimator = null
        }

        onBackPressedDispatcher.addCallback(this) {
            if (isLandscape) toggleFullscreen() else finish()
        }

        val o = binding.overlay
        o.btnPlayPause.setOnClickListener { viewModel.togglePlayPause(); scheduleHide() }
        o.btnNext.setOnClickListener { viewModel.next(); scheduleHide() }
        o.btnPrev.setOnClickListener { viewModel.previous(); scheduleHide() }
        o.btnErrorNext.setOnClickListener { viewModel.next() }
        o.btnErrorPrev.setOnClickListener { viewModel.previous() }
        o.btnRetry.setOnClickListener { viewModel.retry() }
        o.btnFavorite.setOnClickListener { viewModel.currentItem()?.let(viewModel::toggleFavorite); scheduleHide() }
        o.btnLock.setOnClickListener { setLocked(true) }
        o.btnUnlock.setOnClickListener { setLocked(false) }
        o.btnFullscreen.setOnClickListener { toggleFullscreen() }
        o.btnBackFull?.setOnClickListener { toggleFullscreen() }
        o.btnErrorExit?.setOnClickListener { toggleFullscreen() }
    }

    private fun setLocked(value: Boolean) {
        locked = value
        controlsVisible = !value
        renderOverlay()
        scheduleHide()
    }

    private fun toggleControls() {
        if (locked) return
        controlsVisible = !controlsVisible
        renderOverlay()
        scheduleHide()
    }

    private fun scheduleHide() {
        handler.removeCallbacks(hideControls)
        if (controlsVisible && !locked) handler.postDelayed(hideControls, CONTROLS_TIMEOUT_MS)
    }

    // --- state ------------------------------------------------------------------------------

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.items.collect(::onItems) }
                launch { viewModel.index.collect { renderHeader() } }
                launch { viewModel.label.collect { renderHeader() } }
                launch { viewModel.status.collect { renderOverlay(); renderHeader() } }
                launch { viewModel.playWhenReady.collect { renderPlayIcon(it) } }
                launch {
                    combine(viewModel.items, viewModel.index) { items, index -> items.getOrNull(index) }
                        .collect { renderCurrent(it) }
                }
            }
        }
    }

    private fun onItems(items: List<ChannelItem>) {
        listAdapter.submitList(items)
        renderHeader()
    }

    private fun renderCurrent(item: ChannelItem?) {
        listAdapter.setCurrent(item)
        val o = binding.overlay
        o.btnFavorite.isSelected = item?.isFavorite == true
        val index = viewModel.index.value
        val last = viewModel.items.value.lastIndex
        o.btnPrev.alpha = if (index > 0) 1f else DISABLED_ALPHA
        o.btnNext.alpha = if (index < last) 1f else DISABLED_ALPHA
        o.btnPrev.isEnabled = index > 0
        o.btnNext.isEnabled = index < last
        o.btnErrorPrev.alpha = o.btnPrev.alpha
        o.btnErrorNext.alpha = o.btnNext.alpha
        o.btnErrorPrev.isEnabled = o.btnPrev.isEnabled
        o.btnErrorNext.isEnabled = o.btnNext.isEnabled
        renderHeader()
        // keep the playing row in view when the list is long
        if (index in 0..last) binding.rvChannels?.let { (it.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(index + 1, 0) }
    }

    private fun counterText(): String {
        val total = viewModel.items.value.size
        return resources.getQuantityString(R.plurals.player_counter, total, (viewModel.index.value + 1).coerceAtMost(total), total)
    }

    private fun renderHeader() {
        val item = viewModel.currentItem()
        val total = viewModel.items.value.size
        val label = viewModel.label.value
        binding.tvLabel?.text = label
        binding.tvCounter?.text = counterText()
        binding.overlay.tvFullName?.text = item?.displayName.orEmpty()
        binding.overlay.tvFullSub?.text = "$label · ${counterText()}"
        headerAdapter.update(
            PlayerHeaderAdapter.State(
                channel = item,
                status = viewModel.status.value,
                listTitle = label,
                listCount = resources.getQuantityString(R.plurals.channel_count, total, total),
            ),
        )
    }

    private fun renderPlayIcon(playWhenReady: Boolean) {
        binding.overlay.btnPlayPause.setImageResource(if (playWhenReady) R.drawable.ic_pause else R.drawable.ic_play_arrow)
    }

    private fun renderOverlay() {
        val o = binding.overlay
        val status = viewModel.status.value
        val loading = status == PlaybackStatus.CONNECTING || status == PlaybackStatus.BUFFERING
        val error = status == PlaybackStatus.UNAVAILABLE || status == PlaybackStatus.NETWORK_ERROR

        o.layoutLoading.show(loading && !error)
        if (loading) {
            o.tvLoading.setText(if (status == PlaybackStatus.CONNECTING) R.string.player_connecting else R.string.player_buffering)
        }
        o.layoutError.show(error)
        if (error) {
            val network = status == PlaybackStatus.NETWORK_ERROR
            o.ivErrorIcon.setImageResource(if (network) R.drawable.ic_wifi_off else R.drawable.ic_tv_off)
            o.tvErrorTitle.setText(if (network) R.string.player_error_network_title else R.string.player_error_unavailable_title)
            o.tvErrorDesc.setText(if (network) R.string.player_error_network_desc else R.string.player_error_unavailable_desc)
        }

        val showControls = controlsVisible && !locked && !error && !loading
        o.layoutControls.show(showControls)
        o.dim.show(showControls)
        o.btnUnlock.show(locked && !error)
    }

    private fun readChannel() = ChannelItem(
        id = intent.getLongExtra(EXTRA_CHANNEL_ID, 0L),
        playlistId = intent.getLongExtra(EXTRA_PLAYLIST_ID, -1L).takeIf { it >= 0 },
        name = intent.getStringExtra(EXTRA_NAME).orEmpty(),
        groupTitle = intent.getStringExtra(EXTRA_GROUP).orEmpty(),
        logoUrl = intent.getStringExtra(EXTRA_LOGO),
        streamUrl = intent.getStringExtra(EXTRA_URL).orEmpty(),
        isFavorite = false,
    )

    companion object {
        private const val EXTRA_PLAYLIST_ID = "playlist_id"
        private const val EXTRA_CHANNEL_ID = "channel_id"
        private const val EXTRA_NAME = "name"
        private const val EXTRA_GROUP = "group"
        private const val EXTRA_LOGO = "logo"
        private const val EXTRA_URL = "url"
        private const val STATE_LOCKED = "locked"
        private const val STATE_CONTROLS = "controls_visible"
        private const val CONTROLS_TIMEOUT_MS = 3_500L
        private const val DISABLED_ALPHA = 0.35f

        fun intent(context: Context, source: PlayerSource, channel: ChannelItem): Intent =
            source.writeTo(Intent(context, PlayerActivity::class.java))
                .putExtra(EXTRA_PLAYLIST_ID, channel.playlistId ?: -1L)
                .putExtra(EXTRA_CHANNEL_ID, channel.id)
                .putExtra(EXTRA_NAME, channel.name)
                .putExtra(EXTRA_GROUP, channel.groupTitle)
                .putExtra(EXTRA_LOGO, channel.logoUrl)
                .putExtra(EXTRA_URL, channel.streamUrl)
    }
}
