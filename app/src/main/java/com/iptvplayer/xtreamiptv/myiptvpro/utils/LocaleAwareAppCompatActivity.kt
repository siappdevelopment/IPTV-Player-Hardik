package com.iptvplayer.xtreamiptv.myiptvpro.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement.ADSAppManage

/**
 * Wraps activity with saved language. On resume, recreates only THIS screen when language
 * changed — does not bring other activities to the foreground (Samsung / launcher safe).
 */
open class LocaleAwareAppCompatActivity : AppCompatActivity() {

    private var appliedLanguage = ""
    private var knownLanguageEpoch = 0
    private var applyingLocaleToResources = false

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.wrap(newBase))
    }

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
        if (overrideConfiguration != null) {
            LocaleHelper.applyLocaleToConfiguration(this, overrideConfiguration)
        }
        super.applyOverrideConfiguration(overrideConfiguration)
    }

    override fun getResources(): Resources {
        val resources = super.getResources()
        if (applyingLocaleToResources) return resources
        val appContext = applicationContext ?: ADSAppManage.getApp() ?: return resources
        applyingLocaleToResources = true
        try {
            LocaleHelper.applyLocaleToResources(
                resources,
                LocaleHelper.readSavedLanguage(appContext)
            )
        } finally {
            applyingLocaleToResources = false
        }
        return resources
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        LocaleHelper.applyToContext(this)
        appliedLanguage = LocaleHelper.readSavedLanguage(this)
        knownLanguageEpoch = LocaleHelper.getLanguageEpoch(this)
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        refreshLocaleIfNeeded()
        com.iptvplayer.xtreamiptv.myiptvpro.ADS.navigation.StartupFlowManager
            .maybeShowPendingDefaultButtonAd(this)
    }

    /** LanguageActivity disables this so Done → finish is not interrupted by recreate. */
    protected open fun shouldAutoRefreshLocale(): Boolean = true

    protected fun refreshLocaleIfNeeded() {
        if (!shouldAutoRefreshLocale() || isFinishing || isDestroyed) return
        val saved = LocaleHelper.readSavedLanguage(this)
        val epoch = LocaleHelper.getLanguageEpoch(this)
        if (saved != appliedLanguage || epoch != knownLanguageEpoch) {
            appliedLanguage = saved
            knownLanguageEpoch = epoch
            LocaleHelper.applyToContext(this)
            recreate()
        }
    }
}
