package com.iptvplayer.xtreamiptv.myiptvpro.ADS.launcher.utils

import android.content.Context

object AppPreferences {
    private const val PREFS_NAME = "pdf_reader_prefs"


    fun getBoolean(context: Context, key: String, default: Boolean = false): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(key, default)
    }


    private const val KEY_APP_THEME = "app_theme"

    fun getAppTheme(context: Context): Int {
        // 0 -> System Default, 1 -> Light Theme, 2 -> Dark Theme
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_APP_THEME, 1)
    }

    fun setAppTheme(context: Context, theme: Int) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_APP_THEME, theme)
            .apply()
    }


    private const val KEY_APP_LANGUAGE = "app_language"

    fun saveAppLanguage(context: Context, languageCode: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_APP_LANGUAGE, languageCode)
            .commit() // commit() is sync — important before recreate()
    }

    private const val KEY_UNLOCK_SAVING_BEHAVIOR = "unlock_saving_behavior"
    private const val KEY_IS_LOG_IN = "is_log_in"

}
