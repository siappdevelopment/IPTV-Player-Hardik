package com.iptvplayer.xtreamiptv.myiptvpro.ui.common

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.data.repository.ChannelRepository
import com.iptvplayer.xtreamiptv.myiptvpro.data.repository.PlaylistRepository
import com.iptvplayer.xtreamiptv.myiptvpro.domain.model.ChannelItem
import com.iptvplayer.xtreamiptv.myiptvpro.utils.Messages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

/**
 * Everything a channel card can do (favorite, "more" sheet, edit, delete, add channel).
 * Shared by the tabs, playlist details, search and the player so behaviour is identical everywhere.
 */
class ChannelActionHandler(
    private val activity: AppCompatActivity,
    private val channels: ChannelRepository,
    private val playlists: PlaylistRepository,
    private val messageAnchor: () -> View,
    private val messageAbove: () -> View? = { null },
    private val onNeedPlaylist: () -> Unit = {},
) {
    private val scope get() = activity.lifecycleScope

    fun toggleFavorite(item: ChannelItem) {
        val makeFavorite = !item.isFavorite
        scope.launch {
            channels.setFavorite(item.streamUrl, makeFavorite)
            if (makeFavorite) {
                Messages.show(messageAnchor(), R.string.toast_fav_added, R.drawable.ic_favorite, R.color.color_error, messageAbove())
            } else {
                Messages.neutral(messageAnchor(), R.string.toast_fav_removed, R.drawable.ic_favorite_border, messageAbove())
            }
        }
    }

    /** The "more" sheet. [inRecent] adds "Remove from recent" for history rows. */
    fun showActions(item: ChannelItem, inRecent: Boolean = false) {
        val hasRow = item.id > 0
        Sheets.channelActions(
            context = activity,
            item = item,
            config = ChannelActionsConfig(canEdit = hasRow, canDelete = hasRow, showRemoveRecent = inRecent),
            onFavorite = { toggleFavorite(item) },
            onEdit = { editChannel(item) },
            onDelete = { confirmDelete(item) },
            onRemoveRecent = {
                scope.launch {
                    channels.removeRecent(item.streamUrl)
                    Messages.neutral(messageAnchor(), R.string.toast_recent_removed, R.drawable.ic_history_toggle_off, messageAbove())
                }
            },
        )
    }

    fun addChannel(preselectPlaylistId: Long? = null) {
        scope.launch {
            val all = playlists.observePlaylists().first()
            if (all.isEmpty()) {
                Messages.error(messageAnchor(), R.string.toast_add_playlist_first, messageAbove())
                onNeedPlaylist()
                return@launch
            }
            Sheets.channelForm(
                context = activity,
                initial = null,
                playlists = all,
                preselectPlaylistId = preselectPlaylistId,
                pickLogo = ::pickLogo,
                onSave = { result ->
                    scope.launch {
                        channels.addChannel(result.playlistId ?: all.first().id, result.name, result.url, result.group, result.logo)
                        Messages.show(messageAnchor(), R.string.toast_channel_added, above = messageAbove())
                    }
                },
            )
        }
    }

    private fun editChannel(item: ChannelItem) {
        Sheets.channelForm(
            context = activity,
            initial = ChannelFormInput(item.id, item.name, item.streamUrl, item.groupTitle, item.logoUrl),
            playlists = emptyList(),
            preselectPlaylistId = null,
            pickLogo = ::pickLogo,
            onSave = { result ->
                scope.launch {
                    channels.updateChannel(item.id, result.name, result.url, result.group, result.logo)
                    Messages.show(messageAnchor(), R.string.toast_channel_updated, above = messageAbove())
                }
            },
        )
    }

    private fun confirmDelete(item: ChannelItem) {
        Dialogs.confirm(
            context = activity,
            title = activity.getString(R.string.dialog_delete_channel_title),
            description = activity.getString(R.string.dialog_delete_channel_desc, item.displayName),
            confirmText = R.string.action_delete,
        ) {
            scope.launch {
                channels.deleteChannel(item.id)
                Messages.show(messageAnchor(), R.string.toast_channel_deleted, R.drawable.ic_delete, R.color.color_error, messageAbove())
            }
        }
    }

    // --- logo picking -------------------------------------------------------------------------

    private fun pickLogo(onResult: (String?) -> Unit) {
        var launcher: ActivityResultLauncher<String>? = null
        launcher = activity.activityResultRegistry.register(
            "logo_pick_" + UUID.randomUUID(),
            ActivityResultContracts.GetContent(),
        ) { uri ->
            launcher?.unregister()
            if (uri == null) onResult(null) else scope.launch { onResult(storeLogo(uri)) }
        }
        launcher.launch("image/*")
    }

    /** Copies the picked image into app storage (downscaled) so it stays available. */
    private suspend fun storeLogo(uri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val resolver = activity.contentResolver
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
            var sample = 1
            while (bounds.outWidth / sample > MAX_LOGO_SIDE || bounds.outHeight / sample > MAX_LOGO_SIDE) sample *= 2
            val bitmap = resolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = sample })
            } ?: return@withContext null
            val dir = File(activity.filesDir, "logos").apply { mkdirs() }
            val file = File(dir, UUID.randomUUID().toString() + ".png")
            file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            Uri.fromFile(file).toString()
        } catch (e: Exception) {
            null
        }
    }

    private companion object {
        const val MAX_LOGO_SIDE = 512
    }
}
