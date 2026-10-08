package com.iptvplayer.xtreamiptv.myiptvpro.ui.common

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.data.local.entity.PlaylistEntity
import com.iptvplayer.xtreamiptv.myiptvpro.data.prefs.ViewMode
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ItemPlaylistCardBinding
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ItemPlaylistRowBinding
import com.iptvplayer.xtreamiptv.myiptvpro.domain.model.PlaylistItem

class PlaylistCallbacks(
    val onOpen: (PlaylistItem) -> Unit,
    val onEdit: (PlaylistItem) -> Unit,
    val onDelete: (PlaylistItem) -> Unit,
)

/** Playlist cards (GRID) or rows (LIST). */
class PlaylistAdapter(
    private val callbacks: PlaylistCallbacks,
) : ListAdapter<PlaylistItem, RecyclerView.ViewHolder>(Diff) {

    var mode: ViewMode = ViewMode.GRID
        set(value) {
            if (field == value) return
            field = value
            @Suppress("NotifyDataSetChanged")
            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int) = mode.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (ViewMode.entries[viewType] == ViewMode.LIST) {
            RowHolder(ItemPlaylistRowBinding.inflate(inflater, parent, false))
        } else {
            CardHolder(ItemPlaylistCardBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is CardHolder -> holder.bind(item)
            is RowHolder -> holder.bind(item)
        }
    }

    private fun item(holder: RecyclerView.ViewHolder): PlaylistItem? =
        holder.bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION }?.let { getItem(it) }

    private inner class CardHolder(val b: ItemPlaylistCardBinding) : RecyclerView.ViewHolder(b.root) {
        init {
            b.root.setOnClickListener { item(this)?.let(callbacks.onOpen) }
            b.btnEdit.setOnClickListener { item(this)?.let(callbacks.onEdit) }
            b.btnDelete.setOnClickListener { item(this)?.let(callbacks.onDelete) }
        }

        fun bind(item: PlaylistItem) {
            val icon = Categories.playlistIcon(item.name)
            b.thumb1.bind(item.logo1, icon)
            b.thumb2.bind(item.logo2, icon)
            b.ivIcon.setImageResource(icon)
            b.tvName.text = item.name
            b.tvCount.text = channelCountText(b.root.context, item.channelCount)
            b.tvSource.bindSource(item)
        }
    }

    private inner class RowHolder(val b: ItemPlaylistRowBinding) : RecyclerView.ViewHolder(b.root) {
        init {
            b.root.setOnClickListener { item(this)?.let(callbacks.onOpen) }
            b.btnEdit.setOnClickListener { item(this)?.let(callbacks.onEdit) }
            b.btnDelete.setOnClickListener { item(this)?.let(callbacks.onDelete) }
        }

        fun bind(item: PlaylistItem) {
            val icon = Categories.playlistIcon(item.name)
            b.thumb.bind(item.logo1, icon)
            b.ivIcon.setImageResource(icon)
            b.tvName.text = item.name
            b.tvCount.text = channelCountText(b.root.context, item.channelCount)
            b.tvSource.bindSource(item)
        }
    }

    private fun TextView.bindSource(item: PlaylistItem) {
        val isUrl = item.sourceType == PlaylistEntity.SOURCE_URL
        setText(if (isUrl) R.string.source_url else R.string.source_file)
        val size = resources.getDimensionPixelSize(R.dimen._14sdp)
        val icon = ContextCompat.getDrawable(context, if (isUrl) R.drawable.ic_link else R.drawable.ic_description)
            ?.mutate()
        icon?.setBounds(0, 0, size, size)
        setCompoundDrawablesRelative(icon, null, null, null)
    }
    private object Diff : DiffUtil.ItemCallback<PlaylistItem>() {
        override fun areItemsTheSame(a: PlaylistItem, b: PlaylistItem) = a.id == b.id
        override fun areContentsTheSame(a: PlaylistItem, b: PlaylistItem) = a == b
    }
}

fun channelCountText(context: Context, count: Int): String =
    context.resources.getQuantityString(R.plurals.channel_count, count, count)
