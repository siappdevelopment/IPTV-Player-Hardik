package com.iptvplayer.xtreamiptv.myiptvpro.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ItemCompactRowBinding
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ItemSearchCountBinding
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ItemSearchPlaylistBinding
import com.iptvplayer.xtreamiptv.myiptvpro.domain.model.ChannelItem
import com.iptvplayer.xtreamiptv.myiptvpro.domain.model.PlaylistItem
import com.iptvplayer.xtreamiptv.myiptvpro.ui.common.Categories

class SearchAdapter(
    private val onChannel: (ChannelItem) -> Unit,
    private val onFavorite: (ChannelItem) -> Unit,
    private val onPlaylist: (PlaylistItem) -> Unit,
) : ListAdapter<SearchRow, RecyclerView.ViewHolder>(Diff) {

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is SearchRow.Count -> TYPE_COUNT
        is SearchRow.PlaylistResult -> TYPE_PLAYLIST
        is SearchRow.ChannelResult -> TYPE_CHANNEL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_COUNT -> CountHolder(ItemSearchCountBinding.inflate(inflater, parent, false))
            TYPE_PLAYLIST -> PlaylistHolder(ItemSearchPlaylistBinding.inflate(inflater, parent, false))
            else -> ChannelHolder(ItemCompactRowBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val row = getItem(position)) {
            is SearchRow.Count -> (holder as CountHolder).bind(row)
            is SearchRow.PlaylistResult -> (holder as PlaylistHolder).bind(row.playlist)
            is SearchRow.ChannelResult -> (holder as ChannelHolder).bind(row.channel)
        }
    }

    private inner class CountHolder(val b: ItemSearchCountBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(row: SearchRow.Count) {
            b.tvCount.text = b.root.resources.getQuantityString(R.plurals.search_result_count, row.total, row.total)
        }
    }

    private inner class PlaylistHolder(val b: ItemSearchPlaylistBinding) : RecyclerView.ViewHolder(b.root) {
        private var current: PlaylistItem? = null

        init {
            b.root.setOnClickListener { current?.let(onPlaylist) }
        }

        fun bind(item: PlaylistItem) {
            current = item
            b.ivIcon.setImageResource(Categories.playlistIcon(item.name))
            b.tvName.text = item.name
            val count = b.root.resources.getQuantityString(R.plurals.channel_count, item.channelCount, item.channelCount)
            b.tvMeta.text = b.root.context.getString(R.string.search_playlist_result, count)
        }
    }

    private inner class ChannelHolder(val b: ItemCompactRowBinding) : RecyclerView.ViewHolder(b.root) {
        private var current: ChannelItem? = null

        init {
            b.root.setOnClickListener { current?.let(onChannel) }
            b.btnFavorite.setOnClickListener { current?.let(onFavorite) }
        }

        fun bind(item: ChannelItem) {
            current = item
            b.thumb.bind(item.logoUrl)
            b.tvName.text = item.displayName
            b.tvCategories.text = item.categoryLine
            b.btnFavorite.isSelected = item.isFavorite
        }
    }

    private object Diff : DiffUtil.ItemCallback<SearchRow>() {
        override fun areItemsTheSame(a: SearchRow, b: SearchRow) = when {
            a is SearchRow.Count && b is SearchRow.Count -> true
            a is SearchRow.PlaylistResult && b is SearchRow.PlaylistResult -> a.playlist.id == b.playlist.id
            a is SearchRow.ChannelResult && b is SearchRow.ChannelResult ->
                a.channel.id == b.channel.id && a.channel.streamUrl == b.channel.streamUrl
            else -> false
        }

        override fun areContentsTheSame(a: SearchRow, b: SearchRow) = a == b
    }

    private companion object {
        const val TYPE_COUNT = 0
        const val TYPE_PLAYLIST = 1
        const val TYPE_CHANNEL = 2
    }
}
