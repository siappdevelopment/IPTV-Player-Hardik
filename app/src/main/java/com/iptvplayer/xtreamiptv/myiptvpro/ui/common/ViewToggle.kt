package com.iptvplayer.xtreamiptv.myiptvpro.ui.common

import android.view.View
import com.iptvplayer.xtreamiptv.myiptvpro.data.prefs.ViewMode
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ViewViewToggleBinding
import com.iptvplayer.xtreamiptv.myiptvpro.utils.show

/** Drives the segmented cards / grid / list switch. Only the modes in [available] are shown. */
class ViewToggle(
    private val binding: ViewViewToggleBinding,
    private val available: List<ViewMode>,
    private val onSelected: (ViewMode) -> Unit,
) {
    private val buttons: Map<ViewMode, View> = mapOf(
        ViewMode.CARDS to binding.btnViewCards,
        ViewMode.GRID to binding.btnViewGrid,
        ViewMode.LIST to binding.btnViewList,
    )

    init {
        buttons.forEach { (mode, view) ->
            view.show(mode in available)
            view.setOnClickListener { onSelected(mode) }
        }
    }

    fun select(mode: ViewMode) {
        buttons.forEach { (m, view) -> view.isSelected = m == mode }
    }
}
