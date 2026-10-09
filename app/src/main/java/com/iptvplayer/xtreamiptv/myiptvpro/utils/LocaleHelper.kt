package com.iptvplayer.xtreamiptv.myiptvpro.utils

import android.content.Context
import android.content.res.Configuration
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement.ADSAppManage
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.launcher.common.Utils
import java.util.Locale

/**
 * Locale via SharedPreferences + context wrap.
 * Avoids AppCompat setApplicationLocales (breaks task stack on Samsung Android 15 / launcher apps).
 */
object LocaleHelper {

    @JvmStatic
    fun readSavedLanguage(context: Context): String {
        return LanguageManager.normalizeLanguageCode(Utils.getAppLanguageNew(context))
    }

    @JvmStatic
    fun wrap(context: Context): Context {
        return Utils.wrapContext(context)
    }

    @JvmStatic
    fun getLanguageEpoch(context: Context): Int {
        return SharedPreferenceManager.getInt(context, Constance.LANGUAGE_EPOCH, 0)
    }

    @JvmStatic
    fun bumpLanguageEpoch(context: Context) {
        val appContext = context.applicationContext ?: context
        appContext.getSharedPreferences(Constance.PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt(Constance.LANGUAGE_EPOCH, getLanguageEpoch(appContext) + 1)
            .commit()
    }

    /** Keep locale on configurations that activities override for theme/uiMode. */
    @JvmStatic
    fun applyLocaleToConfiguration(context: Context, configuration: Configuration) {
        val appContext = context.applicationContext ?: ADSAppManage.getApp() ?: context
        val languageCode = readSavedLanguage(appContext)
        val locale = Locale.forLanguageTag(languageCode)
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
    }

    /** Force AppCompat / activity Resources to the saved language (XML @string inflation). */
    @JvmStatic
    fun applyLocaleToResources(resources: android.content.res.Resources, languageCode: String) {
        val locale = Locale.forLanguageTag(languageCode.ifBlank { "en" })
        val config = resources.configuration
        val currentTag = if (config.locales.size() == 0) {
            ""
        } else {
            config.locales[0]?.toLanguageTag().orEmpty()
        }
        if (currentTag.equals(locale.toLanguageTag(), ignoreCase = true)) return
        Locale.setDefault(locale)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    @JvmStatic
    fun applyToContext(context: Context) {
        Utils.applyStoredLocale(context)
        applyLocaleToResources(context.resources, readSavedLanguage(context))
    }

    /**
     * Persist language and apply to application resources immediately.
     * Live screens refresh via [LocaleAwareAppCompatActivity] onResume / attachBaseContext.
     */
    @JvmStatic
    fun setLocale(context: Context, languageCode: String) {
        val normalized = LanguageManager.normalizeLanguageCode(languageCode)
        ADSAppManage.blockAppOpenAd(3000)
        Utils.setAppLanguageNew(context.applicationContext, normalized)
        Utils.applyStoredLocale(context.applicationContext)
        bumpLanguageEpoch(context.applicationContext)
    }
}
