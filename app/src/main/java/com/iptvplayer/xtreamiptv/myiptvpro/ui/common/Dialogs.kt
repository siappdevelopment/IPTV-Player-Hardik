package com.iptvplayer.xtreamiptv.myiptvpro.ui.common

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.DialogConfirmBinding

/** The design's centred delete / clear confirmation. */
object Dialogs {

    /** Shows [root] (a layout with its own rounded background) as a centred card with the design's 24 side margin. */
    fun showCard(context: Context, root: android.view.View, cancelable: Boolean = true): AlertDialog {
        val dialog = MaterialAlertDialogBuilder(context).setView(root).setCancelable(cancelable).setBackgroundInsetStart(0).setBackgroundInsetEnd(0).setBackgroundInsetTop(0).setBackgroundInsetBottom(0).create()
        dialog.show()
        val margin = context.resources.getDimensionPixelSize(R.dimen._24sdp)
        dialog.window?.apply {
            setLayout(context.resources.displayMetrics.widthPixels - 2 * margin, ViewGroup.LayoutParams.WRAP_CONTENT)
            setDimAmount(DIM_CARD)
        }
        return dialog
    }

    private const val DIM_CARD = 0.7f

    fun confirm(
        context: Context,
        title: String,
        description: String,
        @StringRes confirmText: Int,
        onConfirm: () -> Unit,
    ): AlertDialog {
        val binding = DialogConfirmBinding.inflate(LayoutInflater.from(context))
        val dialog = MaterialAlertDialogBuilder(context).setView(binding.root).setBackgroundInsetStart(0).setBackgroundInsetEnd(0).setBackgroundInsetTop(0).setBackgroundInsetBottom(0).create()
        binding.tvTitle.text = title
        binding.tvDesc.text = description
        binding.btnConfirm.setText(confirmText)
        binding.btnCancel.setOnClickListener { dialog.dismiss() }
        binding.btnConfirm.setOnClickListener {
            dialog.dismiss()
            onConfirm()
        }
        dialog.show()
        val margin = context.resources.getDimensionPixelSize(R.dimen._28sdp)
        dialog.window?.setLayout(
            context.resources.displayMetrics.widthPixels - 2 * margin,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
        return dialog
    }
}
