package com.iptvplayer.xtreamiptv.myiptvpro.ADS.launcher.utils

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.launcher.interfaces.ActivityPrintContextProvider

/**
 * Wraps activity with saved language. On resume, recreates only THIS screen when language
 * changed — does not bring other activities to the foreground (Samsung / launcher safe).
 */
open class LocaleAwareAppCompatActivity : AppCompatActivity(), ActivityPrintContextProvider {

    private var appliedLanguage = ""
    private var knownLanguageEpoch = 0
    private var knownThemeEpoch = 0

    override lateinit var printServiceContext: Context
//        private set

    override fun attachBaseContext(newBase: Context) {
        printServiceContext = newBase
        super.attachBaseContext(LocaleHelper.wrap(newBase))
    }

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
        if (overrideConfiguration != null) {
                LocaleHelper.applyLocaleToConfiguration(this, overrideConfiguration)
        }
        super.applyOverrideConfiguration(overrideConfiguration)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        syncAppCompatLocalesIfNeeded()
        appliedLanguage = LocaleHelper.readSavedLanguage(this)
        knownLanguageEpoch = LocaleHelper.getLanguageEpoch(this)
        knownThemeEpoch = ThemeManager.syncThemeEpoch(this)
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        refreshLocaleIfNeeded()
        refreshThemeIfNeeded()
        com.iptvplayer.xtreamiptv.myiptvpro.ADS.navigation.StartupFlowManager
            .maybeShowPendingDefaultButtonAd(this)
    }

    /** LanguageActivity disables this so Done → finish is not interrupted by recreate. */
    protected open fun shouldAutoRefreshLocale(): Boolean = true

    protected open fun shouldAutoRefreshTheme(): Boolean = true

    /**
     * The default-launcher activity returns false: pushing locales through AppCompat (the framework
     * LocaleManager on Android 13+) reconfigures its task and fails with "Can't change activity type once
     * set" (standard -> home). Its language still comes from the LocaleHelper.wrap() context.
     */
    protected open fun canSyncAppCompatLocales(): Boolean = true

    /**
     * AppCompat still inflates @string/ layout text from [AppCompatDelegate] locales on API 30–32.
     * Context wrap alone is not enough until the app process restarts — sync before super.onCreate().
     */
    private fun syncAppCompatLocalesIfNeeded() {
        val saved = LocaleHelper.readSavedLanguage(this)
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        val currentTag = if (currentLocales.isEmpty) {
            ""
        } else {
            LanguageManager.normalizeLanguageCode(
                currentLocales[0]?.toLanguageTag() ?: ""
            )
        }
        val changeRequired = currentTag != saved
        val homeTask = changeRequired && (!canSyncAppCompatLocales() || isInHomeTask())
        Log.d(
            LOCALE_TAG,
            "activity=${javaClass.simpleName} saved='$saved' current='$currentTag' " +
                "changeRequired=$changeRequired homeTask=$homeTask"
        )
        if (!changeRequired) return
        if (homeTask) {
            // On Android 13+ AppCompat forwards this to the framework LocaleManager, which throws
            // "Can't change activity type once set" (activityType=home) for a Home/launcher task.
            // The saved language is NOT reset: this screen is still drawn in it through the
            // LocaleHelper.wrap() context above, and AppCompat is synced from the next normal task.
            Log.d(LOCALE_TAG, "application locale update deferred (Home task)")
            return
        }
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(saved))
    }

    /** True when this activity lives in the default-launcher (Home type) task, where locales can't be changed. */
    private fun isInHomeTask(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return false
        val appTasks = getSystemService(ActivityManager::class.java)?.appTasks ?: return false
        return appTasks.any { task ->
            val info = task.taskInfo ?: return@any false
            info.taskId == taskId && info.baseIntent.hasCategory(Intent.CATEGORY_HOME)
        }
    }

    private fun refreshLocaleIfNeeded() {
        if (!shouldAutoRefreshLocale() || isFinishing || isDestroyed) return
        syncAppCompatLocalesIfNeeded()
        val saved = LocaleHelper.readSavedLanguage(this)
        val epoch = LocaleHelper.getLanguageEpoch(this)
        if (saved != appliedLanguage || epoch != knownLanguageEpoch) {
            appliedLanguage = saved
            knownLanguageEpoch = epoch
            recreate()
        }
    }

    private fun refreshThemeIfNeeded() {
        if (!shouldAutoRefreshTheme() || isFinishing || isDestroyed) return
        if (ThemeManager.shouldRecreateForTheme(this, knownThemeEpoch)) {
            knownThemeEpoch = ThemeManager.syncThemeEpoch(this)
            recreate()
        }
    }
}

private const val LOCALE_TAG = "LocaleSync"
