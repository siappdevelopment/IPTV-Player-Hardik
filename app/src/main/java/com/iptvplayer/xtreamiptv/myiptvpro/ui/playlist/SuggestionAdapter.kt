package com.iptvplayer.xtreamiptv.myiptvpro.ui.playlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ItemSuggestionBinding

/** A ready-made public playlist the user can pick instead of typing a link. */
data class Suggestion(@param:StringRes val nameRes: Int, val url: String)

val DEFAULT_SUGGESTIONS = listOf(
    Suggestion(R.string.suggestion_comedy, "https://iptv-org.github.io/iptv/categories/comedy.m3u"),
    Suggestion(R.string.suggestion_lifestyle, "https://iptv-org.github.io/iptv/categories/lifestyle.m3u"),
    Suggestion(R.string.suggestion_movies, "https://iptv-org.github.io/iptv/categories/movies.m3u"),
    Suggestion(R.string.suggestion_sports, "https://iptv-org.github.io/iptv/categories/sports.m3u"),
)

/** Radio-style list: the row whose URL equals the field is selected. */
class SuggestionAdapter(
    private val items: List<Suggestion>,
    private val onPicked: (Suggestion) -> Unit,
) : RecyclerView.Adapter<SuggestionAdapter.Holder>() {

    var selectedUrl: String? = null
        set(value) {
            if (field == value) return
            field = value
            @Suppress("NotifyDataSetChanged")
            notifyDataSetChanged()
        }

    inner class Holder(val b: ItemSuggestionBinding) : RecyclerView.ViewHolder(b.root) {
        init {
            b.root.setOnClickListener {
                bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION }?.let { onPicked(items[it]) }
            }
        }

        fun bind(item: Suggestion) {
            val selected = item.url == selectedUrl
            b.root.isSelected = selected
            b.ivRadio.isSelected = selected
            b.tvName.setText(item.nameRes)
            b.tvUrl.text = item.url.removePrefix("https://")
        }
    }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(ItemSuggestionBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(items[position])
}
