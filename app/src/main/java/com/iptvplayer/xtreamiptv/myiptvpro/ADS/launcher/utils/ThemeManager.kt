package com.iptvplayer.xtreamiptv.myiptvpro.ADS.launcher.utils

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.iptvplayer.xtreamiptv.myiptvpro.R

object ThemeManager {

    @JvmStatic
    fun getThemeEpoch(context: Context): Int {
        return SharedPreferenceManager.getInt(context, Constance.APP_THEME_EPOCH, 0)
    }

    @JvmStatic
    fun syncThemeEpoch(context: Context): Int {
        return getThemeEpoch(context)
    }

    @JvmStatic
    fun shouldRecreateForTheme(context: Context, knownEpoch: Int): Boolean {
        return knownEpoch != getThemeEpoch(context)
    }
}
