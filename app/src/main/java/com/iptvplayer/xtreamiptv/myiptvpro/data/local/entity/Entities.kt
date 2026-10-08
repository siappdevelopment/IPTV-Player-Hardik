package com.iptvplayer.xtreamiptv.myiptvpro.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** A user playlist imported from a URL or from a local file. */
@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    /** [SOURCE_URL] or [SOURCE_FILE]. */
    val sourceType: String,
    /** The https URL, or the display name of the imported file. */
    val source: String,
    val createdAt: Long,
) {
    companion object {
        const val SOURCE_URL = "URL"
        const val SOURCE_FILE = "FILE"
    }
}

/** One channel row. Deleting the playlist cascades to its channels. */
@Entity(
    tableName = "channels",
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["id"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("playlistId"),
        Index("groupTitle"),
        Index("streamUrl"),
        Index("name"),
    ],
)
data class ChannelEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val playlistId: Long,
    val name: String,
    val groupTitle: String,
    val logoUrl: String?,
    val streamUrl: String,
    val tvgId: String?,
    val position: Int,
)

/** Favorites are keyed by stream URL: duplicates of the same stream share one state. */
@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val streamUrl: String,
    val addedAt: Long,
)

/**
 * Watch history. Holds a snapshot of the channel so entries keep rendering even after the
 * source playlist was deleted (no foreign key on purpose).
 */
@Entity(tableName = "recents", indices = [Index("playedAt")])
data class RecentEntity(
    @PrimaryKey val streamUrl: String,
    val name: String,
    val groupTitle: String,
    val logoUrl: String?,
    val playlistId: Long?,
    val playedAt: Long,
)
