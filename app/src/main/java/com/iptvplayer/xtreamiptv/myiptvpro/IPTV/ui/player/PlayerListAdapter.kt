package com.iptvplayer.xtreamiptv.myiptvpro.IPTV.ui.player

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.domain.model.ChannelItem
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.ui.common.ChannelDiff
import com.iptvplayer.xtreamiptv.myiptvpro.IPTV.utils.show
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ItemCompactRowBinding
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ItemPlayerHeaderBinding

/**
 * The channel queue below the video; the playing row is highlighted. When [adEvery] > 0 a native
 * ad row is placed after every [adEvery] channels (never at the end). [onPlay] still receives the
 * index into the channel list, not the adapter position.
 */
class PlayerListAdapter(
    private val onPlay: (position: Int) -> Unit,
    private val onFavorite: (ChannelItem) -> Unit,
    private val adEvery: Int = 0,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val plainUpdates = AdapterListUpdateCallback(this)

    // Inserts/removes shift every ad row below them, so with ads on they refresh the whole list.
    @Suppress("NotifyDataSetChanged")
    private val updates = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) =
            if (adEvery <= 0) plainUpdates.onInserted(position, count) else notifyDataSetChanged()

        override fun onRemoved(position: Int, count: Int) =
            if (adEvery <= 0) plainUpdates.onRemoved(position, count) else notifyDataSetChanged()

        override fun onMoved(fromPosition: Int, toPosition: Int) =
            if (adEvery <= 0) plainUpdates.onMoved(fromPosition, toPosition) else notifyDataSetChanged()

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            if (adEvery <= 0) plainUpdates.onChanged(position, count, payload)
            else for (i in position until position + count) notifyItemChanged(positionOf(i), payload)
        }
    }

    private val differ = AsyncListDiffer(updates, AsyncDifferConfig.Builder(ChannelDiff).build())

    val currentList: List<ChannelItem> get() = differ.currentList

    fun submitList(list: List<ChannelItem>?) = differ.submitList(list)

    private fun isAd(position: Int) = adEvery > 0 && (position + 1) % (adEvery + 1) == 0

    /** Index into the channel list for an adapter position. */
    private fun dataIndex(position: Int) = if (adEvery <= 0) position else position - position / (adEvery + 1)

    /** Adapter position of the channel at [index] in the channel list. */
    fun positionOf(index: Int) = if (adEvery <= 0) index else index + index / adEvery

    private fun getItem(position: Int): ChannelItem = currentList[dataIndex(position)]

    override fun getItemCount(): Int {
        val n = currentList.size
        return if (adEvery <= 0 || n == 0) n else n + (n - 1) / adEvery
    }

    override fun getItemViewType(position: Int) = if (isAd(position)) VIEW_TYPE_AD else VIEW_TYPE_CHANNEL

    /** The playing channel, tracked by identity so moves and removals stay correct. */
    private var current: ChannelItem? = null

    fun setCurrent(item: ChannelItem?) {
        val old = current
        current = item
        if (old != null && item != null && old.sameChannel(item)) return
        val list = currentList
        list.indexOfFirst { old != null && it.sameChannel(old) }.takeIf { it >= 0 }?.let { notifyItemChanged(positionOf(it)) }
        list.indexOfFirst { item != null && it.sameChannel(item) }.takeIf { it >= 0 }?.let { notifyItemChanged(positionOf(it)) }
    }

    private fun ChannelItem.sameChannel(other: ChannelItem) = id == other.id && streamUrl == other.streamUrl

    private class AdHolder(view: View) : RecyclerView.ViewHolder(view)

    inner class Holder(val b: ItemCompactRowBinding) : RecyclerView.ViewHolder(b.root) {
        init {
            b.root.setOnClickListener {
                bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION }?.let { onPlay(dataIndex(it)) }
            }
            b.btnFavorite.setOnClickListener {
                bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION }?.let { onFavorite(getItem(it)) }
            }
        }

        fun bind(item: ChannelItem) {
            val active = current?.sameChannel(item) == true
            b.root.isSelected = active
            b.ivActive.show(active)
            b.thumb.bind(item.logoUrl)
            b.tvName.text = item.displayName
            b.tvCategories.text = item.categoryLine
            b.btnFavorite.isSelected = item.isFavorite
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_AD) {
            AdHolder(inflater.inflate(R.layout.reso_layout_bottom_native, parent, false))
        } else {
            Holder(ItemCompactRowBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Holder) holder.bind(getItem(position))
    }

    private companion object {
        const val VIEW_TYPE_CHANNEL = 0
        const val VIEW_TYPE_AD = 1
    }
}

/** Fixed header above the list (outside the scrolling list): name, status, quality, categories, favorite pill, list title. */
class PlayerHeader(private val b: ItemPlayerHeaderBinding, onFavorite: () -> Unit) {

    data class State(
        val channel: ChannelItem?,
        val status: PlaybackStatus,
        val listTitle: String,
        val listCount: String,
    )

    init {
        b.btnFavorite.setOnClickListener { onFavorite() }
    }

    fun update(s: State) {
        val ctx = b.root.context
        val channel = s.channel
        b.tvName.text = channel?.displayName.orEmpty()
        b.tvCategories.text = channel?.categoryLine.orEmpty()
        b.tvQuality.text = channel?.quality?.uppercase()
        b.tvQuality.show(channel?.quality != null)

        val (textRes, colorRes) = when (s.status) {
            PlaybackStatus.PLAYING -> R.string.player_status_live to R.color.color_success
            PlaybackStatus.PAUSED -> R.string.player_status_paused to R.color.color_warning
            PlaybackStatus.UNAVAILABLE, PlaybackStatus.NETWORK_ERROR -> R.string.player_status_offline to R.color.color_error
            PlaybackStatus.CONNECTING, PlaybackStatus.BUFFERING -> R.string.player_status_connecting to R.color.color_accent_light
        }
        val color = ContextCompat.getColor(ctx, colorRes)
        b.tvStatus.setText(textRes)
        b.tvStatus.setTextColor(color)
        TextViewCompat.setCompoundDrawableTintList(b.tvStatus, ColorStateList.valueOf(color))

        val favorite = channel?.isFavorite == true
        b.btnFavorite.isSelected = favorite
        b.tvFavorite.setText(if (favorite) R.string.player_saved else R.string.player_favorite)
        b.tvListTitle.text = ctx.getString(R.string.player_in_list, s.listTitle)
        b.tvListCount.text = s.listCount
    }
}
