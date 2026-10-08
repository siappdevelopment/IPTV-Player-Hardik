package com.iptvplayer.xtreamiptv.myiptvpro.data.remote

import java.io.FilterInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/** Why a playlist could not be imported. */
enum class ImportFailure { INVALID_URL, NO_NETWORK, HTTP, IO, EMPTY, FILE, NOT_A_PLAYLIST, TIMEOUT, TOO_BIG }

class ImportException(val failure: ImportFailure, cause: Throwable? = null) : Exception(failure.name, cause)

/**
 * Minimal blocking https downloader for playlists. Cleartext `http://` is refused (the
 * reference app rejects it too); redirects are followed manually and must stay on https.
 * Must be called from a background dispatcher.
 *
 * [allowCleartext] exists for tests that talk to a local server; the app never sets it.
 */
class PlaylistDownloader(private val allowCleartext: Boolean = false) {

    /** Lets another thread abort a download that is blocked on the network. */
    class Call {
        @Volatile private var connection: HttpURLConnection? = null
        @Volatile var cancelled = false
            private set

        internal fun attach(c: HttpURLConnection) {
            connection = c
            if (cancelled) c.disconnect()
        }

        fun cancel() {
            cancelled = true
            connection?.disconnect()
        }
    }

    fun open(rawUrl: String, call: Call = Call()): InputStream {
        var current = parse(rawUrl)
        repeat(MAX_REDIRECTS + 1) {
            val connection = try {
                current.openConnection() as HttpURLConnection
            } catch (e: IOException) {
                throw ImportException(ImportFailure.IO, e)
            }
            call.attach(connection)
            connection.instanceFollowRedirects = false
            connection.connectTimeout = CONNECT_TIMEOUT_MS
            connection.readTimeout = READ_TIMEOUT_MS
            connection.setRequestProperty("User-Agent", USER_AGENT)
            try {
                val code = connection.responseCode
                when {
                    code in 300..399 -> {
                        val location = connection.getHeaderField("Location")
                        connection.disconnect()
                        if (location.isNullOrBlank()) throw ImportException(ImportFailure.HTTP)
                        current = parse(URL(current, location).toString())
                    }
                    code == HttpURLConnection.HTTP_OK -> {
                        return object : FilterInputStream(connection.inputStream) {
                            override fun close() {
                                try {
                                    super.close()
                                } finally {
                                    connection.disconnect()
                                }
                            }
                        }
                    }
                    else -> {
                        connection.disconnect()
                        throw ImportException(ImportFailure.HTTP)
                    }
                }
            } catch (e: ImportException) {
                throw e
            } catch (e: java.net.SocketTimeoutException) {
                connection.disconnect()
                throw ImportException(ImportFailure.TIMEOUT, e)
            } catch (e: IOException) {
                connection.disconnect()
                throw ImportException(ImportFailure.IO, e)
            }
        }
        throw ImportException(ImportFailure.HTTP)
    }

    private fun parse(raw: String): URL {
        // spaces pasted into a link are encoded rather than rejected
        val trimmed = raw.trim().replace(" ", "%20")
        val secure = trimmed.startsWith("https://", ignoreCase = true)
        val plain = allowCleartext && trimmed.startsWith("http://", ignoreCase = true)
        if (!secure && !plain) throw ImportException(ImportFailure.INVALID_URL)
        return try {
            URL(trimmed)
        } catch (e: IOException) {
            throw ImportException(ImportFailure.INVALID_URL, e)
        }
    }

    private companion object {
        const val MAX_REDIRECTS = 5
        const val CONNECT_TIMEOUT_MS = 15_000
        const val READ_TIMEOUT_MS = 30_000
        const val USER_AGENT = "Mozilla/5.0 (Linux; Android) IPTVSmartPlayer/1.0"
    }
}
