package com.iptvplayer.xtreamiptv.myiptvpro.ui.playlist

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.TextView
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.container
import com.iptvplayer.xtreamiptv.myiptvpro.data.repository.ImportResult
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ActivityAddPlaylistBinding
import com.iptvplayer.xtreamiptv.myiptvpro.utils.applySystemBarInsets
import com.iptvplayer.xtreamiptv.myiptvpro.utils.enableAppEdgeToEdge
import com.iptvplayer.xtreamiptv.myiptvpro.utils.hideKeyboard
import com.iptvplayer.xtreamiptv.myiptvpro.utils.onTrimmedTextChanged
import com.iptvplayer.xtreamiptv.myiptvpro.utils.show
import com.iptvplayer.xtreamiptv.myiptvpro.utils.vmFactory
import kotlinx.coroutines.launch

/** Add a playlist by https link or by M3U file; errors are shown inline, as designed. */
class AddPlaylistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPlaylistBinding
    private lateinit var suggestions: SuggestionAdapter

    private val viewModel: AddPlaylistViewModel by viewModels {
        vmFactory { AddPlaylistViewModel(container.playlistRepository) }
    }

    private var fileTab = false
    private var fileUri: Uri? = null
    private var fileName: String? = null

    private val pickFile = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) acceptFile(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableAppEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityAddPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.addRoot.applySystemBarInsets()

        fileTab = savedInstanceState?.getBoolean(STATE_FILE_TAB)
            ?: intent.getBooleanExtra(EXTRA_START_ON_FILE, false)
        fileUri = savedInstanceState?.getString(STATE_FILE_URI)?.toUri()
        fileName = savedInstanceState?.getString(STATE_FILE_NAME)

        setupSuggestions()
        setupInputs()
        setupTabs()
        setupFileTab()
        binding.btnBack.setOnClickListener { finish() }
        binding.btnAdd.setOnClickListener { submit() }
        onBackPressedDispatcher.addCallback(this) {
            // while loading, Back aborts the import and returns to the form; otherwise it leaves the screen
            if (viewModel.loading.value) viewModel.cancelImport() else finish()
        }
        renderTab()
        renderFile()
        observe()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(STATE_FILE_TAB, fileTab)
        outState.putString(STATE_FILE_URI, fileUri?.toString())
        outState.putString(STATE_FILE_NAME, fileName)
    }

    // --- setup ------------------------------------------------------------------------------

    private fun setupSuggestions() {
        suggestions = SuggestionAdapter(DEFAULT_SUGGESTIONS) { suggestion ->
            if (binding.etName.text.isNullOrBlank()) binding.etName.setText(getString(suggestion.nameRes))
            binding.etUrl.setText(suggestion.url)
            clearErrors()
            binding.root.hideKeyboard()
        }
        binding.rvSuggestions.adapter = suggestions
    }

    private fun setupInputs() {
        binding.etName.onTrimmedTextChanged { clearName() }
        binding.etUrl.onTrimmedTextChanged { text ->
            binding.btnClearUrl.show(text.isNotEmpty())
            suggestions.selectedUrl = text.takeIf { t -> DEFAULT_SUGGESTIONS.any { it.url == t } }
            clearUrl()
        }
        binding.btnClearUrl.setOnClickListener { binding.etUrl.text?.clear() }
    }

    private fun setupTabs() {
        binding.tabUrl.setOnClickListener { selectTab(file = false) }
        binding.tabFile.setOnClickListener { selectTab(file = true) }
    }

    private fun setupFileTab() {
        binding.dropZone.setOnClickListener { launchPicker() }
        binding.btnChangeFile.setOnClickListener { launchPicker() }
        binding.btnRemoveFile.setOnClickListener {
            fileUri = null
            fileName = null
            renderFile()
        }
    }

    private fun launchPicker() {
        binding.root.hideKeyboard()
        pickFile.launch(arrayOf("*/*"))
    }

    // --- tabs / file ------------------------------------------------------------------------

    private fun selectTab(file: Boolean) {
        if (fileTab == file) return
        fileTab = file
        binding.root.hideKeyboard()
        viewModel.clearFailure()
        renderTab()
    }

    private fun renderTab() {
        binding.tabUrl.isSelected = !fileTab
        binding.tabFile.isSelected = fileTab
        binding.groupUrl.show(!fileTab)
        binding.groupFile.show(fileTab)
    }

    private fun acceptFile(uri: Uri) {
        val (name, size) = queryFile(uri)
        if (size != null && size > MAX_FILE_BYTES) {
            showFileError(R.string.err_file_big)
            return
        }
        fileUri = uri
        fileName = name ?: uri.lastPathSegment
        binding.tvFileError.show(false)
        renderFile()
    }

    private fun queryFile(uri: Uri): Pair<String?, Long?> = try {
        contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE), null, null, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val name = cursor.getString(0)
                    val size = if (cursor.isNull(1)) null else cursor.getLong(1)
                    name to size
                } else {
                    null to null
                }
            } ?: (null to null)
    } catch (e: Exception) {
        null to null
    }

    private fun renderFile() {
        val has = fileUri != null
        binding.dropZone.show(!has)
        binding.fileCard.show(has)
        binding.btnChangeFile.show(has)
        binding.tvFileName.text = fileName
    }

    // --- validation / submit ----------------------------------------------------------------

    private fun submit() {
        binding.root.hideKeyboard()
        clearErrors()
        val name = binding.etName.text.toString().trim()
        var ok = true
        if (name.isEmpty()) {
            showError(binding.fieldName, binding.tvNameError, R.string.err_name_required)
            ok = false
        }
        if (fileTab) {
            if (fileUri == null) {
                showFileError(R.string.err_file_required)
                ok = false
            }
        } else {
            val url = binding.etUrl.text.toString().trim()
            val error = when {
                url.isEmpty() -> R.string.err_url_required
                !url.startsWith("https://", ignoreCase = true) -> R.string.err_url_invalid
                else -> null
            }
            if (error != null) {
                showError(binding.fieldUrl, binding.tvUrlError, error)
                binding.tvUrlHint.show(false)
                ok = false
            }
        }
        if (!ok) return

        if (fileTab) {
            viewModel.importFromFile(name, fileUri!!, fileName.orEmpty())
        } else {
            viewModel.importFromUrl(name, binding.etUrl.text.toString().trim())
        }
    }

    private fun showError(field: android.view.View, label: TextView, message: Int) {
        field.isActivated = true
        label.setText(message)
        label.show(true)
    }

    private fun showFileError(message: Int) {
        binding.tvFileError.setText(message)
        binding.tvFileError.show(true)
    }

    private fun clearName() {
        binding.fieldName.isActivated = false
        binding.tvNameError.show(false)
    }

    private fun clearUrl() {
        binding.fieldUrl.isActivated = false
        binding.tvUrlError.show(false)
        binding.tvUrlHint.show(true)
    }

    private fun clearErrors() {
        clearName()
        clearUrl()
        binding.tvFileError.show(false)
    }

    // --- state ------------------------------------------------------------------------------

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.loading.collect { loading ->
                        binding.loadingOverlay.show(loading)
                        binding.btnAdd.isEnabled = !loading
                        if (loading) {
                            binding.tvLoadingDesc.text =
                                getString(R.string.add_loading_desc, binding.etName.text.toString().trim())
                        }
                    }
                }
                launch {
                    viewModel.failure.collect { desc ->
                        binding.errorBanner.show(desc != null)
                        if (desc != null) {
                            binding.tvErrorDesc.setText(desc)
                            revealErrorBanner()
                        }
                    }
                }
                launch { viewModel.imported.collect(::onImported) }
            }
        }
    }

    /** The banner sits below the (long) suggestions list, so bring it into view when a failure appears. */
    private fun revealErrorBanner() {
        binding.scroll.post {
            val bottom = binding.errorBanner.bottom + binding.scroll.paddingBottom
            binding.scroll.smoothScrollTo(0, (bottom - binding.scroll.height).coerceAtLeast(0))
        }
    }

    private fun onImported(result: ImportResult) {
        val count = resources.getQuantityString(R.plurals.channel_count, result.channelCount, result.channelCount)
        setResult(RESULT_OK, Intent().putExtra(EXTRA_MESSAGE, getString(R.string.toast_playlist_added, count)))
        finish()
    }

    companion object {
        const val EXTRA_START_ON_FILE = "start_on_file"
        const val EXTRA_MESSAGE = "message"
        private const val STATE_FILE_TAB = "file_tab"
        private const val STATE_FILE_URI = "file_uri"
        private const val STATE_FILE_NAME = "file_name"
        private const val MAX_FILE_BYTES = 20L * 1024 * 1024
    }
}
