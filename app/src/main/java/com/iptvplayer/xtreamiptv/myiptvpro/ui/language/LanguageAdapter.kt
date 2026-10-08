package com.iptvplayer.xtreamiptv.myiptvpro.ui.language

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.iptvplayer.xtreamiptv.myiptvpro.data.prefs.AppLanguage
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ItemLanguageBinding

class LanguageAdapter(
    private val onSelect: (AppLanguage) -> Unit,
) : ListAdapter<AppLanguage, LanguageAdapter.Holder>(Diff) {

    var selectedCode: String = "en"
        set(value) {
            if (field == value) return
            field = value
            @Suppress("NotifyDataSetChanged")
            notifyDataSetChanged()
        }

    inner class Holder(val b: ItemLanguageBinding) : RecyclerView.ViewHolder(b.root) {
        init {
            b.root.setOnClickListener {
                bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION }?.let { onSelect(getItem(it)) }
            }
        }

        fun bind(language: AppLanguage) {
            val selected = language.code == selectedCode
            // selection state propagates to the tile and radio drawables
            b.root.isSelected = selected
            b.tvShort.isSelected = selected
            b.ivRadio.isSelected = selected
            b.tvShort.text = language.short
            b.tvNative.text = language.nativeName
            b.tvEnglish.text = language.englishName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(ItemLanguageBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(getItem(position))

    private object Diff : DiffUtil.ItemCallback<AppLanguage>() {
        override fun areItemsTheSame(a: AppLanguage, b: AppLanguage) = a.code == b.code
        override fun areContentsTheSame(a: AppLanguage, b: AppLanguage) = a == b
    }
}
