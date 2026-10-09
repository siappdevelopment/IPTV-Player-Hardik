package com.iptvplayer.xtreamiptv.myiptvpro.Theme

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.iptvplayer.xtreamiptv.myiptvpro.R

/**
 * STUB for the source app's calendar theming engine (Theme/ThemeManager.kt, 35 KB, not exported).
 * The startup flow only calls these three members. Replace the bodies with your own theming
 * (or leave as-is for a plain, un-themed flow).
 */
object ThemeManager {

    /** LanguageActivity.onCreate: re-tint the screen. No-op = default colours from the layout. */
    fun applyTheme(activity: Activity) = Unit

    /** ADSUtilitis / VpnHelper loading + VPN dialogs. No-op = default dialog colours. */
    @JvmStatic
    fun applyDialog(root: View, color: Int) = Unit

    /** LanguageAdapter radio dot. */
    fun createThemedDotDrawable(
        context: Context,
        checked: Boolean,
        color: Int = ThemePreference.getPrimaryColor(context)
    ): Drawable {
        val resId = if (checked) R.drawable.ic_select_check_box else R.drawable.ic_unselect_check_box
        val drawable = ContextCompat.getDrawable(context, resId)?.mutate()
            ?: ColorDrawable(Color.TRANSPARENT)
        if (checked) DrawableCompat.setTint(drawable, color)
        return drawable
    }
}
