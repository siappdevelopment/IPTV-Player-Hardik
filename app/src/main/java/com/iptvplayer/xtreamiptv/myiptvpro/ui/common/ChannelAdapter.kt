package com.iptvplayer.xtreamiptv.myiptvpro.ui.common

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.data.prefs.ViewMode
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ItemChannelCardBinding
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ItemChannelGridBinding
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ItemChannelRowBinding
import com.iptvplayer.xtreamiptv.myiptvpro.domain.model.ChannelItem
import com.iptvplayer.xtreamiptv.myiptvpro.utils.relativeTime
import com.iptvplayer.xtreamiptv.myiptvpro.utils.show

class ChannelCallbacks(
    val onOpen: (ChannelItem) -> Unit,
    val onFavorite: (ChannelItem) -> Unit,
    val onMore: (ChannelItem) -> Unit,
)

object ChannelDiff : DiffUtil.ItemCallback<ChannelItem>() {
    override fun areItemsTheSame(a: ChannelItem, b: ChannelItem) =
        a.id == b.id && a.streamUrl == b.streamUrl

    override fun areContentsTheSame(a: ChannelItem, b: ChannelItem) = a == b
}

/** "Watched 5m ago" for history rows, or null. */
internal fun Context.watchedText(item: ChannelItem, show: Boolean): String? =
    if (show) item.playedAt?.let { getString(R.string.watched_ago, relativeTime(it)) } else null

/**
 * One adapter for all channel lists. [mode] picks the card layout: large cards, compact grid
 * cards or rows. When [showWatched] is on, history rows show "Watched …".
 */
class ChannelAdapter(
    private val callbacks: ChannelCallbacks,
    private val showWatched: Boolean = false,
) : ListAdapter<ChannelItem, RecyclerView.ViewHolder>(ChannelDiff) {

    var mode: ViewMode = ViewMode.CARDS
        set(value) {
            if (field == value) return
            field = value
            @Suppress("NotifyDataSetChanged")
            notifyDataSetChanged() // the view type of every row changes
        }

    override fun getItemViewType(position: Int) = mode.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (ViewMode.entries[viewType]) {
            ViewMode.CARDS -> CardHolder(ItemChannelCardBinding.inflate(inflater, parent, false))
            ViewMode.GRID -> GridHolder(ItemChannelGridBinding.inflate(inflater, parent, false))
            ViewMode.LIST -> RowHolder(ItemChannelRowBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is CardHolder -> holder.bind(item)
            is GridHolder -> holder.bind(item)
            is RowHolder -> holder.bind(item)
        }
    }

    private fun item(holder: RecyclerView.ViewHolder): ChannelItem? =
        holder.bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION }?.let { getItem(it) }

    private inner class CardHolder(val b: ItemChannelCardBinding) : RecyclerView.ViewHolder(b.root) {
        init {
            b.root.setOnClickListener { item(this)?.let(callbacks.onOpen) }
            b.btnExplore.setOnClickListener { item(this)?.let(callbacks.onOpen) }
            b.btnFavorite.setOnClickListener { item(this)?.let(callbacks.onFavorite) }
            b.btnMore.setOnClickListener { item(this)?.let(callbacks.onMore) }
        }

        fun bind(item: ChannelItem) {
            b.thumb.bind(item.logoUrl)
            b.tvName.text = item.displayName
            b.tvCategories.text = item.categoryLine
            b.tvQuality.text = item.quality?.uppercase()
            b.tvQuality.show(item.quality != null)
            b.btnFavorite.isSelected = item.isFavorite
        }
    }

    private inner class GridHolder(val b: ItemChannelGridBinding) : RecyclerView.ViewHolder(b.root) {
        init {
            b.root.setOnClickListener { item(this)?.let(callbacks.onOpen) }
            b.btnOpen.setOnClickListener { item(this)?.let(callbacks.onOpen) }
            b.btnFavorite.setOnClickListener { item(this)?.let(callbacks.onFavorite) }
            b.btnMore.setOnClickListener { item(this)?.let(callbacks.onMore) }
        }

        fun bind(item: ChannelItem) {
            b.thumb.bind(item.logoUrl)
            b.tvName.text = item.displayName
            b.tvCategories.text = item.categoryLine
            b.tvQuality.text = item.quality?.uppercase()
            b.tvQuality.show(item.quality != null)
            b.btnFavorite.isSelected = item.isFavorite
            val watched = b.root.context.watchedText(item, showWatched)
            b.tvWatched.text = watched
            b.tvWatched.show(watched != null)
        }
    }

    private inner class RowHolder(val b: ItemChannelRowBinding) : RecyclerView.ViewHolder(b.root) {
        init {
            b.root.setOnClickListener { item(this)?.let(callbacks.onOpen) }
            b.btnFavorite.setOnClickListener { item(this)?.let(callbacks.onFavorite) }
            b.btnMore.setOnClickListener { item(this)?.let(callbacks.onMore) }
        }

        fun bind(item: ChannelItem) {
            b.thumb.bind(item.logoUrl)
            b.tvName.text = item.displayName
            b.tvCategories.text = item.categoryLine
            b.tvQuality.text = item.quality?.uppercase()
            b.tvQuality.show(item.quality != null)
            b.btnFavorite.isSelected = item.isFavorite
            val watched = b.root.context.watchedText(item, showWatched)
            b.tvWatched.text = watched
            b.tvWatched.show(watched != null)
        }
    }
}
