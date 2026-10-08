package com.iptvplayer.xtreamiptv.myiptvpro.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ItemCategoryChipBinding

/** [value] is the category token used for filtering; empty means "All". */
data class CategoryChip(val label: String, val value: String, val count: Int, @DrawableRes val icon: Int)

class CategoryChipAdapter(
    private val onSelected: (CategoryChip) -> Unit,
) : ListAdapter<CategoryChip, CategoryChipAdapter.Holder>(Diff) {

    var selectedValue: String = ""
        set(value) {
            if (field == value) return
            field = value
            @Suppress("NotifyDataSetChanged")
            notifyDataSetChanged()
        }

    inner class Holder(val b: ItemCategoryChipBinding) : RecyclerView.ViewHolder(b.root) {
        init {
            b.root.setOnClickListener {
                bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION }?.let { onSelected(getItem(it)) }
            }
        }

        fun bind(chip: CategoryChip) {
            b.root.isSelected = chip.value == selectedValue
            b.ivIcon.setImageResource(chip.icon)
            b.tvLabel.text = chip.label
            b.tvCount.text = chip.count.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(ItemCategoryChipBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(getItem(position))

    private object Diff : DiffUtil.ItemCallback<CategoryChip>() {
        override fun areItemsTheSame(a: CategoryChip, b: CategoryChip) = a.value == b.value
        override fun areContentsTheSame(a: CategoryChip, b: CategoryChip) = a == b
    }
}
