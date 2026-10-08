package com.iptvplayer.xtreamiptv.myiptvpro

import com.iptvplayer.xtreamiptv.myiptvpro.domain.model.GROUP_UNKNOWN
import com.iptvplayer.xtreamiptv.myiptvpro.domain.parser.M3uParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.BufferedReader
import java.io.StringReader
import kotlin.coroutines.cancellation.CancellationException

/** Encodes the parser rules observed in the R&D document (section 9) plus import edge cases. */
class M3uParserTest {

    private val sample = """
        #EXTM3U
        #EXTINF:-1 tvg-id="bip.1" tvg-name="BipBop Advanced" tvg-logo="https://x/logo.png" group-title="Alpha",BipBop Advanced
        https://example.com/a.m3u8
        #EXTINF:-1 tvg-id="mux.1" tvg-name="Mux Test" group-title="Alpha",Mux Test Stream
        https://example.com/b.m3u8
        #EXTINF:-1 group-title="Beta",Dead Stream 404
        https://example.com/does-not-exist.m3u8
        #EXTINF:-1 group-title="Beta",Bad Scheme
        notaurl://foo
        #EXTINF:-1 tvg-name="No Group",No Group Channel
        https://example.com/c.m3u8
        #EXTINF:-1 group-title="Beta",BipBop Advanced
        https://example.com/a.m3u8
        #EXTINF:-1 group-title="Gamma",
        https://example.com/d.m3u8
        #EXTINF:-1 group-title="Gamma",ÜñíCödé Chännel 日本
        https://example.com/e.m3u8
        #EXTINF:-1 group-title="Delta",Malformed no url line
        #EXTINF:-1 group-title="Delta",After malformed
        https://example.com/f.m3u8
    """.trimIndent()

    private fun detailed(text: String) = M3uParser.parseDetailed(BufferedReader(StringReader(text)))

    @Test
    fun sampleYieldsNineChannels_dropsEntryWithoutUrl() {
        val channels = M3uParser.parse(sample)
        assertEquals(9, channels.size)
        assertEquals(false, channels.any { it.name == "Malformed no url line" })
    }

    @Test
    fun displayNamePrefersTvgNameThenTitle() {
        val channels = M3uParser.parse(sample)
        assertEquals("Mux Test", channels[1].name)
        assertEquals("Dead Stream 404", channels[2].name)
        assertEquals("No Group", channels[4].name)
    }

    @Test
    fun missingGroupBecomesUnknown_andGroupIsKeptRaw() {
        val channels = M3uParser.parse(sample)
        assertEquals(GROUP_UNKNOWN, channels[4].groupTitle)
        val multi = M3uParser.parse("#EXTINF:-1 group-title=\"Entertainment;Family;General\",X\nhttps://a/b")
        assertEquals("Entertainment;Family;General", multi.single().groupTitle)
    }

    @Test
    fun duplicatesAndInvalidSchemesAreKept_blankNameAllowed_unicodeOk() {
        val channels = M3uParser.parse(sample)
        assertEquals(2, channels.count { it.streamUrl == "https://example.com/a.m3u8" })
        assertEquals("notaurl://foo", channels[3].streamUrl)
        assertEquals("", channels[6].name)
        assertEquals("ÜñíCödé Chännel 日本", channels[7].name)
    }

    @Test
    fun logoAndTvgIdAreCaptured() {
        val first = M3uParser.parse(sample).first()
        assertEquals("https://x/logo.png", first.logoUrl)
        assertEquals("bip.1", first.tvgId)
        assertNull(M3uParser.parse(sample)[2].logoUrl)
    }

    @Test
    fun commaInsideTitleIsKept_andQuotedCommaInAttributeIsIgnored() {
        val ch = M3uParser.parse("#EXTINF:-1 group-title=\"A,B\",Name, HD\nhttps://a/b").single()
        assertEquals("Name, HD", ch.name)
        assertEquals("A,B", ch.groupTitle)
    }

    @Test
    fun bareUrlLinesBecomeChannels_butHtmlIsIgnored() {
        assertEquals(1, M3uParser.parse("https://a/b\n").size)
        assertEquals(0, M3uParser.parse("<html><body>hello</body></html>").size)
        assertEquals(0, M3uParser.parse("").size)
        assertEquals(0, M3uParser.parse("#EXTM3U\n").size)
    }

    @Test
    fun bomAndWindowsLineEndingsAreHandled() {
        val text = "﻿#EXTM3U\r\n#EXTINF:-1,One\r\nhttps://a/1\r\n#EXTINF:-1,Two\r\nhttps://a/2\r\n"
        val channels = M3uParser.parse(text)
        assertEquals(listOf("One", "Two"), channels.map { it.name })
    }

    @Test
    fun singleQuotedAndMixedCaseAttributesAreRead() {
        val text = "#EXTINF:-1 TVG-ID='x1' Tvg-Logo='https://l/a.png' GROUP-TITLE='News',Daily\nhttps://a/1"
        val ch = M3uParser.parse(text).single()
        assertEquals("x1", ch.tvgId)
        assertEquals("https://l/a.png", ch.logoUrl)
        assertEquals("News", ch.groupTitle)
        assertEquals("Daily", ch.name)
    }

    @Test
    fun extraDirectivesBetweenExtinfAndUrlAreSkipped() {
        val text = "#EXTM3U\n#EXTINF:-1 group-title=\"A\",One\n#EXTVLCOPT:http-user-agent=Foo\n#KODIPROP:x=y\nhttps://a/1\n"
        val ch = M3uParser.parse(text).single()
        assertEquals("One", ch.name)
        assertEquals("https://a/1", ch.streamUrl)
    }

    @Test
    fun unbalancedQuotesFallBackToTheLastComma() {
        val ch = M3uParser.parse("#EXTINF:-1 tvg-name=\"Broken, group-title=A,Fallback Name\nhttps://a/1").single()
        assertEquals("Fallback Name", ch.name)
    }

    @Test
    fun htmlAndPlainTextAreNotPlaylists() {
        val page = "<!doctype html>\n<a href=\"https://example.com/x\">link</a>\nhttp://www.w3.org/1999/xhtml"
        assertEquals(false, detailed(page).isPlaylist)
        assertEquals(false, detailed("hello world\nthis is not a playlist").isPlaylist)
        assertEquals(true, detailed("#EXTM3U\n").isPlaylist)
        assertEquals(true, detailed("https://a/1\nhttps://a/2\n").isPlaylist)
        assertEquals(true, detailed("#EXTINF:-1,A\nhttps://a/1").isPlaylist)
    }

    @Test
    fun veryLongNamesAndSpecialCharactersSurvive() {
        val long = "N".repeat(500)
        val group = "Tést & Ünï; 日本語; 📺"
        val ch = M3uParser.parse("#EXTINF:-1 group-title=\"$group\",$long <b>&amp;</b>\nhttps://a/1").single()
        assertEquals("$long <b>&amp;</b>", ch.name)
        assertEquals(group, ch.groupTitle)
    }

    @Test
    fun largePlaylistParsesQuickly() {
        val sb = StringBuilder("#EXTM3U\n")
        repeat(20_000) { sb.append("#EXTINF:-1 group-title=\"G${it % 50}\",Ch $it\nhttps://h/$it.m3u8\n") }
        val start = System.nanoTime()
        val channels = M3uParser.parse(sb.toString())
        val ms = (System.nanoTime() - start) / 1_000_000
        assertEquals(20_000, channels.size)
        assert(ms < 3_000) { "parsing took $ms ms" }
    }

    @Test(expected = CancellationException::class)
    fun cancellationStopsParsing() {
        M3uParser.parse(BufferedReader(StringReader("#EXTM3U\n#EXTINF:-1,A\nhttps://a/1\n")), isActive = { false })
    }
}
