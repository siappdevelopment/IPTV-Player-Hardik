package com.iptvplayer.xtreamiptv.myiptvpro.data.repository

import androidx.room.withTransaction
import com.iptvplayer.xtreamiptv.myiptvpro.data.local.database.AppDatabase
import com.iptvplayer.xtreamiptv.myiptvpro.data.local.entity.ChannelEntity
import com.iptvplayer.xtreamiptv.myiptvpro.data.local.entity.FavoriteEntity
import com.iptvplayer.xtreamiptv.myiptvpro.data.local.entity.RecentEntity
import com.iptvplayer.xtreamiptv.myiptvpro.domain.model.ChannelItem
import com.iptvplayer.xtreamiptv.myiptvpro.domain.model.GROUP_UNKNOWN
import com.iptvplayer.xtreamiptv.myiptvpro.domain.model.GroupCount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/** Where a search looks. */
sealed interface SearchScope {
    /** Every channel and every playlist name. */
    data object All : SearchScope
    data class Playlist(val id: Long, val name: String) : SearchScope
    /** The channels of one category (one token of the `A;B` group value). */
    data class Category(val name: String) : SearchScope
    data object Recent : SearchScope
    data object Favorites : SearchScope
}

/** Channels, favorites and recents. Lists are Room flows, so the UI updates instantly. */
class ChannelRepository(private val db: AppDatabase) {
    private val channelDao = db.channelDao()
    private val favoriteDao = db.favoriteDao()
    private val recentDao = db.recentDao()

    fun observeByPlaylist(playlistId: Long): Flow<List<ChannelItem>> = channelDao.observeByPlaylist(playlistId)

    fun observeAll(group: String): Flow<List<ChannelItem>> = channelDao.observeAll(group)

    fun observeGroupCounts(): Flow<List<GroupCount>> = channelDao.observeGroupCounts()

    fun observeChannelCount(): Flow<Int> = channelDao.observeCount()

    fun observeBannerLogos(limit: Int): Flow<List<String>> = channelDao.observeBannerLogos(limit)

    fun observeFavorites(): Flow<List<ChannelItem>> = favoriteDao.observeFavorites()

    fun observeFavoriteUrls(): Flow<List<String>> = favoriteDao.observeUrls()

    fun observeRecents(): Flow<List<ChannelItem>> = recentDao.observe()

    suspend fun getPlaylistChannels(playlistId: Long): List<ChannelItem> = channelDao.getByPlaylist(playlistId)

    suspend fun findPlaylistIdForUrl(streamUrl: String): Long? = channelDao.findPlaylistIdForUrl(streamUrl)

    /** Case-insensitive match on channel name or category inside [scope]. Results are capped at [limit]. */
    suspend fun search(scope: SearchScope, query: String, limit: Int = SEARCH_LIMIT): List<ChannelItem> {
        val q = escapeLike(query.trim())
        if (q.isEmpty()) return emptyList()
        return when (scope) {
            SearchScope.All -> channelDao.searchAll(q, limit)
            is SearchScope.Playlist -> channelDao.searchInPlaylist(scope.id, q, limit)
            is SearchScope.Category -> channelDao.searchInGroup(scope.name, q, limit)
            SearchScope.Recent -> recentDao.search(q, limit)
            SearchScope.Favorites -> {
                val needle = query.trim()
                favoriteDao.observeFavorites().first().filter {
                    it.name.contains(needle, ignoreCase = true) || it.groupTitle.contains(needle, ignoreCase = true)
                }.take(limit)
            }
        }
    }

    suspend fun setFavorite(streamUrl: String, favorite: Boolean) {
        if (favorite) {
            favoriteDao.add(FavoriteEntity(streamUrl, System.currentTimeMillis()))
        } else {
            favoriteDao.remove(streamUrl)
        }
    }

    /** Appends a channel to [playlistId]. Returns the new channel id. */
    suspend fun addChannel(playlistId: Long, name: String, url: String, group: String, logo: String?): Long =
        db.withTransaction {
            channelDao.insert(
                ChannelEntity(
                    playlistId = playlistId,
                    name = name.trim(),
                    groupTitle = group.trim().ifBlank { GROUP_UNKNOWN },
                    logoUrl = logo,
                    streamUrl = url.trim(),
                    tvgId = null,
                    position = channelDao.nextPosition(playlistId),
                ),
            )
        }

    /**
     * Edits a channel. Favorites and history are keyed by stream URL, so when the URL changes
     * they are moved to the new URL together with the edit.
     */
    suspend fun updateChannel(id: Long, name: String, url: String, group: String, logo: String?) {
        db.withTransaction {
            val old = channelDao.getById(id) ?: return@withTransaction
            val newUrl = url.trim()
            val newGroup = group.trim().ifBlank { GROUP_UNKNOWN }
            channelDao.update(id, name.trim(), newGroup, logo, newUrl)
            if (old.streamUrl != newUrl) {
                if (favoriteDao.isFavorite(old.streamUrl)) {
                    favoriteDao.add(FavoriteEntity(newUrl, System.currentTimeMillis()))
                }
                recentDao.remove(newUrl) // avoid a primary-key clash with an existing entry
            }
            recentDao.update(old.streamUrl, name.trim(), newGroup, logo, newUrl)
            favoriteDao.deleteOrphans()
        }
    }

    suspend fun deleteChannel(id: Long) {
        db.withTransaction {
            channelDao.delete(id)
            favoriteDao.deleteOrphans()
        }
    }

    /** Adds (or refreshes) a history entry; the newest entry is always first. */
    suspend fun addRecent(channel: ChannelItem) {
        db.withTransaction {
            recentDao.upsert(
                RecentEntity(
                    streamUrl = channel.streamUrl,
                    name = channel.name,
                    groupTitle = channel.groupTitle,
                    logoUrl = channel.logoUrl,
                    playlistId = channel.playlistId,
                    playedAt = System.currentTimeMillis(),
                ),
            )
            recentDao.trim(MAX_RECENTS)
        }
    }

    suspend fun removeRecent(streamUrl: String) = recentDao.remove(streamUrl)

    suspend fun clearRecents() = recentDao.deleteAll()

    private fun escapeLike(raw: String): String =
        raw.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_")

    private companion object {
        const val MAX_RECENTS = 200
        const val SEARCH_LIMIT = 300
    }
}
