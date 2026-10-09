package com.iptvplayer.xtreamiptv.myiptvpro.ADS.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.AppAnalyticsEvents
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement.ADSAppManage
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.activities.language.LanguageActivity
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.activities.permissions.PermissionActivity
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.activities.privacy.PrivacyPolicySetting
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.launcher.common.Utils
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.launcher.intro.Intro1Activity
import com.iptvplayer.xtreamiptv.myiptvpro.container
import com.iptvplayer.xtreamiptv.myiptvpro.ui.main.MainActivity
import com.iptvplayer.xtreamiptv.myiptvpro.ui.player.PlayerActivity
import com.iptvplayer.xtreamiptv.myiptvpro.ui.player.PlayerSource
import com.iptvplayer.xtreamiptv.myiptvpro.utils.Constance
import com.iptvplayer.xtreamiptv.myiptvpro.utils.LocaleHelper
import com.iptvplayer.xtreamiptv.myiptvpro.utils.PermissionManager
import com.iptvplayer.xtreamiptv.myiptvpro.utils.SharedPreferenceManager
import android.util.Log
import kotlin.jvm.java

object StartupFlowManager {

    private const val PREFS_NAME = "startup_flow_prefs"
    private const val KEY_FLOW_STEPS = "flow_steps"
    private const val KEY_FLOW_INDEX = "flow_index"
    private const val KEY_FLOW_ACTIVE = "flow_active"

    private val DEFAULT_FLOW_KEYS = listOf(
        "Language",
        "OnBoarding",
        "PolicyScreen",
        "Home"
    )

    @JvmStatic
    fun saveFlow(context: Context, rawSteps: List<String>) {
        val parsed = parseSteps(rawSteps)
        val stepsToSave = if (parsed.isEmpty()) parseSteps(DEFAULT_FLOW_KEYS) else parsed
        getPrefs(context).edit()
            .putString(KEY_FLOW_STEPS, stepsToSave.joinToString("|") { it.key })
            .putInt(KEY_FLOW_INDEX, 0)
            .putBoolean(KEY_FLOW_ACTIVE, false)
            .apply()
    }

    @JvmStatic
    fun startStartupFlow(activity: Activity) {
        getPrefs(activity).edit()
            .putInt(KEY_FLOW_INDEX, 0)
            .putBoolean(KEY_FLOW_ACTIVE, true)
            .apply()
        advanceFlow(activity, fromIndex = 0)
    }

    @JvmStatic
    @Synchronized
    fun completeStep(activity: Activity, completedStep: StartupFlowStep) {
        if (!isStartupFlowActive(activity)) {
            navigateToHome(activity)
            return
        }

        val flow = getSavedFlow(activity)
        val completedIndex = flow.indexOf(completedStep)
        val currentIndex = getCurrentIndex(activity)

        // Another activity (e.g. LauncherHome vs DefaultApp) already advanced past this step.
        if (completedIndex >= 0 && currentIndex > completedIndex) {
            if (!activity.isFinishing) {
                activity.finish()
            }
            return
        }

        val nextIndex = if (completedIndex >= 0) {
            completedIndex + 1
        } else {
            currentIndex + 1
        }
        // Persist progress before starting next screen to avoid duplicate navigation.
        saveCurrentIndex(activity, nextIndex)
        advanceFlow(activity, fromIndex = nextIndex)
    }

    /** Close the app when user presses back on an onboarding / startup screen. */
    @JvmStatic
    fun exitAppOnBackPress(activity: Activity) {
        clearFlowState(activity)
        activity.finishAffinity()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.finishAndRemoveTask()
        }
    }

    @JvmStatic
    fun completeOnboarding(activity: Activity) {
        Utils.setIntroCompleted(activity.applicationContext, true)
        AppAnalyticsEvents.track(activity, AppAnalyticsEvents.ONBOARDING_COMPLETE)
        completeStep(activity, StartupFlowStep.ONBOARDING)
    }

    @JvmStatic
    fun isStartupFlowActive(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_FLOW_ACTIVE, false)
    }


    private fun advanceFlow(activity: Activity, fromIndex: Int) {
        if (activity.isFinishing) return

        val flow = getSavedFlow(activity)
        if (flow.isEmpty()) {
            navigateToHome(activity)
            return
        }

        var index = fromIndex.coerceAtLeast(0)
        while (index < flow.size) {
            val step = flow[index]
            if (shouldSkipStep(activity, step)) {
                index++
                continue
            }
            saveCurrentIndex(activity, index)
            navigateToStep(activity, step)
            return
        }

        navigateToHome(activity)
    }

    private fun shouldSkipStep(context: Context, step: StartupFlowStep): Boolean {
        return when (step) {
            StartupFlowStep.LANGUAGE -> {
                val isLoggedIn = SharedPreferenceManager.getBoolean(context, Constance.IS_LOG_IN, false)
                val hasStoredLocale = Utils.getAppLanguageNew(context).isNotEmpty()
                isLoggedIn && hasStoredLocale
            }

            StartupFlowStep.ONBOARDING -> Utils.getIntroCompleted(context)

            // This app has no dialer features: the (phone/default-dialer) Permission screen is never shown.
            StartupFlowStep.PERMISSION -> true

            StartupFlowStep.POLICY_SCREEN ->
                SharedPreferenceManager.getBoolean(context, Constance.PRIVACY_DATA_CONSENT, false)

            StartupFlowStep.HOME -> false
        }
    }

    private fun navigateToStep(activity: Activity, step: StartupFlowStep) {
        val intent = when (step) {
            StartupFlowStep.LANGUAGE -> {
                Intent(activity, LanguageActivity::class.java)
            }

            StartupFlowStep.ONBOARDING -> {
                LocaleHelper.setLocale(activity, LocaleHelper.readSavedLanguage(activity))
                ADSAppManage.FastStart = true
                Intent(activity, Intro1Activity::class.java)
            }

            StartupFlowStep.PERMISSION -> {
                LocaleHelper.setLocale(activity, LocaleHelper.readSavedLanguage(activity))
                Intent(activity, PermissionActivity::class.java)
            }

            StartupFlowStep.POLICY_SCREEN -> {
                LocaleHelper.setLocale(activity, LocaleHelper.readSavedLanguage(activity))
                Intent(activity, PrivacyPolicySetting::class.java)
            }

            StartupFlowStep.HOME -> {
                navigateToHome(activity)
                return
            }
        }

        activity.startActivity(intent)
        activity.overridePendingTransition(android.R.anim.fade_in, R.anim.stay)
        activity.finish()
    }

    /** End of the startup flow: MainActivity, then the last channel when "Resume last channel" is on. */
    private fun navigateToHome(activity: Activity) {
        LocaleHelper.setLocale(activity, LocaleHelper.readSavedLanguage(activity))
        // Clear first so nothing re-enters the flow.
        clearFlowState(activity)
        val prefs = activity.container.preferences
        prefs.onboardingDone = true

        activity.startActivity(
            Intent(activity, homeDestinationClass()).apply {
                addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
                )
            }
        )
        if (prefs.resumeLastChannel) {
            prefs.lastChannel()?.let { (channel, playlistId) ->
                val source = if (playlistId != null) PlayerSource.Playlist(playlistId, "") else PlayerSource.Single
                activity.startActivity(PlayerActivity.intent(activity, source, channel))
            }
        }
        activity.overridePendingTransition(0, 0)
        activity.finishAffinity()
    }

    @JvmStatic
    fun homeDestinationClass(): Class<out Activity> = MainActivity::class.java

    /** No-op kept for [com.iptvplayer.xtreamiptv.myiptvpro.ADS.launcher.utils.LocaleAwareAppCompatActivity] callers. */
    @JvmStatic
    fun maybeShowPendingDefaultButtonAd(activity: Activity) {
    }

    private fun parseSteps(rawSteps: List<String>): List<StartupFlowStep> {
        return rawSteps.mapNotNull { StartupFlowStep.fromKey(it) }
    }

    private fun getSavedFlow(context: Context): List<StartupFlowStep> {
        val raw = getPrefs(context).getString(KEY_FLOW_STEPS, null)
        if (raw.isNullOrBlank()) {
            return parseSteps(DEFAULT_FLOW_KEYS)
        }
        return raw.split("|").mapNotNull { StartupFlowStep.fromKey(it) }
    }

    private fun getCurrentIndex(context: Context): Int =
        getPrefs(context).getInt(KEY_FLOW_INDEX, 0)

    private fun saveCurrentIndex(context: Context, index: Int) {
        getPrefs(context).edit().putInt(KEY_FLOW_INDEX, index).apply()
    }

    private fun clearFlowState(context: Context) {
        getPrefs(context).edit()
            .putBoolean(KEY_FLOW_ACTIVE, false)
            .putInt(KEY_FLOW_INDEX, 0)
            .apply()
    }

    private fun getPrefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
