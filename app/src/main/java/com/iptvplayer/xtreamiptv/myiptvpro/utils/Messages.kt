package com.iptvplayer.xtreamiptv.myiptvpro.utils

import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import com.google.android.material.snackbar.Snackbar
import com.iptvplayer.xtreamiptv.myiptvpro.R

/** The design's toast: a rounded dark pill with a coloured icon, shown above the bottom bar. */
object Messages {

    fun show(
        anchor: View,
        text: String,
        @DrawableRes icon: Int = R.drawable.ic_check_circle,
        @ColorRes tint: Int = R.color.color_success,
        above: View? = null,
    ) {
        val snackbar = Snackbar.make(anchor, text, Snackbar.LENGTH_SHORT)
        val context = anchor.context
        snackbar.view.background = ContextCompat.getDrawable(context, R.drawable.bg_toast)
        snackbar.view.setPadding(0, 0, 0, 0)
        snackbar.setBackgroundTint(ContextCompat.getColor(context, R.color.transparent))
        snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)?.apply {
            setTextColor(ContextCompat.getColor(context, R.color.color_text_primary))
            setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen._14sdp))
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            compoundDrawablePadding = resources.getDimensionPixelSize(R.dimen._10sdp)
            setCompoundDrawablesRelativeWithIntrinsicBounds(icon, 0, 0, 0)
            TextViewCompat.setCompoundDrawableTintList(
                this,
                android.content.res.ColorStateList.valueOf(ContextCompat.getColor(context, tint)),
            )
            val h = resources.getDimensionPixelSize(R.dimen._14sdp)
            val v = resources.getDimensionPixelSize(R.dimen._12sdp)
            setPadding(h, v, h + h / 2, v)
        }
        above?.let { snackbar.setAnchorView(it) }
        snackbar.show()
    }

    fun show(
        anchor: View,
        @StringRes text: Int,
        @DrawableRes icon: Int = R.drawable.ic_check_circle,
        @ColorRes tint: Int = R.color.color_success,
        above: View? = null,
    ) = show(anchor, anchor.context.getString(text), icon, tint, above)

    fun error(anchor: View, @StringRes text: Int, above: View? = null) =
        show(anchor, text, R.drawable.ic_error, R.color.color_error, above)

    fun neutral(anchor: View, @StringRes text: Int, @DrawableRes icon: Int, above: View? = null) =
        show(anchor, text, icon, R.color.color_text_secondary, above)
}
