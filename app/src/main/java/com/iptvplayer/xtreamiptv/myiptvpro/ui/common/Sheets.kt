package com.iptvplayer.xtreamiptv.myiptvpro.ui.common

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ItemFormChipBinding
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.SheetAddMenuBinding
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.SheetChannelActionsBinding
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.SheetChannelFormBinding
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.SheetPlaylistEditBinding
import com.iptvplayer.xtreamiptv.myiptvpro.domain.model.ChannelItem
import com.iptvplayer.xtreamiptv.myiptvpro.domain.model.GROUP_UNKNOWN
import com.iptvplayer.xtreamiptv.myiptvpro.domain.model.PlaylistItem
import com.iptvplayer.xtreamiptv.myiptvpro.utils.hideKeyboard
import com.iptvplayer.xtreamiptv.myiptvpro.utils.show
import com.iptvplayer.xtreamiptv.myiptvpro.utils.showKeyboard

/** Initial values of the channel form; null for "add". */
data class ChannelFormInput(val id: Long, val name: String, val url: String, val group: String, val logo: String?)

data class ChannelFormResult(
    val name: String,
    val url: String,
    val group: String,
    val logo: String?,
    /** Only set when adding. */
    val playlistId: Long?,
)

class ChannelActionsConfig(
    val canEdit: Boolean,
    val canDelete: Boolean,
    val showRemoveRecent: Boolean,
)

/** The design's bottom sheets. All of them are plain [BottomSheetDialog]s over the app theme. */
object Sheets {

    private val STREAM_URL = Regex("^(https?|rtsp|rtmp|udp)://\\S+$", RegexOption.IGNORE_CASE)

    fun show(context: Context, view: android.view.View, expand: Boolean = false): BottomSheetDialog {
        val dialog = BottomSheetDialog(context)
        dialog.setContentView(view)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog.setOnShowListener {
            dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)?.let { sheet ->
                sheet.setBackgroundResource(R.color.transparent)
                if (expand) {
                    val behavior = BottomSheetBehavior.from(sheet)
                    behavior.skipCollapsed = true
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }
        dialog.show()
        return dialog
    }

    // --- centre "+" ---------------------------------------------------------------------------

    fun addMenu(context: Context, onAddPlaylist: () -> Unit, onAddChannel: () -> Unit) {
        val b = SheetAddMenuBinding.inflate(LayoutInflater.from(context))
        val dialog = show(context, b.root)
        b.optionPlaylist.setOnClickListener { dialog.dismiss(); onAddPlaylist() }
        b.optionChannel.setOnClickListener { dialog.dismiss(); onAddChannel() }
    }

    // --- "more" on a channel ------------------------------------------------------------------

    fun channelActions(
        context: Context,
        item: ChannelItem,
        config: ChannelActionsConfig,
        onFavorite: () -> Unit,
        onEdit: () -> Unit,
        onDelete: () -> Unit,
        onRemoveRecent: () -> Unit,
    ) {
        val b = SheetChannelActionsBinding.inflate(LayoutInflater.from(context))
        val dialog = show(context, b.root)
        b.thumb.bind(item.logoUrl)
        b.tvName.text = item.displayName
        b.tvCategories.text = item.categoryLine

        b.actionFavorite.setText(if (item.isFavorite) R.string.action_remove_favorite else R.string.action_add_favorite)
        b.actionFavorite.setCompoundDrawablesRelativeWithIntrinsicBounds(
            if (item.isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border, 0, 0, 0,
        )
        tintDrawable(b.actionFavorite, if (item.isFavorite) R.color.color_error else R.color.color_text_primary)
        b.actionEdit.show(config.canEdit)
        b.actionDelete.show(config.canDelete)
        b.actionRemoveRecent.show(config.showRemoveRecent)

        b.actionFavorite.setOnClickListener { dialog.dismiss(); onFavorite() }
        b.actionEdit.setOnClickListener { dialog.dismiss(); onEdit() }
        b.actionDelete.setOnClickListener { dialog.dismiss(); onDelete() }
        b.actionRemoveRecent.setOnClickListener { dialog.dismiss(); onRemoveRecent() }
    }

    private fun tintDrawable(view: TextView, colorRes: Int) {
        TextViewCompat.setCompoundDrawableTintList(
            view,
            ColorStateList.valueOf(ContextCompat.getColor(view.context, colorRes)),
        )
    }

    // --- add / edit channel -------------------------------------------------------------------

    /**
     * @param pickLogo asks the host to open the image picker and report the stored logo URI (or null).
     */
    fun channelForm(
        context: Context,
        initial: ChannelFormInput?,
        playlists: List<PlaylistItem>,
        preselectPlaylistId: Long?,
        pickLogo: ((String?) -> Unit) -> Unit,
        onSave: (ChannelFormResult) -> Unit,
    ) {
        val b = SheetChannelFormBinding.inflate(LayoutInflater.from(context))
        val dialog = show(context, b.root, expand = true)
        val editing = initial != null
        b.tvTitle.setText(if (editing) R.string.form_edit_channel_title else R.string.form_add_channel_title)
        b.btnSave.setText(if (editing) R.string.action_save_changes else R.string.form_save_channel)
        b.etName.setText(initial?.name.orEmpty())
        b.etUrl.setText(initial?.url.orEmpty())
        b.etName.setSelection(b.etName.text.length)

        // category: single choice; untouched edits keep the original raw group value
        val originalGroup = initial?.group
        val initialToken = initial?.group?.split(';')?.map { it.trim() }
            ?.firstOrNull { it.isNotEmpty() && it != GROUP_UNKNOWN } ?: "General"
        var category = initialToken
        var categoryTouched = false
        val categoryNames = (listOf(initialToken) + Categories.known).distinct()
        val categoryViews = mutableMapOf<String, TextView>()
        categoryNames.forEach { name ->
            val chip = ItemFormChipBinding.inflate(LayoutInflater.from(context), b.groupCategories, false).root
            chip.text = name
            chip.isSelected = name == category
            chip.setOnClickListener {
                category = name
                categoryTouched = true
                categoryViews.forEach { (n, v) -> v.isSelected = n == name }
            }
            categoryViews[name] = chip
            b.groupCategories.addView(chip)
        }

        // target playlist (add only)
        var playlistId: Long? = preselectPlaylistId?.takeIf { id -> playlists.any { it.id == id } } ?: playlists.firstOrNull()?.id
        b.sectionPlaylists.show(!editing && playlists.isNotEmpty())
        val playlistViews = mutableMapOf<Long, TextView>()
        playlists.forEach { playlist ->
            val chip = ItemFormChipBinding.inflate(LayoutInflater.from(context), b.groupPlaylists, false).root
            chip.text = playlist.name
            chip.isSelected = playlist.id == playlistId
            chip.setOnClickListener {
                playlistId = playlist.id
                playlistViews.forEach { (id, v) -> v.isSelected = id == playlist.id }
            }
            playlistViews[playlist.id] = chip
            b.groupPlaylists.addView(chip)
        }

        // logo
        var logo = initial?.logo
        fun renderLogo() {
            b.thumbLogo.bind(logo)
            b.btnRemoveLogo.show(logo != null)
        }
        renderLogo()
        b.btnChooseLogo.setOnClickListener {
            b.root.hideKeyboard()
            pickLogo { picked ->
                if (picked != null) {
                    logo = picked
                    renderLogo()
                }
            }
        }
        b.btnRemoveLogo.setOnClickListener { logo = null; renderLogo() }

        b.btnCancel.setOnClickListener { dialog.dismiss() }
        b.btnSave.setOnClickListener {
            val name = b.etName.text.toString().trim()
            val url = b.etUrl.text.toString().trim()
            val nameOk = name.isNotEmpty()
            val urlError = when {
                url.isEmpty() -> R.string.err_stream_url
                !STREAM_URL.matches(url) -> R.string.err_stream_url_invalid
                else -> null
            }
            b.fieldName.isActivated = !nameOk
            b.tvNameError.show(!nameOk)
            if (!nameOk) b.tvNameError.setText(R.string.err_channel_name)
            b.fieldUrl.isActivated = urlError != null
            b.tvUrlError.show(urlError != null)
            if (urlError != null) b.tvUrlError.setText(urlError)
            if (!nameOk || urlError != null) return@setOnClickListener

            val group = if (editing && !categoryTouched && !originalGroup.isNullOrBlank()) originalGroup else category
            dialog.dismiss()
            onSave(ChannelFormResult(name, url, group, logo, if (editing) null else playlistId))
        }
        b.etName.addTextChangedListener(clearError(b.fieldName, b.tvNameError))
        b.etUrl.addTextChangedListener(clearError(b.fieldUrl, b.tvUrlError))
        if (!editing) b.etName.post { b.etName.showKeyboard() }
    }

    private fun clearError(field: android.view.View, error: android.view.View) = object : android.text.TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            field.isActivated = false
            error.visibility = android.view.View.GONE
        }
        override fun afterTextChanged(s: android.text.Editable?) = Unit
    }

    // --- edit playlist ------------------------------------------------------------------------

    fun playlistEdit(context: Context, currentName: String, onSave: (String) -> Unit) {
        val b = SheetPlaylistEditBinding.inflate(LayoutInflater.from(context))
        val dialog = show(context, b.root)
        b.etName.setText(currentName)
        b.etName.setSelection(b.etName.text.length)
        b.etName.addTextChangedListener(clearError(b.fieldName, b.tvNameError))
        b.btnCancel.setOnClickListener { dialog.dismiss() }
        b.btnSave.setOnClickListener {
            val name = b.etName.text.toString().trim()
            if (name.isEmpty()) {
                b.fieldName.isActivated = true
                b.tvNameError.show(true)
            } else {
                dialog.dismiss()
                onSave(name)
            }
        }
        b.etName.post { b.etName.showKeyboard() }
    }
}
