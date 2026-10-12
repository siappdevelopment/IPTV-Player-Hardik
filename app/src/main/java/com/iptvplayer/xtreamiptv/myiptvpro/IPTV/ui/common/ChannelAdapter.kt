package com.iptvplayer.xtreamiptv.myiptvpro.IPTV.ui.common

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.data.prefs.ViewMode
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.domain.model.ChannelItem
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.utils.relativeTime
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.utils.show
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ItemChannelCardBinding
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ItemChannelGridBinding
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ItemChannelRowBinding

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
 * cards or rows. When [showWatched] is on, history rows show "Watched …". When [adEveryRows] > 0 a
 * native ad row is placed after every [adEveryRows] channel rows (never at the end); in grid mode
 * a row is [gridSpan] channels. Ask [isAd] so a grid can give those rows the full width.
 */
class ChannelAdapter(
    private val callbacks: ChannelCallbacks,
    private val showWatched: Boolean = false,
    private val adEveryRows: Int = 0,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /** Channels per row in grid mode; set to the grid's span count. */
    var gridSpan: Int = 1

    /** Channels between two ads (0 = no ads). */
    private val adEvery: Int
        get() = if (adEveryRows <= 0) 0 else adEveryRows * if (mode == ViewMode.GRID) gridSpan.coerceAtLeast(1) else 1

    private val plainUpdates = AdapterListUpdateCallback(this)

    // Inserts/removes shift every ad row below them, so with ads on they refresh the whole list.
    @Suppress("NotifyDataSetChanged")
    private val updates = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) =
            if (adEvery == 0) plainUpdates.onInserted(position, count) else notifyDataSetChanged()

        override fun onRemoved(position: Int, count: Int) =
            if (adEvery == 0) plainUpdates.onRemoved(position, count) else notifyDataSetChanged()

        override fun onMoved(fromPosition: Int, toPosition: Int) =
            if (adEvery == 0) plainUpdates.onMoved(fromPosition, toPosition) else notifyDataSetChanged()

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            if (adEvery == 0) plainUpdates.onChanged(position, count, payload)
            else for (i in position until position + count) notifyItemChanged(i + i / adEvery, payload)
        }
    }

    private val differ = AsyncListDiffer(updates, AsyncDifferConfig.Builder(ChannelDiff).build())

    fun submitList(list: List<ChannelItem>?) = differ.submitList(list)

    var mode: ViewMode = ViewMode.CARDS
        set(value) {
            if (field == value) return
            field = value
            @Suppress("NotifyDataSetChanged")
            notifyDataSetChanged() // the view type of every row changes
        }

    fun isAd(position: Int) = adEvery > 0 && (position + 1) % (adEvery + 1) == 0

    /** Index into the channel list for an adapter position (ad rows shift everything below them). */
    private fun dataIndex(position: Int) = if (adEvery == 0) position else position - position / (adEvery + 1)

    private fun getItem(position: Int): ChannelItem = differ.currentList[dataIndex(position)]

    override fun getItemCount(): Int {
        val n = differ.currentList.size
        return if (adEvery == 0 || n == 0) n else n + (n - 1) / adEvery
    }

    override fun getItemViewType(position: Int) = if (isAd(position)) VIEW_TYPE_AD else mode.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == VIEW_TYPE_AD) {
            return AdHolder(inflater.inflate(R.layout.reso_layout_medium_native, parent, false))
        }
        return when (ViewMode.entries[viewType]) {
            ViewMode.CARDS -> CardHolder(ItemChannelCardBinding.inflate(inflater, parent, false))
            ViewMode.GRID -> GridHolder(ItemChannelGridBinding.inflate(inflater, parent, false))
            ViewMode.LIST -> RowHolder(ItemChannelRowBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CardHolder -> holder.bind(getItem(position))
            is GridHolder -> holder.bind(getItem(position))
            is RowHolder -> holder.bind(getItem(position))
        }
    }

    private fun item(holder: RecyclerView.ViewHolder): ChannelItem? =
        holder.bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION }?.let { getItem(it) }

    private class AdHolder(view: View) : RecyclerView.ViewHolder(view)

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

    private companion object {
        const val VIEW_TYPE_AD = 100
    }
}
