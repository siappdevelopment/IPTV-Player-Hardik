package com.iptvplayer.xtreamiptv.myiptvpro.ui.player

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ItemCompactRowBinding
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ItemPlayerHeaderBinding
import com.iptvplayer.xtreamiptv.myiptvpro.domain.model.ChannelItem
import com.iptvplayer.xtreamiptv.myiptvpro.ui.common.ChannelDiff
import com.iptvplayer.xtreamiptv.myiptvpro.utils.show

/** The channel queue below the video; the playing row is highlighted. */
class PlayerListAdapter(
    private val onPlay: (position: Int) -> Unit,
    private val onFavorite: (ChannelItem) -> Unit,
) : ListAdapter<ChannelItem, PlayerListAdapter.Holder>(ChannelDiff) {

    /** The playing channel, tracked by identity so moves and removals stay correct. */
    private var current: ChannelItem? = null

    fun setCurrent(item: ChannelItem?) {
        val old = current
        current = item
        if (old != null && item != null && old.sameChannel(item)) return
        val list = currentList
        list.indexOfFirst { old != null && it.sameChannel(old) }.takeIf { it >= 0 }?.let(::notifyItemChanged)
        list.indexOfFirst { item != null && it.sameChannel(item) }.takeIf { it >= 0 }?.let(::notifyItemChanged)
    }

    private fun ChannelItem.sameChannel(other: ChannelItem) = id == other.id && streamUrl == other.streamUrl

    inner class Holder(val b: ItemCompactRowBinding) : RecyclerView.ViewHolder(b.root) {
        init {
            b.root.setOnClickListener {
                bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION }?.let(onPlay)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(ItemCompactRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(getItem(position))
}

/** Single header row above the list: name, status, quality, categories, favorite pill, list title. */
class PlayerHeaderAdapter(private val onFavorite: () -> Unit) : RecyclerView.Adapter<PlayerHeaderAdapter.Holder>() {

    data class State(
        val channel: ChannelItem?,
        val status: PlaybackStatus,
        val listTitle: String,
        val listCount: String,
    )

    private var state: State? = null

    fun update(value: State) {
        state = value
        notifyItemChanged(0)
    }

    override fun getItemCount() = 1

    inner class Holder(val b: ItemPlayerHeaderBinding) : RecyclerView.ViewHolder(b.root) {
        init {
            b.btnFavorite.setOnClickListener { onFavorite() }
        }

        fun bind(s: State) {
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
            b.btnFavorite.setText(if (favorite) R.string.player_saved else R.string.player_favorite)
            b.tvListTitle.text = ctx.getString(R.string.player_in_list, s.listTitle)
            b.tvListCount.text = s.listCount
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(ItemPlayerHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: Holder, position: Int) {
        state?.let(holder::bind)
    }
}
