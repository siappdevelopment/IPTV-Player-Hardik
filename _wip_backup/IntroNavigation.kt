package com.iptvplayer.xtreamiptv.myiptvpro.ui.onboarding

import android.app.Activity
import android.content.Intent
import com.iptvplayer.xtreamiptv.myiptvpro.container
import com.iptvplayer.xtreamiptv.myiptvpro.ui.main.MainActivity

/** Page-to-page routing for the intro flow; the page count comes from Remote Config (0..3). */
object IntroNavigation {
    private val pages = listOf(Intro1Activity::class.java, Intro2Activity::class.java, Intro3Activity::class.java)

    /** Starts the first intro page, or completes onboarding when the flow is switched off. */
    fun open(activity: Activity) {
        if (activity.container.adsConfig.onboardingCount > 0) {
            activity.startActivity(Intent(activity, pages[0]))
            activity.finish()
        } else {
            complete(activity)
        }
    }

    /** Called from page [current] (1-based) once its Continue / Next is done. */
    fun next(activity: Activity, current: Int) {
        if (current >= activity.container.adsConfig.onboardingCount) {
            complete(activity)
        } else {
            activity.startActivity(Intent(activity, pages[current]))
            activity.finish()
        }
    }

    private fun complete(activity: Activity) {
        activity.container.preferences.onboardingDone = true
        activity.startActivity(Intent(activity, MainActivity::class.java))
        activity.finish()
    }
}
