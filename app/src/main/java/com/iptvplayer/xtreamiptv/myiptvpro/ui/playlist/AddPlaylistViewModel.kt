package com.iptvplayer.xtreamiptv.myiptvpro.ui.playlist

import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.data.remote.ImportException
import com.iptvplayer.xtreamiptv.myiptvpro.data.remote.ImportFailure
import com.iptvplayer.xtreamiptv.myiptvpro.data.repository.ImportResult
import com.iptvplayer.xtreamiptv.myiptvpro.data.repository.PlaylistRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class AddPlaylistViewModel(private val repository: PlaylistRepository) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    /** Description for the red "Couldn’t load playlist" banner, or null when there is no failure. */
    private val _failure = MutableStateFlow<Int?>(null)
    val failure: StateFlow<Int?> = _failure.asStateFlow()

    private val _imported = Channel<ImportResult>(Channel.BUFFERED)
    val imported = _imported.receiveAsFlow()

    private var job: Job? = null

    fun importFromUrl(name: String, url: String) = start { repository.importFromUrl(name, url) }

    fun importFromFile(name: String, uri: Uri, displayName: String) =
        start { repository.importFromFile(name, uri, displayName) }

    fun clearFailure() {
        _failure.value = null
    }

    /** Back while loading: abort the download and return to the form. */
    fun cancelImport() {
        job?.cancel()
    }

    private fun start(block: suspend () -> ImportResult) {
        if (_loading.value) return // one import at a time
        _loading.value = true
        _failure.value = null
        var timedOut = false
        val importJob = viewModelScope.launch {
            try {
                _imported.send(block())
            } catch (e: CancellationException) {
                throw e
            } catch (e: ImportException) {
                ensureActive() // cancelled by the user: do not report the aborted download as an error
                _failure.value = if (timedOut) R.string.add_error_timeout else describe(e.failure)
            } catch (e: Exception) {
                ensureActive()
                _failure.value = if (timedOut) R.string.add_error_timeout else R.string.add_error_desc
            } finally {
                _loading.value = false
            }
        }
        job = importJob
        // an import that has not finished in time is aborted and reported, never an endless spinner
        viewModelScope.launch {
            delay(IMPORT_TIMEOUT_MS)
            if (importJob.isActive) {
                timedOut = true
                importJob.cancel()
                _failure.value = R.string.add_error_timeout
                _loading.value = false
            }
        }
    }

    @StringRes
    private fun describe(failure: ImportFailure): Int = when (failure) {
        ImportFailure.EMPTY -> R.string.add_error_empty
        ImportFailure.FILE -> R.string.add_error_file
        ImportFailure.NOT_A_PLAYLIST -> R.string.add_error_invalid
        ImportFailure.TIMEOUT -> R.string.add_error_timeout
        ImportFailure.TOO_BIG -> R.string.add_error_big
        ImportFailure.NO_NETWORK, ImportFailure.INVALID_URL, ImportFailure.HTTP, ImportFailure.IO -> R.string.add_error_desc
    }

    private companion object {
        const val IMPORT_TIMEOUT_MS = 120_000L
    }
}
