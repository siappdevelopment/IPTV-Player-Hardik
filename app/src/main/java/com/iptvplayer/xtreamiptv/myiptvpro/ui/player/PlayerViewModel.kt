package com.iptvplayer.xtreamiptv.myiptvpro.ui.player

import android.app.Application
import androidx.annotation.OptIn
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.data.prefs.AppPreferences
import com.iptvplayer.xtreamiptv.myiptvpro.data.repository.ChannelRepository
import com.iptvplayer.xtreamiptv.myiptvpro.data.repository.PlaylistRepository
import com.iptvplayer.xtreamiptv.myiptvpro.domain.model.ChannelItem
import com.iptvplayer.xtreamiptv.myiptvpro.utils.isNetworkAvailable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** What the status chip and the overlays show. */
enum class PlaybackStatus { CONNECTING, BUFFERING, PLAYING, PAUSED, UNAVAILABLE, NETWORK_ERROR }

/**
 * Owns the one ExoPlayer instance for the screen so playback survives rotation (portrait ⇄ fullscreen)
 * and the player is released exactly once when the screen is left for good.
 */
@OptIn(UnstableApi::class)
class PlayerViewModel(
    private val app: Application,
    private val channels: ChannelRepository,
    private val playlists: PlaylistRepository,
    private val prefs: AppPreferences,
    private val source: PlayerSource,
    private val start: ChannelItem,
) : AndroidViewModel(app) {

    val player: ExoPlayer = createPlayer(app, prefs.hardwareDecoding)

    private val queue = MutableStateFlow<List<ChannelItem>>(emptyList())

    private val _index = MutableStateFlow(0)
    val index: StateFlow<Int> = _index.asStateFlow()

    private val _label = MutableStateFlow(start.displayName)
    val label: StateFlow<String> = _label.asStateFlow()

    private val _status = MutableStateFlow(PlaybackStatus.CONNECTING)
    val status: StateFlow<PlaybackStatus> = _status.asStateFlow()

    private val _playWhenReady = MutableStateFlow(true)
    val playWhenReady: StateFlow<Boolean> = _playWhenReady.asStateFlow()

    /** Set when fullscreen is left so the forced orientation is released once portrait. */
    var orientationResetPending = false

    private var hasPlayed = false

    private val favoriteUrls: StateFlow<Set<String>> = channels.observeFavoriteUrls()
        .map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())

    /** The queue with live favorite state. */
    val items: StateFlow<List<ChannelItem>> = combine(queue, favoriteUrls) { list, favorites ->
        list.map { it.copy(isFavorite = it.streamUrl in favorites) }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val listener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_BUFFERING ->
                    _status.value = if (hasPlayed) PlaybackStatus.BUFFERING else PlaybackStatus.CONNECTING
                Player.STATE_READY -> {
                    hasPlayed = true
                    _status.value = readyStatus()
                }
            }
        }

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            _playWhenReady.value = playWhenReady
            if (player.playbackState == Player.STATE_READY) _status.value = readyStatus()
        }

        override fun onPlayerError(error: PlaybackException) {
            _status.value = if (isNetworkProblem(error)) PlaybackStatus.NETWORK_ERROR else PlaybackStatus.UNAVAILABLE
        }
    }

    init {
        player.addListener(listener)
        viewModelScope.launch { loadQueueAndPlay() }
    }

    private fun readyStatus() = if (player.playWhenReady) PlaybackStatus.PLAYING else PlaybackStatus.PAUSED

    private fun isNetworkProblem(error: PlaybackException): Boolean =
        !app.isNetworkAvailable() ||
            error.errorCode == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED ||
            error.errorCode == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT

    private suspend fun loadQueueAndPlay() {
        val (list, label) = resolveQueue()
        var startIndex = list.indexOfFirst { start.id != 0L && it.id == start.id }
        if (startIndex < 0) startIndex = list.indexOfFirst { it.streamUrl == start.streamUrl }
        if (startIndex >= 0) {
            queue.value = list
            _label.value = label
        } else {
            queue.value = listOf(start) // source is gone or does not contain it: play just this channel
            startIndex = 0
            _label.value = start.displayName
        }
        playAt(startIndex)
    }

    private suspend fun resolveQueue(): Pair<List<ChannelItem>, String> = when (val s = source) {
        is PlayerSource.Playlist -> {
            val playlist = playlists.getPlaylist(s.id)
            if (playlist == null) emptyList<ChannelItem>() to "" else channels.getPlaylistChannels(s.id) to playlist.name
        }
        is PlayerSource.Category -> channels.observeAll(s.name).first() to s.name
        PlayerSource.AllChannels -> channels.observeAll("").first() to app.getString(R.string.search_scope_all)
        PlayerSource.Recent -> channels.observeRecents().first() to app.getString(R.string.search_scope_recent)
        PlayerSource.Favorites -> channels.observeFavorites().first() to app.getString(R.string.search_scope_favorites)
        PlayerSource.Single -> emptyList<ChannelItem>() to ""
    }

    fun playAt(position: Int) {
        val list = queue.value
        if (position !in list.indices) return
        val item = list[position]
        _index.value = position
        hasPlayed = false
        _status.value = PlaybackStatus.CONNECTING
        viewModelScope.launch { channels.addRecent(item) }
        prefs.saveLastChannel(item, item.playlistId)
        player.setMediaItem(MediaItem.fromUri(item.streamUrl))
        player.prepare()
        player.playWhenReady = true
    }

    fun next() = playAt(_index.value + 1)

    fun previous() = playAt(_index.value - 1)

    fun retry() = playAt(_index.value)

    fun togglePlayPause() {
        player.playWhenReady = !player.playWhenReady
    }

    fun toggleFavorite(item: ChannelItem) {
        val isFavorite = item.streamUrl in favoriteUrls.value
        viewModelScope.launch { channels.setFavorite(item.streamUrl, !isFavorite) }
    }

    fun currentItem(): ChannelItem? = items.value.getOrNull(_index.value)

    override fun onCleared() {
        player.removeListener(listener)
        player.release()
    }

    private companion object {
        const val USER_AGENT = "Mozilla/5.0 (Linux; Android) IPTVSmartPlayer/1.0"
        const val CONNECT_TIMEOUT_MS = 15_000
        const val READ_TIMEOUT_MS = 20_000

        fun createPlayer(app: Application, hardwareDecoding: Boolean): ExoPlayer {
            val httpFactory = DefaultHttpDataSource.Factory()
                .setUserAgent(USER_AGENT)
                .setConnectTimeoutMs(CONNECT_TIMEOUT_MS)
                .setReadTimeoutMs(READ_TIMEOUT_MS)
                .setAllowCrossProtocolRedirects(true)
            val mediaSourceFactory = DefaultMediaSourceFactory(DefaultDataSource.Factory(app, httpFactory))
            val selector = if (hardwareDecoding) {
                MediaCodecSelector.DEFAULT
            } else {
                // software decoders first
                MediaCodecSelector { mime, secure, tunneling ->
                    MediaCodecSelector.DEFAULT.getDecoderInfos(mime, secure, tunneling).sortedBy { it.hardwareAccelerated }
                }
            }
            val renderers = DefaultRenderersFactory(app).setMediaCodecSelector(selector)
            return ExoPlayer.Builder(app, renderers)
                .setMediaSourceFactory(mediaSourceFactory)
                .setHandleAudioBecomingNoisy(true)
                .build()
                .also {
                    it.setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(C.USAGE_MEDIA)
                            .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                            .build(),
                        true,
                    )
                }
        }
    }
}
