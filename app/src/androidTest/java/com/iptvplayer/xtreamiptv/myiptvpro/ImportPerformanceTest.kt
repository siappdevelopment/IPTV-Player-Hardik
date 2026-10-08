package com.iptvplayer.xtreamiptv.myiptvpro

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.iptvplayer.xtreamiptv.myiptvpro.data.local.database.AppDatabase
import com.iptvplayer.xtreamiptv.myiptvpro.data.remote.PlaylistDownloader
import com.iptvplayer.xtreamiptv.myiptvpro.data.repository.PlaylistRepository
import com.iptvplayer.xtreamiptv.myiptvpro.domain.parser.M3uParser
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.BufferedReader
import java.io.File
import java.io.StringReader

/** Measures where the time goes when a very large playlist is imported (parse vs. database). */
@RunWith(AndroidJUnit4::class)
class ImportPerformanceTest {

    private val count = 50_000

    private fun playlistText() = buildString {
        append("#EXTM3U\n")
        repeat(count) { i ->
            append("#EXTINF:-1 tvg-id=\"id$i\" tvg-logo=\"https://logos.example/$i.png\" group-title=\"Group ${i % 120}\",Channel number $i HD\n")
            append("https://streams.example/live/$i/index.m3u8\n")
        }
    }

    @Test
    fun fiftyThousandChannels_importTimings() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val text = playlistText()

        val parseStart = System.nanoTime()
        val parsed = M3uParser.parse(BufferedReader(StringReader(text)))
        val parseMs = (System.nanoTime() - parseStart) / 1_000_000
        assertEquals(count, parsed.size)

        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        val repo = PlaylistRepository(db, PlaylistDownloader(), context.contentResolver)
        val file = File.createTempFile("perf", ".m3u", context.cacheDir).apply { writeText(text) }
        val importStart = System.nanoTime()
        val result = repo.importFromFile("Perf", Uri.fromFile(file), "perf.m3u")
        val importMs = (System.nanoTime() - importStart) / 1_000_000
        Log.i("PERF", "parse-only=$parseMs ms, full import (read+parse+insert)=$importMs ms, channels=${result.channelCount}")

        val queryStart = System.nanoTime()
        val list = repo.observePlaylists().first()
        val groups = db.channelDao().observeGroupCounts().first()
        val search = db.channelDao().searchAll("number 49", 50)
        val queryMs = (System.nanoTime() - queryStart) / 1_000_000
        Log.i("PERF", "list+groups+search queries=$queryMs ms (groups=${groups.size}, hits=${search.size})")
        db.close()

        assertEquals(count, result.channelCount)
        assertEquals(1, list.size)
        assertTrue("import took $importMs ms", importMs < 30_000)
    }
}
