package com.iptvplayer.xtreamiptv.myiptvpro.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.iptvplayer.xtreamiptv.myiptvpro.ADS.AppAnalyticsEvents
import java.lang.ref.WeakReference

/**
 * Tracks live activities so background screens can pick up theme/locale changes
 * before the user navigates back to them.
 * Also fires screen-wise analytics when an activity is displayed.
 */
object ActivityStackTracker : Application.ActivityLifecycleCallbacks {

    private val activities = mutableListOf<WeakReference<Activity>>()

    @JvmStatic
    fun register(application: Application) {
        application.registerActivityLifecycleCallbacks(this)
    }

    private fun cleanup() {
        activities.removeAll { it.get() == null }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        cleanup()
        activities.add(WeakReference(activity))
    }

    override fun onActivityDestroyed(activity: Activity) {
        activities.removeAll { it.get() == null || it.get() === activity }
    }

    override fun onActivityStarted(activity: Activity) = Unit

    override fun onActivityResumed(activity: Activity) {
        AppAnalyticsEvents.trackActivityDisplayed(activity)
    }

    override fun onActivityPaused(activity: Activity) = Unit

    override fun onActivityStopped(activity: Activity) = Unit

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
}
