package com.iptvplayer.xtreamiptv.myiptvpro.data.repository

import android.content.ContentResolver
import android.net.Uri
import androidx.room.withTransaction
import com.iptvplayer.xtreamiptv.myiptvpro.data.local.database.AppDatabase
import com.iptvplayer.xtreamiptv.myiptvpro.data.local.entity.ChannelEntity
import com.iptvplayer.xtreamiptv.myiptvpro.data.local.entity.PlaylistEntity
import com.iptvplayer.xtreamiptv.myiptvpro.data.remote.ImportException
import com.iptvplayer.xtreamiptv.myiptvpro.data.remote.ImportFailure
import com.iptvplayer.xtreamiptv.myiptvpro.data.remote.PlaylistDownloader
import com.iptvplayer.xtreamiptv.myiptvpro.domain.model.ParsedChannel
import com.iptvplayer.xtreamiptv.myiptvpro.domain.model.PlaylistItem
import com.iptvplayer.xtreamiptv.myiptvpro.domain.parser.M3uParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

/** Outcome of an import: the new playlist and how many channels it holds. */
data class ImportResult(val playlistId: Long, val channelCount: Int)

class PlaylistRepository(
    private val db: AppDatabase,
    private val downloader: PlaylistDownloader,
    private val contentResolver: ContentResolver,
) {
    private val playlistDao = db.playlistDao()
    private val channelDao = db.channelDao()

    fun observePlaylists(): Flow<List<PlaylistItem>> = playlistDao.observeAll()

    fun observePlaylist(id: Long): Flow<PlaylistItem?> = playlistDao.observeOne(id)

    suspend fun searchPlaylists(query: String): List<PlaylistItem> =
        playlistDao.search(query.trim().replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_"))

    suspend fun hasUrlPlaylist(url: String): Boolean = playlistDao.findByUrl(url.trim()) != null

    suspend fun getPlaylist(id: Long): PlaylistEntity? = playlistDao.getById(id)

    /** Downloads an https playlist, parses it off the main thread and stores it. Returns the new id. */
    suspend fun importFromUrl(name: String, url: String): ImportResult = withContext(Dispatchers.IO) {
        val trimmed = url.trim()
        val channels = abortable { call -> downloader.open(trimmed, call).use { parse(it) } }
        storeUrl(name, trimmed, channels)
    }

    /** Reads a playlist chosen through the system picker. [displayName] is the file name. */
    suspend fun importFromFile(name: String, uri: Uri, displayName: String): ImportResult =
        withContext(Dispatchers.IO) {
            val stream = try {
                contentResolver.openInputStream(uri)
            } catch (e: IOException) {
                throw ImportException(ImportFailure.FILE, e)
            } catch (e: SecurityException) {
                throw ImportException(ImportFailure.FILE, e)
            } ?: throw ImportException(ImportFailure.FILE)
            val channels = try {
                stream.use { parse(it) }
            } catch (e: IOException) {
                throw ImportException(ImportFailure.FILE, e)
            }
            store(name, PlaylistEntity.SOURCE_FILE, displayName, channels)
        }

    suspend fun renamePlaylist(id: Long, name: String) = playlistDao.rename(id, name)

    suspend fun deletePlaylist(id: Long) {
        db.withTransaction {
            playlistDao.delete(id) // cascades to channels
            db.favoriteDao().deleteOrphans() // favorites follow their channels; recents stay
        }
    }

    /** Removes every playlist, channel, favorite and recent entry. */
    suspend fun clearAll() {
        db.withTransaction {
            playlistDao.deleteAll()
            db.favoriteDao().deleteAll()
            db.recentDao().deleteAll()
        }
    }

    private suspend fun parse(stream: InputStream): List<ParsedChannel> {
        val job = currentCoroutineContext()[Job]
        val reader = BufferedReader(InputStreamReader(SizeLimitedStream(stream, MAX_PLAYLIST_BYTES), StandardCharsets.UTF_8), BUFFER_SIZE)
        val parsed = try {
            M3uParser.parseDetailed(reader) { job?.isActive != false }
        } catch (e: java.net.SocketTimeoutException) {
            throw ImportException(ImportFailure.TIMEOUT, e)
        } catch (e: IOException) {
            throw ImportException(ImportFailure.IO, e)
        }
        if (!parsed.isPlaylist) throw ImportException(ImportFailure.NOT_A_PLAYLIST)
        if (parsed.channels.isEmpty()) throw ImportException(ImportFailure.EMPTY)
        return parsed.channels
    }

    /**
     * Runs a blocking download so that cancelling the calling coroutine (Back, timeout) also aborts the
     * network call, instead of waiting for the socket timeouts.
     */
    private suspend fun <T> abortable(block: suspend (PlaylistDownloader.Call) -> T): T = coroutineScope {
        val call = PlaylistDownloader.Call()
        val watcher = launch(Dispatchers.IO) {
            try {
                awaitCancellation()
            } finally {
                call.cancel()
            }
        }
        try {
            block(call)
        } catch (e: Exception) {
            // the abort itself surfaces as a network error: report a cancelled import as cancellation
            coroutineContext.ensureActive()
            throw e
        } finally {
            watcher.cancel()
        }
    }

    /** Adding a link that is already in the library refreshes that playlist (and renames it) instead of duplicating it. */
    private suspend fun storeUrl(name: String, url: String, channels: List<ParsedChannel>): ImportResult =
        db.withTransaction {
            val existing = playlistDao.findByUrl(url)
            if (existing == null) {
                store(name, PlaylistEntity.SOURCE_URL, url, channels)
            } else {
                playlistDao.rename(existing.id, name.trim())
                channelDao.deleteByPlaylist(existing.id)
                insertChannels(existing.id, channels)
                ImportResult(existing.id, channels.size)
            }
        }

    private suspend fun store(
        name: String,
        sourceType: String,
        source: String,
        channels: List<ParsedChannel>,
    ): ImportResult = db.withTransaction {
        val playlistId = playlistDao.insert(
            PlaylistEntity(
                name = name.trim(),
                sourceType = sourceType,
                source = source,
                createdAt = System.currentTimeMillis(),
            ),
        )
        insertChannels(playlistId, channels)
        ImportResult(playlistId, channels.size)
    }

    private suspend fun insertChannels(playlistId: Long, channels: List<ParsedChannel>) {
        channels.chunked(INSERT_CHUNK).forEachIndexed { chunkIndex, chunk ->
            channelDao.insertAll(
                chunk.mapIndexed { i, c ->
                    ChannelEntity(
                        playlistId = playlistId,
                        name = c.name,
                        groupTitle = c.groupTitle,
                        logoUrl = c.logoUrl,
                        streamUrl = c.streamUrl,
                        tvgId = c.tvgId,
                        position = chunkIndex * INSERT_CHUNK + i,
                    )
                },
            )
        }
    }

    /**
     * Re-downloads every playlist that was imported from a link and replaces its channels.
     * Favorites and history are keyed by stream URL, so they survive. File playlists cannot be
     * refreshed (the file is not kept). Returns (refreshed, failed).
     */
    suspend fun refreshUrlPlaylists(): Pair<Int, Int> = withContext(Dispatchers.IO) {
        var ok = 0
        var failed = 0
        for (playlist in playlistDao.getByType(PlaylistEntity.SOURCE_URL)) {
            if (refreshPlaylist(playlist.id) != null) ok++ else failed++
        }
        ok to failed
    }

    /**
     * Re-downloads one link playlist and replaces its channels.
     * Returns the new channel count, or null when it is not a link playlist or could not be refreshed.
     */
    suspend fun refreshPlaylist(playlistId: Long): Int? = withContext(Dispatchers.IO) {
        val playlist = playlistDao.getById(playlistId)
        if (playlist == null || playlist.sourceType != PlaylistEntity.SOURCE_URL) return@withContext null
        try {
            val channels = abortable { call -> downloader.open(playlist.source, call).use { parse(it) } }
            db.withTransaction {
                channelDao.deleteByPlaylist(playlist.id)
                insertChannels(playlist.id, channels)
            }
            channels.size
        } catch (e: kotlin.coroutines.cancellation.CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }
    }
    private companion object {
        const val BUFFER_SIZE = 64 * 1024
        const val MAX_PLAYLIST_BYTES = 50L * 1024 * 1024
        const val INSERT_CHUNK = 500
    }
}

/** Stops reading after [limit] bytes so a huge response cannot exhaust memory. */
private class SizeLimitedStream(source: InputStream, private val limit: Long) : java.io.FilterInputStream(source) {
    private var count = 0L

    private fun add(n: Int): Int {
        if (n > 0) {
            count += n
            if (count > limit) throw ImportException(ImportFailure.TOO_BIG)
        }
        return n
    }

    override fun read(): Int {
        val b = super.read()
        if (b >= 0) add(1)
        return b
    }

    override fun read(buffer: ByteArray, off: Int, len: Int): Int = add(super.read(buffer, off, len))
}
