package com.iptvplayer.xtreamiptv.myiptvpro

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.iptvplayer.xtreamiptv.myiptvpro.data.local.database.AppDatabase
import com.iptvplayer.xtreamiptv.myiptvpro.data.local.entity.ChannelEntity
import com.iptvplayer.xtreamiptv.myiptvpro.data.local.entity.FavoriteEntity
import com.iptvplayer.xtreamiptv.myiptvpro.data.local.entity.PlaylistEntity
import com.iptvplayer.xtreamiptv.myiptvpro.data.local.entity.RecentEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** Verifies the persistence rules documented in the R&D (section 13/14 and 7). Uses an in-memory DB. */
@RunWith(AndroidJUnit4::class)
class DatabaseRulesTest {

    private lateinit var db: AppDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
    }

    @After
    fun tearDown() = db.close()

    private suspend fun playlist(name: String) =
        db.playlistDao().insert(PlaylistEntity(name = name, sourceType = "FILE", source = "$name.m3u", createdAt = 1))

    private fun channel(playlistId: Long, name: String, url: String, group: String = "G", position: Int = 0) =
        ChannelEntity(playlistId = playlistId, name = name, groupTitle = group, logoUrl = null, streamUrl = url, tvgId = null, position = position)

    @Test
    fun deletingPlaylist_cascadesChannels_removesOrphanFavorites_butKeepsRecents() = runBlocking {
        val id = playlist("P")
        db.channelDao().insertAll(listOf(channel(id, "A", "https://a/1"), channel(id, "B", "https://a/2")))
        db.favoriteDao().add(FavoriteEntity("https://a/1", 1))
        db.recentDao().upsert(RecentEntity("https://a/1", "A", "G", null, id, 10))

        db.playlistDao().delete(id)
        db.favoriteDao().deleteOrphans()

        assertEquals(0, db.channelDao().getByPlaylist(id).size)
        assertTrue(db.favoriteDao().observeUrls().first().isEmpty())
        // history keeps rendering even though its playlist is gone
        assertEquals(1, db.recentDao().observe().first().size)
    }

    @Test
    fun favorites_areKeyedByStreamUrl_soDuplicatesShowOnce() = runBlocking {
        val id = playlist("P")
        db.channelDao().insertAll(
            listOf(channel(id, "Dup", "https://a/dup", position = 0), channel(id, "Dup", "https://a/dup", position = 1)),
        )
        db.favoriteDao().add(FavoriteEntity("https://a/dup", 1))

        val inPlaylist = db.channelDao().observeByPlaylist(id).first()
        assertTrue(inPlaylist.all { it.isFavorite }) // both cards are hearted
        assertEquals(1, db.favoriteDao().observeFavorites().first().size) // listed once
    }

    @Test
    fun categoryFilter_matchesSingleTokensInSemicolonGroups() = runBlocking {
        val id = playlist("P")
        db.channelDao().insertAll(
            listOf(
                channel(id, "One", "https://a/1", group = "Entertainment;Family;General"),
                channel(id, "Two", "https://a/2", group = "News"),
            ),
        )
        assertEquals(listOf("One"), db.channelDao().observeAll("Family").first().map { it.name })
        assertEquals(listOf("Two"), db.channelDao().observeAll("News").first().map { it.name })
        assertEquals(2, db.channelDao().observeAll("").first().size)
    }

    @Test
    fun search_isCaseInsensitivePartialAndMatchesNameOrCategory() = runBlocking {
        val id = playlist("P")
        db.channelDao().insertAll(listOf(channel(id, "BipBop Advanced", "https://a/1", group = "Alpha")))
        assertEquals(1, db.channelDao().searchInPlaylist(id, "BIP", 50).size)
        assertEquals(1, db.channelDao().searchInPlaylist(id, "advan", 50).size)
        assertEquals(1, db.channelDao().searchInPlaylist(id, "alpha", 50).size) // matches the category too
    }

    @Test
    fun recents_areNewestFirst_andTrimmedToLimit() = runBlocking {
        db.recentDao().upsert(RecentEntity("u1", "One", "G", null, null, 1))
        db.recentDao().upsert(RecentEntity("u2", "Two", "G", null, null, 2))
        db.recentDao().upsert(RecentEntity("u3", "Three", "G", null, null, 3))
        db.recentDao().upsert(RecentEntity("u1", "One", "G", null, null, 4)) // opened again -> moves to front

        assertEquals(listOf("One", "Three", "Two"), db.recentDao().observe().first().map { it.name })
        db.recentDao().trim(2)
        assertEquals(listOf("One", "Three"), db.recentDao().observe().first().map { it.name })
    }
}
