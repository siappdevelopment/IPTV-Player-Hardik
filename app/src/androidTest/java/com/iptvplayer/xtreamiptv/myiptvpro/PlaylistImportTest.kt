package com.iptvplayer.xtreamiptv.myiptvpro

import android.content.Context
import android.net.Uri
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.iptvplayer.xtreamiptv.myiptvpro.data.local.database.AppDatabase
import com.iptvplayer.xtreamiptv.myiptvpro.data.remote.ImportException
import com.iptvplayer.xtreamiptv.myiptvpro.data.remote.ImportFailure
import com.iptvplayer.xtreamiptv.myiptvpro.data.remote.PlaylistDownloader
import com.iptvplayer.xtreamiptv.myiptvpro.data.repository.PlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

/**
 * End-to-end tests of the import pipeline (download → parse → Room) against a tiny local HTTP server,
 * plus file imports. Every failure mode of the import screen maps to one of these cases.
 */
@RunWith(AndroidJUnit4::class)
class PlaylistImportTest {

    private lateinit var db: AppDatabase
    private lateinit var repo: PlaylistRepository
    private lateinit var server: ServerSocket
    private lateinit var context: Context
    private val port get() = server.localPort

    private val valid = """
        #EXTM3U
        #EXTINF:-1 tvg-id="a" tvg-name="Alpha One" tvg-logo="https://l/a.png" group-title="News",Alpha One
        https://s/1.m3u8
        #EXTINF:-1 group-title="Sports;Live",Beta
        https://s/2.m3u8
        #EXTINF:-1,No Group
        https://s/3.m3u8
        #EXTINF:-1 group-title="News",Alpha One
        https://s/1.m3u8
    """.trimIndent()

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        repo = PlaylistRepository(db, PlaylistDownloader(allowCleartext = true), context.contentResolver)
        server = ServerSocket(0)
        thread(isDaemon = true) { serve() }
    }

    @After
    fun tearDown() {
        server.close()
        db.close()
    }

    // --- tiny HTTP server ---------------------------------------------------------------------

    private fun serve() {
        while (!server.isClosed) {
            val socket = try {
                server.accept()
            } catch (e: Exception) {
                return
            }
            thread(isDaemon = true) { handle(socket) }
        }
    }

    private fun handle(socket: Socket) = socket.use {
        try {
            val request = it.getInputStream().bufferedReader().readLine() ?: return
            val path = request.split(" ").getOrNull(1).orEmpty()
            val out = it.getOutputStream()
            fun reply(code: Int, body: String, extra: String = "") {
                val bytes = body.toByteArray()
                out.write("HTTP/1.1 $code X\r\nContent-Length: ${bytes.size}\r\nConnection: close\r\n$extra\r\n".toByteArray())
                out.write(bytes)
                out.flush()
            }
            when (path) {
                "/ok" -> reply(200, valid)
                "/redirect" -> reply(302, "", "Location: /ok\r\n")
                "/e404" -> reply(404, "nope")
                "/e500" -> reply(500, "boom")
                "/empty" -> reply(200, "")
                "/header-only" -> reply(200, "#EXTM3U\n")
                "/html" -> reply(200, "<!doctype html><html><a href=\"https://example.com/x\">x</a>\nhttp://www.w3.org/x</html>")
                "/big" -> reply(200, buildString {
                    append("#EXTM3U\n")
                    repeat(5_000) { i -> append("#EXTINF:-1 group-title=\"G${i % 40}\",Ch $i\nhttps://s/$i.m3u8\n") }
                })
                "/slow" -> {
                    Thread.sleep(15_000)
                    reply(200, valid)
                }
                else -> reply(404, "?")
            }
        } catch (_: Exception) {
            // client went away (cancelled import)
        }
    }

    private fun url(path: String) = "http://127.0.0.1:$port$path"

    private fun failureOf(block: suspend () -> Unit): ImportFailure = runBlocking {
        try {
            block()
            fail("expected an ImportException")
            error("unreachable")
        } catch (e: ImportException) {
            e.failure
        }
    }

    // --- URL import ---------------------------------------------------------------------------

    @Test
    fun validUrl_savesPlaylistChannelsAndGroups() = runBlocking {
        val result = repo.importFromUrl("News", url("/ok"))
        assertEquals(4, result.channelCount)
        val playlists = repo.observePlaylists().first()
        assertEquals(1, playlists.size)
        assertEquals("News", playlists.single().name)
        assertEquals(4, playlists.single().channelCount)
        val channels = db.channelDao().observeByPlaylist(result.playlistId).first()
        assertEquals(listOf("Alpha One", "Beta", "No Group", "Alpha One"), channels.map { it.name })
        assertEquals(listOf("News", "Sports;Live", "Unknown", "News"), channels.map { it.groupTitle })
        assertEquals("https://l/a.png", channels.first().logoUrl)
        assertTrue(channels.all { it.playlistId == result.playlistId })
    }

    @Test
    fun redirectIsFollowed() = runBlocking {
        assertEquals(4, repo.importFromUrl("R", url("/redirect")).channelCount)
    }

    @Test
    fun sameUrlTwice_refreshesTheExistingPlaylistInsteadOfDuplicatingIt() = runBlocking {
        val first = repo.importFromUrl("One", url("/ok"))
        val second = repo.importFromUrl("Renamed", url("/ok"))
        assertEquals(first.playlistId, second.playlistId)
        val playlists = repo.observePlaylists().first()
        assertEquals(1, playlists.size)
        assertEquals("Renamed", playlists.single().name)
        assertEquals(4, playlists.single().channelCount) // replaced, not doubled
    }

    @Test
    fun urlWithSurroundingSpaces_isTrimmed() = runBlocking {
        assertEquals(4, repo.importFromUrl("S", "   " + url("/ok") + "  ").channelCount)
    }

    @Test
    fun serverErrors_areReportedAsHttpFailures_andSaveNothing() {
        assertEquals(ImportFailure.HTTP, failureOf { repo.importFromUrl("x", url("/e404")) })
        assertEquals(ImportFailure.HTTP, failureOf { repo.importFromUrl("x", url("/e500")) })
        assertEquals(0, runBlocking { repo.observePlaylists().first().size })
    }

    @Test
    fun emptyAndInvalidResponses_areRejected() {
        assertEquals(ImportFailure.NOT_A_PLAYLIST, failureOf { repo.importFromUrl("x", url("/empty")) })
        assertEquals(ImportFailure.EMPTY, failureOf { repo.importFromUrl("x", url("/header-only")) })
        assertEquals(ImportFailure.NOT_A_PLAYLIST, failureOf { repo.importFromUrl("x", url("/html")) })
        assertEquals(0, runBlocking { repo.observePlaylists().first().size })
    }

    @Test
    fun cleartextAndGarbageUrls_areRejected_whenCleartextIsNotAllowed() {
        val strict = PlaylistRepository(db, PlaylistDownloader(), context.contentResolver)
        assertEquals(ImportFailure.INVALID_URL, failureOf { strict.importFromUrl("x", url("/ok")) })
        assertEquals(ImportFailure.INVALID_URL, failureOf { strict.importFromUrl("x", "ftp://a/b") })
        assertEquals(ImportFailure.INVALID_URL, failureOf { strict.importFromUrl("x", "not a url") })
    }

    @Test
    fun unreachableServer_isAnIoFailure() {
        val closed = ServerSocket(0).use { it.localPort }
        val failure = failureOf { repo.importFromUrl("x", "http://127.0.0.1:$closed/x") }
        assertTrue(failure == ImportFailure.IO || failure == ImportFailure.TIMEOUT)
    }

    @Test
    fun largePlaylist_importsOffTheMainThread_andKeepsEveryChannel() = runBlocking {
        val result = repo.importFromUrl("Big", url("/big"))
        assertEquals(5_000, result.channelCount)
        assertEquals(5_000, db.channelDao().observeByPlaylist(result.playlistId).first().size)
        assertEquals(40, db.channelDao().observeGroupCounts().first().size)
    }

    @Test
    fun cancellingASlowImport_abortsPromptly_andSavesNothing() = runBlocking {
        val started = System.currentTimeMillis()
        val job = async(Dispatchers.Default) { repo.importFromUrl("Slow", url("/slow")) }
        delay(600)
        job.cancel()
        try {
            withTimeout(5_000) { job.join() }
        } catch (e: Exception) {
            fail("cancelled import did not stop in time: $e")
        }
        val tookMs = System.currentTimeMillis() - started
        assertTrue("took $tookMs ms", tookMs < 6_000)
        assertTrue(job.isCancelled)
        assertEquals(0, repo.observePlaylists().first().size)
    }

    // --- file import --------------------------------------------------------------------------

    private fun fileUri(content: String): Uri {
        val file = File.createTempFile("import", ".m3u", context.cacheDir)
        file.writeText(content)
        return Uri.fromFile(file)
    }

    @Test
    fun fileImport_savesChannels_withUnicodeAndDuplicates() = runBlocking {
        val text = "#EXTM3U\n#EXTINF:-1 group-title=\"日本\",テレビ\nhttps://a/1\n#EXTINF:-1 group-title=\"日本\",テレビ\nhttps://a/1\n"
        val result = repo.importFromFile("Files", fileUri(text), "jp.m3u")
        assertEquals(2, result.channelCount)
        val channel = db.channelDao().observeByPlaylist(result.playlistId).first().first()
        assertEquals("テレビ", channel.name)
        assertEquals("日本", channel.groupTitle)
        assertEquals("FILE", repo.getPlaylist(result.playlistId)?.sourceType)
    }

    @Test
    fun importingTheSameFileTwice_createsTwoPlaylists() = runBlocking {
        val uri = fileUri(valid)
        repo.importFromFile("A", uri, "x.m3u")
        repo.importFromFile("B", uri, "x.m3u")
        assertEquals(2, repo.observePlaylists().first().size)
    }

    @Test
    fun badFiles_areRejected() {
        assertEquals(ImportFailure.NOT_A_PLAYLIST, failureOf { repo.importFromFile("x", fileUri("just some text\nmore text"), "t.txt") })
        assertEquals(ImportFailure.EMPTY, failureOf { repo.importFromFile("x", fileUri("#EXTM3U\n"), "e.m3u") })
        assertEquals(ImportFailure.FILE, failureOf { repo.importFromFile("x", Uri.fromFile(File("/nonexistent/none.m3u")), "n.m3u") })
        assertEquals(0, runBlocking { repo.observePlaylists().first().size })
    }

    @Test
    fun deletingAPlaylist_removesItsChannelsAndOrphanFavorites() = runBlocking {
        val result = repo.importFromUrl("D", url("/ok"))
        db.favoriteDao().add(com.iptvplayer.xtreamiptv.myiptvpro.data.local.entity.FavoriteEntity("https://s/1.m3u8", 1))
        repo.deletePlaylist(result.playlistId)
        assertTrue(repo.observePlaylists().first().isEmpty())
        assertFalse(db.channelDao().observeAll("").first().any { it.playlistId == result.playlistId })
        assertTrue(db.favoriteDao().observeUrls().first().isEmpty())
    }

    @Test
    fun refresh_replacesChannels_andKeepsFavorites() = runBlocking {
        val result = repo.importFromUrl("Ref", url("/ok"))
        db.favoriteDao().add(com.iptvplayer.xtreamiptv.myiptvpro.data.local.entity.FavoriteEntity("https://s/2.m3u8", 1))
        assertEquals(4, repo.refreshPlaylist(result.playlistId))
        assertEquals(4, db.channelDao().observeByPlaylist(result.playlistId).first().size)
        assertEquals(listOf("https://s/2.m3u8"), db.favoriteDao().observeUrls().first())
    }
}
