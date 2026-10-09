package com.iptvplayer.xtreamiptv.myiptvpro.ADS.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.models.LanguageModel
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.Theme.ThemeManager
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.LanguageDesignBinding

class LanguageAdapter(
    private val list: MutableList<LanguageModel>,
    private val onLanguageSelected: ((LanguageModel) -> Unit)? = null,
) : RecyclerView.Adapter<LanguageAdapter.LanguageDataHolder>() {

    private var selectedPosition = list.indexOfFirst { it.isSelected }

    override fun onCreateViewHolder(
        parent: ViewGroup, p1: Int
    ): LanguageDataHolder {
        val binding =
            LanguageDesignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LanguageDataHolder(binding)
    }

    override fun onBindViewHolder(
        holder: LanguageDataHolder, position: Int
    ) {
        val itemData = list[position]
        holder.setData(itemData, holder.bindingAdapterPosition)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun getSelectedLanguage(): LanguageModel {
        return list[selectedPosition]
    }

    fun getSelectedPosition(): Int {
        return selectedPosition
    }


    inner class LanguageDataHolder(private val binding: LanguageDesignBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setData(itemData: LanguageModel, bindingAdapterPosition: Int) {

            if (selectedPosition == -1 && bindingAdapterPosition == 0) {
                binding.lottiHandClick.visibility = View.VISIBLE
                if (!binding.lottiHandClick.isAnimating) {
                    binding.lottiHandClick.setAnimation(R.raw.hand_click)
                    binding.lottiHandClick.playAnimation()
                }
            } else {
                binding.lottiHandClick.visibility = View.GONE
                binding.lottiHandClick.cancelAnimation()
            }

            binding.ivFlag.setImageResource(itemData.flag)
            binding.tvLanguageNameLocal.text = binding.root.context.getString(itemData.nameLocalRes)
            binding.tvLanguageEnglish.text = binding.root.context.getString(itemData.nameEnglishRes)

            binding.rbLanguage.isChecked = itemData.isSelected
            binding.rbLanguage.background = ThemeManager.createThemedDotDrawable(
                binding.root.context,
                itemData.isSelected
            )

            binding.llLanguage.setOnClickListener {
                updateSelection(bindingAdapterPosition)
            }
            binding.rbLanguage.setOnClickListener {
                updateSelection(bindingAdapterPosition)
            }

        }

        private fun updateSelection(position: Int) {
            if (position == selectedPosition) {
                // Already selected, force UI to stay checked (prevent unselection)
                notifyItemChanged(position)
                return
            }

            val oldPosition = selectedPosition
            selectedPosition = position

            if (oldPosition == -1) {
                // If first time selection, hide animation on first item
                notifyItemChanged(0)
            } else {
                list[oldPosition].isSelected = false
                notifyItemChanged(oldPosition)
            }

            list[position].isSelected = true
            notifyItemChanged(position)
            onLanguageSelected?.invoke(list[position])
        }

    }
}