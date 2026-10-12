package com.iptvplayer.xtreamiptv.myiptvpro.Main.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.Main.model.LanguageModel

class LanguageAdapter(
    private val list: List<LanguageModel>
) : RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {

    private var selectedPosition = 0

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.icLang)
        val ivSelected: ImageView = view.findViewById(R.id.ivSelected)
        val name: TextView = view.findViewById(R.id.tvLanguage)
        val native: TextView = view.findViewById(R.id.tvSubText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_language, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.icon.setImageResource(item.icon)
        holder.name.text = item.name
        holder.native.text = item.nativeName

        if (position == selectedPosition) {
            holder.ivSelected.visibility = View.VISIBLE
            holder.ivSelected.setImageResource(R.drawable.ic_selected_lang)
            holder.itemView.setBackgroundResource(R.drawable.language_selected_bg)
        } else {
            holder.ivSelected.visibility = View.GONE
            holder.itemView.setBackgroundResource(R.drawable.items_bg)
        }

        holder.itemView.setOnClickListener {
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition == RecyclerView.NO_POSITION) return@setOnClickListener

            val previousPosition = selectedPosition
            selectedPosition = currentPosition

            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
        }
    }
}