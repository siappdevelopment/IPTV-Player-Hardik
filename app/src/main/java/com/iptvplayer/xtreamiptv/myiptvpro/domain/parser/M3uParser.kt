package com.iptvplayer.xtreamiptv.myiptvpro.domain.parser

import com.iptvplayer.xtreamiptv.myiptvpro.domain.model.GROUP_UNKNOWN
import com.iptvplayer.xtreamiptv.myiptvpro.domain.model.ParsedChannel
import java.io.BufferedReader
import java.io.StringReader
import kotlin.coroutines.cancellation.CancellationException

/** Parsed channels plus whether the text looked like a playlist at all (see [M3uParser.parseDetailed]). */
class ParseResult(val channels: List<ParsedChannel>, val isPlaylist: Boolean)

/**
 * Streaming M3U / M3U8 parser. Rules (taken from the R&D observations):
 *  - `#EXTM3U` and unknown `#` directives are ignored.
 *  - Display name = `tvg-name`, otherwise the title after the first unquoted comma (may be blank).
 *  - Category = `group-title`, otherwise [GROUP_UNKNOWN]; the raw value (e.g. `A;B`) is kept.
 *  - The first non-comment line after `#EXTINF` is the stream URL.
 *  - An `#EXTINF` directly followed by another `#EXTINF` is dropped (no URL).
 *  - Duplicates and any URL scheme are kept; a bare URL line without `#EXTINF` becomes a
 *    channel with a blank name. Lines that are not URLs (e.g. HTML) are skipped.
 *  - Attribute values may use double or single quotes; attribute names are case-insensitive.
 */
object M3uParser {

    private val BOM = 0xFEFF.toChar()

    fun parse(text: String): List<ParsedChannel> = parse(BufferedReader(StringReader(text)))

    /**
     * Reads the whole playlist line by line (constant memory apart from the result list).
     * [isActive] is polled so a cancelled import stops promptly.
     */
    fun parse(reader: BufferedReader, isActive: () -> Boolean = { true }): List<ParsedChannel> =
        parseDetailed(reader, isActive).channels

    /**
     * Like [parse], but also reports whether the text is a playlist: it has an `#EXTM3U` header or an
     * `#EXTINF` entry, or every non-blank line is a URL. A web page or random text is not a playlist,
     * even though some of its lines may contain `://`.
     */
    fun parseDetailed(reader: BufferedReader, isActive: () -> Boolean = { true }): ParseResult {
        val result = ArrayList<ParsedChannel>()
        var pending: ExtInf? = null
        var first = true
        var sawDirective = false
        var junkLines = 0
        while (true) {
            val raw = reader.readLine() ?: break
            if (!isActive()) throw CancellationException("Playlist parsing cancelled")
            var line = raw.trim()
            if (first) {
                line = line.trimStart(BOM).trim()
                first = false
            }
            if (line.isEmpty()) continue

            if (line.startsWith("#EXTINF", ignoreCase = true)) {
                sawDirective = true
                pending = parseExtInf(line)
                continue
            }
            if (line.startsWith("#")) {
                if (line.startsWith("#EXTM3U", ignoreCase = true)) sawDirective = true
                continue
            }

            val info = pending
            pending = null
            if (info != null) {
                result.add(info.toChannel(line))
            } else if (isUrlLine(line)) {
                result.add(ParsedChannel("", GROUP_UNKNOWN, null, line, null))
            } else {
                junkLines++
            }
        }
        return ParseResult(result, isPlaylist = sawDirective || (result.isNotEmpty() && junkLines == 0))
    }

    private fun isUrlLine(line: String): Boolean = line.contains("://") && !line.any { it == '<' || it == '>' } && !line.contains(' ')

    private class ExtInf(
        val name: String,
        val group: String,
        val logo: String?,
        val tvgId: String?,
    ) {
        fun toChannel(url: String) = ParsedChannel(name, group, logo, url, tvgId)
    }

    /**
     * Hand-written scan of `#EXTINF:<duration> key="value" key='value' key=value,<title>` (a regex per line
     * was the bottleneck on very large playlists). Attributes end at the first comma outside a quoted value.
     */
    private fun parseExtInf(line: String): ExtInf {
        var tvgName: String? = null
        var group: String? = null
        var logo: String? = null
        var tvgId: String? = null
        var title: String? = null
        var unbalanced = false
        val n = line.length
        var i = line.indexOf(':').let { if (it < 0) "#EXTINF".length else it + 1 }
        while (i < n) {
            val c = line[i]
            if (c == ',') {
                title = line.substring(i + 1).trim()
                break
            }
            if (c == ' ' || c == '\t') {
                i++
                continue
            }
            val keyStart = i
            while (i < n && line[i] != '=' && line[i] != ' ' && line[i] != ',' && line[i] != '\t') i++
            if (i >= n || line[i] != '=') {
                if (i == keyStart) i++
                continue // a bare token such as the duration
            }
            val key = line.substring(keyStart, i)
            i++ // '='
            val value: String?
            if (i < n && (line[i] == '"' || line[i] == '\'')) {
                val end = line.indexOf(line[i], i + 1)
                if (end < 0) {
                    unbalanced = true
                    value = null
                    i = n
                } else {
                    value = line.substring(i + 1, end).trim()
                    i = end + 1
                }
            } else {
                val start = i
                while (i < n && line[i] != ' ' && line[i] != ',' && line[i] != '\t') i++
                value = line.substring(start, i)
            }
            if (value == null) continue
            when {
                key.equals("tvg-name", ignoreCase = true) -> tvgName = value
                key.equals("group-title", ignoreCase = true) -> group = value
                key.equals("tvg-logo", ignoreCase = true) -> logo = value
                key.equals("tvg-id", ignoreCase = true) -> tvgId = value
            }
        }
        if (unbalanced || title == null) title = lastTitle(line)
        val name = tvgName?.takeIf { it.isNotBlank() } ?: title
        return ExtInf(
            name = name,
            group = group.orEmpty().ifBlank { GROUP_UNKNOWN },
            logo = logo?.takeIf { it.isNotBlank() },
            tvgId = tvgId?.takeIf { it.isNotBlank() },
        )
    }

    /** Text after the last comma: a fallback for lines whose quotes are unbalanced. */
    private fun lastTitle(line: String): String {
        val i = line.lastIndexOf(',')
        return if (i < 0) "" else line.substring(i + 1).trim().trim('"', '\'')
    }
}
