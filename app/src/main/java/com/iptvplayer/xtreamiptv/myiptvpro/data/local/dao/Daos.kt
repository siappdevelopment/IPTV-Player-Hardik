package com.iptvplayer.xtreamiptv.myiptvpro.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.iptvplayer.xtreamiptv.myiptvpro.data.local.entity.ChannelEntity
import com.iptvplayer.xtreamiptv.myiptvpro.data.local.entity.FavoriteEntity
import com.iptvplayer.xtreamiptv.myiptvpro.data.local.entity.PlaylistEntity
import com.iptvplayer.xtreamiptv.myiptvpro.data.local.entity.RecentEntity
import com.iptvplayer.xtreamiptv.myiptvpro.domain.model.ChannelItem
import com.iptvplayer.xtreamiptv.myiptvpro.domain.model.GroupCount
import com.iptvplayer.xtreamiptv.myiptvpro.domain.model.PlaylistItem
import kotlinx.coroutines.flow.Flow

/** SQL fragment shared by every query that returns [ChannelItem] rows from the `channels` table. */
private const val CH_COLUMNS = """
    c.id AS id, c.playlistId AS playlistId, c.name AS name, c.groupTitle AS groupTitle,
    c.logoUrl AS logoUrl, c.streamUrl AS streamUrl, (f.streamUrl IS NOT NULL) AS isFavorite,
    NULL AS playedAt
"""

@Dao
interface PlaylistDao {
    @Insert
    suspend fun insert(playlist: PlaylistEntity): Long

    @Query(
        """
        SELECT p.id, p.name, p.sourceType, p.source, p.createdAt,
               (SELECT COUNT(*) FROM channels c WHERE c.playlistId = p.id) AS channelCount,
               (SELECT c.logoUrl FROM channels c WHERE c.playlistId = p.id AND c.logoUrl IS NOT NULL AND c.logoUrl != ''
                ORDER BY c.position LIMIT 1) AS logo1,
               (SELECT c.logoUrl FROM channels c WHERE c.playlistId = p.id AND c.logoUrl IS NOT NULL AND c.logoUrl != ''
                ORDER BY c.position LIMIT 1 OFFSET 1) AS logo2
        FROM playlists p ORDER BY p.createdAt ASC, p.id ASC
        """,
    )
    fun observeAll(): Flow<List<PlaylistItem>>

    @Query(
        """
        SELECT p.id, p.name, p.sourceType, p.source, p.createdAt,
               (SELECT COUNT(*) FROM channels c WHERE c.playlistId = p.id) AS channelCount,
               NULL AS logo1, NULL AS logo2
        FROM playlists p WHERE p.name LIKE '%' || :query || '%' ESCAPE '\' ORDER BY p.createdAt ASC
        """,
    )
    suspend fun search(query: String): List<PlaylistItem>

    @Query("SELECT * FROM playlists WHERE id = :id")
    suspend fun getById(id: Long): PlaylistEntity?

    @Query("SELECT * FROM playlists WHERE sourceType = 'URL' AND source = :url LIMIT 1")
    suspend fun findByUrl(url: String): PlaylistEntity?

    @Query("SELECT * FROM playlists WHERE sourceType = :type")
    suspend fun getByType(type: String): List<PlaylistEntity>

    @Query(
        """
        SELECT p.id, p.name, p.sourceType, p.source, p.createdAt,
               (SELECT COUNT(*) FROM channels c WHERE c.playlistId = p.id) AS channelCount,
               NULL AS logo1, NULL AS logo2
        FROM playlists p WHERE p.id = :id
        """,
    )
    fun observeOne(id: Long): Flow<PlaylistItem?>

    @Query("UPDATE playlists SET name = :name WHERE id = :id")
    suspend fun rename(id: Long, name: String)

    @Query("DELETE FROM playlists WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM playlists")
    suspend fun deleteAll()
}

@Dao
interface ChannelDao {
    @Insert
    suspend fun insertAll(channels: List<ChannelEntity>)

    @Insert
    suspend fun insert(channel: ChannelEntity): Long

    @Query("SELECT COALESCE(MAX(position), -1) + 1 FROM channels WHERE playlistId = :playlistId")
    suspend fun nextPosition(playlistId: Long): Int

    @Query("DELETE FROM channels WHERE playlistId = :playlistId")
    suspend fun deleteByPlaylist(playlistId: Long)

    /** Channels of one playlist in file order. */
    @Query(
        """
        SELECT $CH_COLUMNS FROM channels c LEFT JOIN favorites f ON f.streamUrl = c.streamUrl
        WHERE c.playlistId = :playlistId ORDER BY c.position ASC, c.id ASC
        """,
    )
    fun observeByPlaylist(playlistId: Long): Flow<List<ChannelItem>>

    /** Every channel; [group] (one category token, or empty for all) narrows the list. */
    @Query(
        """
        SELECT $CH_COLUMNS FROM channels c LEFT JOIN favorites f ON f.streamUrl = c.streamUrl
        WHERE (:group = '' OR (';' || c.groupTitle || ';') LIKE '%;' || :group || ';%')
        ORDER BY c.playlistId ASC, c.position ASC, c.id ASC
        """,
    )
    fun observeAll(group: String): Flow<List<ChannelItem>>

    @Query("SELECT groupTitle, COUNT(*) AS total FROM channels GROUP BY groupTitle")
    fun observeGroupCounts(): Flow<List<GroupCount>>

    @Query("SELECT COUNT(*) FROM channels")
    fun observeCount(): Flow<Int>

    /** A handful of real logos for the home banner collage. */
    @Query("SELECT logoUrl FROM channels WHERE logoUrl IS NOT NULL AND logoUrl != '' ORDER BY id DESC LIMIT :limit")
    fun observeBannerLogos(limit: Int): Flow<List<String>>

    /** Snapshot used to start the player queue. */
    @Query(
        """
        SELECT $CH_COLUMNS FROM channels c LEFT JOIN favorites f ON f.streamUrl = c.streamUrl
        WHERE c.playlistId = :playlistId ORDER BY c.position ASC, c.id ASC
        """,
    )
    suspend fun getByPlaylist(playlistId: Long): List<ChannelItem>

    // --- search (name OR category, case-insensitive) -------------------------------------------

    @Query(
        """
        SELECT $CH_COLUMNS FROM channels c LEFT JOIN favorites f ON f.streamUrl = c.streamUrl
        WHERE c.name LIKE '%' || :query || '%' ESCAPE '\' OR c.groupTitle LIKE '%' || :query || '%' ESCAPE '\'
        ORDER BY c.name COLLATE NOCASE LIMIT :limit
        """,
    )
    suspend fun searchAll(query: String, limit: Int): List<ChannelItem>

    @Query(
        """
        SELECT $CH_COLUMNS FROM channels c LEFT JOIN favorites f ON f.streamUrl = c.streamUrl
        WHERE c.playlistId = :playlistId
          AND (c.name LIKE '%' || :query || '%' ESCAPE '\' OR c.groupTitle LIKE '%' || :query || '%' ESCAPE '\')
        ORDER BY c.position LIMIT :limit
        """,
    )
    suspend fun searchInPlaylist(playlistId: Long, query: String, limit: Int): List<ChannelItem>

    @Query(
        """
        SELECT $CH_COLUMNS FROM channels c LEFT JOIN favorites f ON f.streamUrl = c.streamUrl
        WHERE (';' || c.groupTitle || ';') LIKE '%;' || :group || ';%'
          AND (c.name LIKE '%' || :query || '%' ESCAPE '\' OR c.groupTitle LIKE '%' || :query || '%' ESCAPE '\')
        ORDER BY c.name COLLATE NOCASE LIMIT :limit
        """,
    )
    suspend fun searchInGroup(group: String, query: String, limit: Int): List<ChannelItem>

    // --- single-channel edits ------------------------------------------------------------------

    @Query("SELECT * FROM channels WHERE id = :id")
    suspend fun getById(id: Long): ChannelEntity?

    @Query("SELECT playlistId FROM channels WHERE streamUrl = :streamUrl ORDER BY playlistId ASC LIMIT 1")
    suspend fun findPlaylistIdForUrl(streamUrl: String): Long?

    @Query("UPDATE channels SET name = :name, groupTitle = :group, logoUrl = :logo, streamUrl = :url WHERE id = :id")
    suspend fun update(id: Long, name: String, group: String, logo: String?, url: String)

    @Query("DELETE FROM channels WHERE id = :id")
    suspend fun delete(id: Long)
}

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun add(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE streamUrl = :streamUrl")
    suspend fun remove(streamUrl: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE streamUrl = :streamUrl)")
    suspend fun isFavorite(streamUrl: String): Boolean

    @Query("SELECT streamUrl FROM favorites")
    fun observeUrls(): Flow<List<String>>

    /** One entry per favorite stream URL, newest favorite first. */
    @Query(
        """
        SELECT MIN(c.id) AS id, c.playlistId AS playlistId, c.name AS name, c.groupTitle AS groupTitle,
               c.logoUrl AS logoUrl, c.streamUrl AS streamUrl, 1 AS isFavorite, NULL AS playedAt
        FROM channels c INNER JOIN favorites f ON f.streamUrl = c.streamUrl
        GROUP BY c.streamUrl ORDER BY MAX(f.addedAt) DESC
        """,
    )
    fun observeFavorites(): Flow<List<ChannelItem>>

    /** Favorites whose stream no longer exists in any playlist are removed. */
    @Query("DELETE FROM favorites WHERE streamUrl NOT IN (SELECT streamUrl FROM channels)")
    suspend fun deleteOrphans()

    @Query("DELETE FROM favorites")
    suspend fun deleteAll()
}

@Dao
interface RecentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(recent: RecentEntity)

    /** Newest first. `id` points at a channel row with the same stream URL, or 0 if none is left. */
    @Query(
        """
        SELECT COALESCE((SELECT MIN(c.id) FROM channels c WHERE c.streamUrl = r.streamUrl), 0) AS id,
               r.playlistId AS playlistId, r.name AS name, r.groupTitle AS groupTitle,
               r.logoUrl AS logoUrl, r.streamUrl AS streamUrl,
               (f.streamUrl IS NOT NULL) AS isFavorite, r.playedAt AS playedAt
        FROM recents r LEFT JOIN favorites f ON f.streamUrl = r.streamUrl
        ORDER BY r.playedAt DESC
        """,
    )
    fun observe(): Flow<List<ChannelItem>>

    @Query(
        """
        SELECT COALESCE((SELECT MIN(c.id) FROM channels c WHERE c.streamUrl = r.streamUrl), 0) AS id,
               r.playlistId AS playlistId, r.name AS name, r.groupTitle AS groupTitle,
               r.logoUrl AS logoUrl, r.streamUrl AS streamUrl,
               (f.streamUrl IS NOT NULL) AS isFavorite, r.playedAt AS playedAt
        FROM recents r LEFT JOIN favorites f ON f.streamUrl = r.streamUrl
        WHERE r.name LIKE '%' || :query || '%' ESCAPE '\' OR r.groupTitle LIKE '%' || :query || '%' ESCAPE '\'
        ORDER BY r.playedAt DESC LIMIT :limit
        """,
    )
    suspend fun search(query: String, limit: Int): List<ChannelItem>

    @Query("DELETE FROM recents WHERE streamUrl = :streamUrl")
    suspend fun remove(streamUrl: String)

    @Query("UPDATE recents SET name = :name, groupTitle = :group, logoUrl = :logo, streamUrl = :url WHERE streamUrl = :oldUrl")
    suspend fun update(oldUrl: String, name: String, group: String, logo: String?, url: String)

    @Query("DELETE FROM recents WHERE streamUrl NOT IN (SELECT streamUrl FROM recents ORDER BY playedAt DESC LIMIT :keep)")
    suspend fun trim(keep: Int)

    @Query("DELETE FROM recents")
    suspend fun deleteAll()
}
