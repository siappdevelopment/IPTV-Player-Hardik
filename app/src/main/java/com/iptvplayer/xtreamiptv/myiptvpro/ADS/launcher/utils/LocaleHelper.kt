package com.iptvplayer.xtreamiptv.myiptvpro.ADS.launcher.utils

import android.content.Context
import android.content.res.Configuration
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement.ADSAppManage
import java.util.Locale

/**
 * Locale via SharedPreferences + context wrap.
 * Avoids AppCompat setApplicationLocales (breaks task stack on Samsung Android 15 / launcher apps).
 */
object LocaleHelper {

    @JvmStatic
    fun readSavedLanguage(context: Context): String {
        return LanguageManager.normalizeLanguageCode(
            com.iptvplayer.xtreamiptv.myiptvpro.ADS.launcher.utils.Utils.getAppLanguageNew(
                context
            )
        )
    }

    @JvmStatic
    fun wrap(context: Context): Context {
        return com.iptvplayer.xtreamiptv.myiptvpro.ADS.launcher.utils.Utils.wrapContext(context)
    }

    @JvmStatic
    fun getLanguageEpoch(context: Context): Int {
        return SharedPreferenceManager.getInt(context, Constance.LANGUAGE_EPOCH, 0)
    }

    @JvmStatic
    fun bumpLanguageEpoch(context: Context) {
        SharedPreferenceManager.putInt(
            context,
            Constance.LANGUAGE_EPOCH,
            getLanguageEpoch(context) + 1
        )
    }

    /** Keep locale on configurations that activities override for theme/uiMode. */
    @JvmStatic
    fun applyLocaleToConfiguration(context: Context, configuration: Configuration) {
        val appContext = context.applicationContext ?: context
        val languageCode = readSavedLanguage(appContext)
        val locale = Locale.forLanguageTag(languageCode)
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
    }

    /**
     * Persist language and apply to application resources immediately.
     * Live screens refresh via [LocaleAwareAppCompatActivity] onResume / attachBaseContext.
     */
    @JvmStatic
    fun setLocale(context: Context, languageCode: String) {
        val normalized = LanguageManager.normalizeLanguageCode(languageCode)
        ADSAppManage.blockAppOpenAd(3000)
        com.iptvplayer.xtreamiptv.myiptvpro.ADS.launcher.utils.Utils.setAppLanguageNew(
            context.applicationContext,
            normalized
        )
        // Keep AppPreferences in sync so BaseActivity/MainActivity do not reset locale on open.
        AppPreferences.saveAppLanguage(
            context.applicationContext,
            normalized,
        )
        com.iptvplayer.xtreamiptv.myiptvpro.ADS.launcher.utils.Utils.applyStoredLocale(context.applicationContext)
        bumpLanguageEpoch(context.applicationContext)
    }
}
